/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.voxel;

import com.google.common.base.Preconditions;
import java.util.Optional;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.OperationFailedRuntimeException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssignerFactory;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracterFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;

/**
 * Voxel-data that is bounded to exist in a particular bounding-box in an image.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@Accessors(fluent = true)
public class BoundedVoxels<T> {

    private static final Point3i ALL_ONES_2D = new Point3i(1, 1, 0);
    private static final Point3i ALL_ONES_3D = new Point3i(1, 1, 1);

    /** A bounding-box that associates voxels to a particular part of an image */
    @Getter private final BoundingBox boundingBox;

    /**
     * Voxel-data that fits inside the bounding-box (its extent is invariant with the extent of the
     * bounding-box).
     */
    @Getter private final Voxels<T> voxels;

    /** Extracts value from voxels using local coordinates */
    private final VoxelsExtracter<T> extracterLocal;

    /** Extracts value from voxels using local coordinates */
    private final VoxelsExtracter<T> extracterGlobal;

    /**
     * Creates voxels bounded to match the entire voxel-data at the origin.
     *
     * @param voxels voxel-data
     */
    public BoundedVoxels(Voxels<T> voxels) {
        this(new BoundingBox(voxels), voxels);
    }

    /**
     * Copy constructor
     *
     * @param source where to copy from
     */
    public BoundedVoxels(BoundedVoxels<T> source) {
        this(source.boundingBox(), source.voxels.duplicate());
    }

    /**
     * Creates voxels with a corresponding bounding box.
     *
     * @param boundingBox bounding-box
     * @param voxels voxels which must have the same extent as {@code boundingBox}
     */
    public BoundedVoxels(BoundingBox boundingBox, Voxels<T> voxels) {
        Preconditions.checkArgument(boundingBox.extent().equals(voxels.extent()));
        this.boundingBox = boundingBox;
        this.voxels = voxels;
        this.extracterLocal = voxels.extract();
        this.extracterGlobal = VoxelsExtracterFactory.atCorner(cornerMin(), voxels.extract());
    }

    public boolean equalsDeep(BoundedVoxels<?> other) {

        if (!boundingBox.equals(other.boundingBox)) {
            return false;
        }
        return voxels.equalsDeep(other.voxels);
    }

    /**
     * Replaces the voxels in the box.
     *
     * <p>This is an <b>immutable</b> operation, and a new {@link Voxels} are created.
     *
     * @param voxelsToAssign voxels to be assigned.
     * @return a newly created replacement voxels but with an identical bounding-box
     */
    public BoundedVoxels<T> replaceVoxels(Voxels<T> voxelsToAssign) {
        Preconditions.checkArgument(voxelsToAssign.extent().equals(extent()));
        return new BoundedVoxels<>(boundingBox, voxelsToAssign);
    }

    public BoundedVoxels<T> growToZ(int sz, VoxelsFactoryTypeBound<T> factory) {
        assert (this.boundingBox.extent().z() == 1);
        assert (this.voxels.extent().z() == 1);

        BoundingBox boxNew =
                new BoundingBox(boundingBox.cornerMin(), boundingBox.extent().duplicateChangeZ(sz));

        Voxels<T> buffer = factory.createInitialized(boxNew.extent());

        Extent extent = this.boundingBox.extent();
        BoundingBox boxSrc = new BoundingBox(extent);

        // we copy in one by one
        for (int z = 0; z < buffer.extent().z(); z++) {
            extracterLocal.boxCopyTo(boxSrc, buffer, new BoundingBox(new Point3i(0, 0, z), extent));
        }

        return new BoundedVoxels<>(boxNew, buffer);
    }

    /**
     * Grow bounding-box by 1 pixel in all directions
     *
     * @param do3D 3-dimensions (true) or 2-dimensions (false)
     * @param clipRegion a region to clip to, which we can't grow beyond
     * @return a bounding box: the crnr is the relative-position to the current bounding box, the
     *     extent is absolute
     */
    public BoundingBox dilate(boolean do3D, Optional<Extent> clipRegion) {
        Point3i allOnes = do3D ? ALL_ONES_3D : ALL_ONES_2D;
        return createGrownBoxAbsolute(allOnes, allOnes, clipRegion);
    }

    /**
     * Grows buffer of the object-mask in positive and negative directions by a certain amount.
     *
     * <p>This operation is <i>immutable</i>.
     *
     * @param growthNegative
     * @param growthPositive
     * @param clipRegion if defined, clips the buffer to this region
     * @param factory
     * @return the grown object-mask with newly-created buffers
     */
    public BoundedVoxels<T> growBuffer(
            Point3i growthNegative,
            Point3i growthPositive,
            Optional<Extent> clipRegion,
            VoxelsFactoryTypeBound<T> factory)
            throws OperationFailedException {

        if (clipRegion.isPresent() && !clipRegion.get().contains(this.boundingBox)) {
            throw new OperationFailedException(
                    "Cannot grow the bounding-box of the object-mask, as it is already outside the clipping region.");
        }

        Extent extent = this.voxels.extent();

        BoundingBox grownBox = createGrownBoxRelative(growthNegative, growthPositive, clipRegion);

        // We allocate a new buffer
        Voxels<T> bufferNew = factory.createInitialized(grownBox.extent());
        extracterLocal.boxCopyTo(
                new BoundingBox(extent), bufferNew, new BoundingBox(grownBox.cornerMin(), extent));

        // We create a new bounding box
        BoundingBox box =
                new BoundingBox(
                        Point3i.immutableSubtract(
                                this.boundingBox.cornerMin(), grownBox.cornerMin()),
                        grownBox.extent());

        return new BoundedVoxels<>(box, bufferNew);
    }

    /**
     * Creates a scaled-version (in XY dimensions only) of the current bounding-box
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param scaleFactor what to scale X and Y dimensions by?
     * @param interpolator means of interpolating between pixels
     * @param clipTo an extent which the object-masks should always fit inside after scaling (to
     *     catch any rounding errors that push the bounding box outside the scene-boundary)
     * @return a new bounded-voxels box of specified size containing scaled contents of the existing
     */
    public BoundedVoxels<T> scale(
            ScaleFactor scaleFactor, Interpolator interpolator, Optional<Extent> clipTo) {

        // Construct a new bounding-box, clipping if necessary
        BoundingBox boundingBoxScaled =
                clipTo.map(extent -> boundingBox.scaleClipTo(scaleFactor, extent))
                        .orElseGet(() -> boundingBox.scale(scaleFactor));

        Voxels<T> voxelsOut =
                extracterLocal.resizedXY(
                        boundingBoxScaled.extent().x(),
                        boundingBoxScaled.extent().y(),
                        interpolator);

        return new BoundedVoxels<>(boundingBoxScaled, voxelsOut);
    }

    /**
     * A maximum-intensity projection (flattens in z dimension)
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @return newly-created bounded-voxels flattened in Z dimension.
     */
    public BoundedVoxels<T> projectMax() {
        return new BoundedVoxels<>(boundingBox.flattenZ(), extracterLocal.projectMax());
    }

    public BoundedVoxels<T> duplicate() {
        return new BoundedVoxels<>(this);
    }

    /**
     * A slice buffer with <i>local</i> coordinates i.e. relative to the bounding-box corner
     *
     * @param sliceIndexRelative sliceIndex (z) relative to the bounding-box of the object-mask
     * @return the buffer
     */
    public T sliceBufferLocal(int sliceIndexRelative) {
        return voxels.sliceBuffer(sliceIndexRelative);
    }

    /**
     * A slice buffer with <i>global</i> coordinates
     *
     * @param sliceIndexGlobal sliceIndex (z) in global coordinates
     * @return the buffer
     */
    public T sliceBufferGlobal(int sliceIndexGlobal) {
        return voxels.sliceBuffer(sliceIndexGlobal - boundingBox().cornerMin().z());
    }

    public Extent extent() {
        return voxels.extent();
    }

    /**
     * Creates an box with a subrange of the slices.
     *
     * <p>This will always reuse the existing voxel-buffers..
     *
     * @param zMin minimum z-slice index, inclusive.
     * @param zMax maximum z-slice index, inclusive.
     * @param factory factory to use to create new voxels.
     * @return a newly created box for the slice-range requested.
     * @throws CreateException
     */
    public BoundedVoxels<T> regionZ(int zMin, int zMax, VoxelsFactoryTypeBound<T> factory)
            throws CreateException {

        if (!boundingBox.contains().z(zMin)) {
            throw new CreateException("zMin outside range");
        }
        if (!boundingBox.contains().z(zMax)) {
            throw new CreateException("zMax outside range");
        }

        int relZ = zMin - boundingBox.cornerMin().z();

        BoundingBox target =
                new BoundingBox(
                        boundingBox.cornerMin().duplicateChangeZ(zMin),
                        boundingBox.extent().duplicateChangeZ(zMax - zMin + 1));

        SubrangeVoxelAccess<T> voxelAccess = new SubrangeVoxelAccess<>(relZ, target.extent(), this);
        return new BoundedVoxels<>(target, factory.create(voxelAccess));
    }

    /**
     * A (sub-)region of the voxels.
     *
     * <p>The region may some smaller portion of the voxels, or the voxels in their entirety.
     *
     * <p>It should <b>never</b> be larger than the voxels.
     *
     * @see VoxelsExtracter#region
     * @param box bounding-box in absolute coordinates.
     * @param reuseIfPossible if true the existing box will be reused if possible, otherwise a new
     *     box is always created.
     * @return bounded0voxels corresponding to the requested region, either newly-created or reused
     * @throws CreateException
     */
    public BoundedVoxels<T> region(BoundingBox box, boolean reuseIfPossible)
            throws CreateException {

        if (!boundingBox.contains().box(box)) {
            throw new CreateException("Source box does not contain target box");
        }

        return new BoundedVoxels<>(box, extracterGlobal.region(box, reuseIfPossible));
    }

    /**
     * Like {@link #region} but only expects a bounding-box that intersects at least partially.
     *
     * <p>This is a weakened condition compared to {@link #region}.
     *
     * <p>The region outputted will have the same size and coordinates as the bounding-box, but with
     * the correct voxel-values for the part within the voxels. Any other voxels are set to {@code
     * voxelValueForRest}.
     *
     * <p>A new voxel-buffer is always created for this operation i.e. the existing box is never
     * reused like sometimes in {@link #region}..
     *
     * @param box bounding-box in absolute coordinates, that must at least partially intersect with
     *     the current bounds.
     * @param voxelValueForRest a voxel-value for the parts of the buffer not covered by the
     *     intersection.
     * @return newly created voxels containing partially some parts of the existing voxels and other
     *     regions.
     * @throws CreateException if the boxes do not intersect
     */
    public BoundedVoxels<T> regionIntersecting(BoundingBox box, int voxelValueForRest)
            throws CreateException {

        Optional<BoundingBox> boxIntersect = boundingBox.intersection().with(box);
        if (!boxIntersect.isPresent()) {
            throw new CreateException(
                    "Requested bounding-box does not intersect with current bounds");
        }

        Voxels<T> bufNew = voxels.factory().createInitialized(box.extent());

        // We can rely on the newly created voxels being 0 by default, otherwise we must update.
        if (voxelValueForRest != 0) {
            voxels.assignValue(voxelValueForRest).toAll();
        }

        extracterGlobal.boxCopyTo(
                boxIntersect.get(), bufNew, boxIntersect.get().relativePositionToBox(box));

        return new BoundedVoxels<>(box, bufNew);
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent should be unchanged
     * in value)
     *
     * <p>This is an <b>immutable</b> operation, but the existing voxel-buffers are reused in the
     * new object.
     *
     * @return a new object-mask with the updated bounding box
     */
    public BoundedVoxels<T> mapBoundingBoxPreserveExtent(BoundingBox boundingBoxToAssign) {

        if (!boundingBoxToAssign.extent().equals(boundingBox.extent())) {
            throw new OperationFailedRuntimeException(
                    "The extent changed while mapping bounding-box, which is not allowed.");
        }

        return new BoundedVoxels<>(boundingBoxToAssign, voxels);
    }

    /**
     * Extracts a particular slice.
     *
     * <p>This is an <b>immutable</b> operation, but the voxels-buffer for the slice is reused.
     *
     * @param sliceIndex which slice to extract (z) in global coordinates
     * @return the extracted-slice (bounded)
     */
    public BoundedVoxels<T> extractSlice(int sliceIndex) {

        int zRelative = sliceIndex - boundingBox().cornerMin().z();

        BoundingBox boxFlattened = boundingBox.flattenZ();

        Voxels<T> slice = extracterLocal.slice(zRelative);
        return new BoundedVoxels<>(boxFlattened, slice);
    }

    public ReadableTuple3i cornerMin() {
        return boundingBox.cornerMin();
    }

    public void replaceSlice(int sliceIndexToUpdate, VoxelBuffer<T> bufferToAssign) {
        voxels.replaceSlice(sliceIndexToUpdate, bufferToAssign);
    }

    public VoxelsArithmetic arithmetic() {
        return voxels.arithmetic();
    }

    /**
     * Assigns a value to a bounded-voxels accepting GLOBAL coordinates for objects, boxes etc.
     *
     * @param valueToAssign value to assign
     * @return an assigner that expects global co-ordinates
     */
    public VoxelsAssigner assignValue(int valueToAssign) {
        return VoxelsAssignerFactory.shiftBackBy(
                voxels.assignValue(valueToAssign), boundingBox.cornerMin());
    }

    /** Extracts value from voxels using <i>global</i> coordinates */
    public final VoxelsExtracter<T> extract() {
        return extracterGlobal;
    }

    /**
     * Creates a grown bounding-box relative to this current box (absolute coordinates)
     *
     * @param neg how much to grow in the negative direction
     * @param pos how much to grow in the negative direction
     * @param clipRegion a region to clip to, which we can't grow beyond
     * @return a bounding box: the crnr is the relative-position to the current bounding box, the
     *     extent is absolute
     */
    private BoundingBox createGrownBoxAbsolute(
            Point3i neg, Point3i pos, Optional<Extent> clipRegion) {
        BoundingBox relBox = createGrownBoxRelative(neg, pos, clipRegion);
        return relBox.reflectThroughOrigin().shiftBy(boundingBox.cornerMin());
    }

    /**
     * Creates a grown bounding-box relative to this current box (relative coordinates)
     *
     * @param neg how much to grow in the negative direction
     * @param pos how much to grow in the negative direction
     * @param clipRegion a region to clip to, which we can't grow beyond
     * @return a bounding box: the corner is the relative-position to the current bounding box
     *     (multipled by -1), the extent is absolute
     */
    private BoundingBox createGrownBoxRelative(
            Point3i neg, Point3i pos, Optional<Extent> clipRegion) {

        Point3i negClip =
                new Point3i(
                        clipNegative(boundingBox.cornerMin().x(), neg.x()),
                        clipNegative(boundingBox.cornerMin().y(), neg.y()),
                        clipNegative(boundingBox.cornerMin().z(), neg.z()));

        ReadableTuple3i boxMax = boundingBox.calculateCornerMax();

        ReadableTuple3i maxPossible;
        if (clipRegion.isPresent()) {
            maxPossible = clipRegion.get().asTuple();
        } else {
            maxPossible = new Point3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        Point3i growBy =
                new Point3i(
                        clipPositive(boxMax.x(), pos.x(), maxPossible.x()) + negClip.x(),
                        clipPositive(boxMax.y(), pos.y(), maxPossible.y()) + negClip.y(),
                        clipPositive(boxMax.z(), pos.z(), maxPossible.z()) + negClip.z());
        return new BoundingBox(negClip, this.voxels.extent().growBy(growBy));
    }

    // Considers growing in the negative direction from crnr by neg increments
    //  returns the maximum number of increments that are allowed without leading
    //  to a bounding box that is <0
    private static int clipNegative(int corner, int negative) {
        int diff = corner - negative;
        if (diff > 0) {
            return negative;
        } else {
            return negative + diff;
        }
    }

    // Considers growing in the positive direction from crnr by neg increments
    //  returns the maximum number of increments that are allowed without leading
    //  to a bounding box that is >= max
    private static int clipPositive(int corner, int positive, int max) {
        int sum = corner + positive;
        if (sum < max) {
            return positive;
        } else {
            return positive - (sum - max + 1);
        }
    }

    public String toString() {
        return boundingBox.toString();
    }
}
