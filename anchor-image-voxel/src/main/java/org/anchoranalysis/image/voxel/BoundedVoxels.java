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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.OperationFailedRuntimeException;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssignerFactory;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracterFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * {@link Voxels} that exist at a particular bounding-box within an image.
 *
 * <p>The {@link Voxels} must always have identical {@link Extent} to the bounding-box.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@Accessors(fluent = true)
public class BoundedVoxels<T> {

    private static final Point3i ALL_ONES_2D = new Point3i(1, 1, 0);
    private static final Point3i ALL_ONES_3D = new Point3i(1, 1, 1);

    /** A bounding-box that associates voxels to a particular part of an image. */
    @Getter private final BoundingBox boundingBox;

    /**
     * Voxel-data that fits inside the bounding-box (its extent is invariant with the extent of the
     * bounding-box).
     */
    @Getter private final Voxels<T> voxels;

    /**
     * Extracts value from voxels using local coordinates (coordinates relative to the
     * bounding-box).
     */
    private final VoxelsExtracter<T> extracterLocal;

    /**
     * Extracts value from voxels using local coordinates (coordinates relative to the
     * bounding-box).
     */
    private final VoxelsExtracter<T> extracterGlobal;

    /**
     * Creates voxels bounded to match the entire voxel-data at the origin.
     *
     * @param voxels voxel-data.
     */
    public BoundedVoxels(Voxels<T> voxels) {
        this(new BoundingBox(voxels.extent()), voxels);
    }

    /**
     * Copy constructor.
     *
     * <p>It is a deep copy. The voxel memory buffer is duplicated.
     *
     * @param source where to copy from.
     */
    public BoundedVoxels(BoundedVoxels<T> source) {
        this(source.boundingBox(), source.voxels.duplicate());
    }

    /**
     * Creates voxels with a corresponding bounding box.
     *
     * @param boundingBox bounding-box.
     * @param voxels voxels which must have the same extent as {@code boundingBox}.
     */
    public BoundedVoxels(BoundingBox boundingBox, Voxels<T> voxels) {
        Preconditions.checkArgument(boundingBox.extent().equals(voxels.extent()));
        this.boundingBox = boundingBox;
        this.voxels = voxels;
        this.extracterLocal = voxels.extract();
        this.extracterGlobal = VoxelsExtracterFactory.atCorner(cornerMin(), voxels.extract());
    }

    /**
     * Performs a <i>deep</i> equality check, that includes checking that each voxel has an
     * identical value.
     *
     * @param other the voxels to check with.
     * @return true iff the two {@link BoundedVoxels} instances have identical values and
     *     bounding-boxes.
     */
    public boolean equalsDeep(BoundedVoxels<?> other) {
        if (boundingBox.equals(other.boundingBox)) {
            return voxels.equalsDeep(other.voxels);
        } else {
            return false;
        }
    }

    /**
     * Replaces the voxels in the box.
     *
     * <p>This is an <b>immutable</b> operation, and a new {@link Voxels} are created.
     *
     * @param voxelsToAssign voxels to be assigned.
     * @return a newly created replacement voxels but with an identical bounding-box.
     */
    public BoundedVoxels<T> replaceVoxels(Voxels<T> voxelsToAssign) {
        Preconditions.checkArgument(voxelsToAssign.extent().equals(extent()));
        return new BoundedVoxels<>(boundingBox, voxelsToAssign);
    }

    /**
     * Grows a single z-sliced {@link BoundedVoxels} by duplicating the slice across the z-dimension
     * {@code sizeZ} number of times.
     *
     * @param sizeZ the size in the z-dimension to grow to i.e. the number of duplicated sizes.
     * @param factory a factory to use to create the duplicated voxels.
     * @return a new {@link BoundedVoxels} with an identical corner, but with a 3D bounding-box (and
     *     duplicated slices) instead of the previous 2D.
     * @throws OperationFailedException if the existing voxels aren't 2D (a single slice).
     */
    public BoundedVoxels<T> growToZ(int sizeZ, VoxelsFactoryTypeBound<T> factory)
            throws OperationFailedException {

        if (this.boundingBox.extent().z() != 1 && this.voxels.extent().z() != 1) {
            throw new OperationFailedException(
                    "This operation may only be used on a 2D voxels, but voxels are currently 3D.");
        }

        BoundingBox boxNew =
                new BoundingBox(
                        boundingBox.cornerMin(), boundingBox.extent().duplicateChangeZ(sizeZ));

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
     * Grow bounding-box by 1 pixel in all directions.
     *
     * @param do3D 3-dimensions (true) or 2-dimensions (false).
     * @param clipRegion a region to clip to, which we can't grow beyond.
     * @return a bounding box: the corner is the relative-position to the current bounding box, the
     *     extent is absolute.
     */
    public BoundingBox dilate(boolean do3D, Optional<Extent> clipRegion) {
        Point3i allOnes = do3D ? ALL_ONES_3D : ALL_ONES_2D;
        return createGrownBoxAbsolute(allOnes, allOnes, clipRegion);
    }

    /**
     * Grows the voxel buffer in the positive and negative directions by a certain amount.
     *
     * <p>This operation is <i>immutable</i>.
     *
     * @param growthNegative how much to grow in the <i>negative</i> direction (i.e. downards
     *     direction on an axis).
     * @param growthPositive how much to grow in the <i>positive</i> direction (i.e. upwards
     *     direction on an axis).
     * @param clipRegion if defined, clips the buffer to this region.
     * @param factory a factory to create {@link VoxelsFactoryTypeBound}.
     * @return a new {@link Voxels} with grown buffers.
     * @throws OperationFailedException if the voxels are located outside the clipping region.
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
     * Creates a scaled-version (in XY dimensions only) of the current bounding-box.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param scaleFactor what to scale X and Y dimensions by?
     * @param interpolator means of interpolating between pixels.
     * @param clipTo an extent which the object-masks should always fit inside after scaling (to
     *     catch any rounding errors that push the bounding box outside the scene-boundary).
     * @return a new {@link BoundedVoxels} box of specified size containing scaled contents of the
     *     existing.
     */
    public BoundedVoxels<T> scale(
            ScaleFactor scaleFactor, Interpolator interpolator, Optional<Extent> clipTo) {

        // Construct a new bounding-box, clipping if necessary
        BoundingBox boundingBoxScaled =
                clipTo.map(extent -> boundingBox.scaleClampTo(scaleFactor, extent))
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

    /**
     * A deep-copy of the current structure.
     *
     * @return a copy of the current data, including duplication of the memory buffers for the
     *     voxels.
     */
    public BoundedVoxels<T> duplicate() {
        return new BoundedVoxels<>(this);
    }

    /**
     * A slice buffer with <i>local</i> coordinates.
     *
     * @param sliceIndexRelative sliceIndex (z) relative to the associated bounding-box minimum
     *     corner.
     * @return the buffer.
     */
    public T sliceBufferLocal(int sliceIndexRelative) {
        return voxels.sliceBuffer(sliceIndexRelative);
    }

    /**
     * A slice buffer with <i>global</i> coordinates.
     *
     * @param sliceIndexGlobal sliceIndex (z) in global coordinates (relative to the image as a
     *     whole).
     * @return the buffer.
     */
    public T sliceBufferGlobal(int sliceIndexGlobal) {
        return voxels.sliceBuffer(sliceIndexGlobal - boundingBox().cornerMin().z());
    }

    /**
     * The size of the voxels across three dimensions, and also the size of the associated
     * bounding-box.
     *
     * @return the size.
     */
    public Extent extent() {
        return voxels.extent();
    }

    /**
     * Creates an box with a subrange of the slices.
     *
     * <p>This will always reuse the existing voxel-buffers.
     *
     * @param zMin minimum z-slice index, inclusive.
     * @param zMax maximum z-slice index, inclusive.
     * @param factory factory to use to create new voxels.
     * @return a newly created box for the slice-range requested.
     * @throws CreateException if {@code zMin} or {@code zMax} are outside the permitted range.
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
     * @throws CreateException if the source box does not contain the target box.
     */
    public BoundedVoxels<T> region(BoundingBox box, boolean reuseIfPossible)
            throws CreateException {

        if (!boundingBox.contains().box(box)) {
            throw new CreateException("The source box does not contain the target box");
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
     * reused like sometimes in {@link #region}.
     *
     * @param box bounding-box in absolute coordinates, that must at least partially intersect with
     *     the current bounds.
     * @param voxelValueForRest a voxel-value for the parts of the buffer not covered by the
     *     intersection.
     * @return newly created voxels containing partially some parts of the existing voxels and other
     *     regions.
     * @throws CreateException if the boxes do not intersect.
     */
    public BoundedVoxels<T> regionIntersecting(BoundingBox box, int voxelValueForRest)
            throws CreateException {

        Optional<BoundingBox> boxIntersect = boundingBox.intersection().with(box);
        if (!boxIntersect.isPresent()) {
            throw new CreateException(
                    "Requested bounding-box does not intersect with current bounds");
        }

        Voxels<T> bufferNew = voxels.factory().createInitialized(box.extent());

        // We can rely on the newly created voxels being 0 by default, otherwise we must update.
        if (voxelValueForRest != 0) {
            voxels.assignValue(voxelValueForRest).toAll();
        }

        extracterGlobal.boxCopyTo(
                boxIntersect.get(), bufferNew, boxIntersect.get().relativePositionToBox(box));

        return new BoundedVoxels<>(box, bufferNew);
    }

    /**
     * Applies a function to map the bounding-box to a new-value.
     *
     * <p>The {@link Extent} of the bounding-box should remain unchanged in value.
     *
     * <p>This is an <b>immutable</b> operation, but the existing voxel-buffers are reused in the
     * new object.
     *
     * @param boundingBoxToAssign the new bounding-box to assign.
     * @return a new object-mask with the updated bounding box.
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
     * @param sliceIndex which slice to extract (z) in global coordinates.
     * @return the extracted-slice (bounded).
     */
    public BoundedVoxels<T> extractSlice(int sliceIndex) {

        int zRelative = sliceIndex - boundingBox().cornerMin().z();

        BoundingBox boxFlattened = boundingBox.flattenZ();

        Voxels<T> slice = extracterLocal.slice(zRelative);
        return new BoundedVoxels<>(boxFlattened, slice);
    }

    /**
     * The minimum corner of the bounding box in each dimension.
     *
     * @return the point used internally as a corner (exposed read-only).
     */
    public ReadableTuple3i cornerMin() {
        return boundingBox.cornerMin();
    }

    /**
     * Assigns a new buffer for a slice.
     *
     * <p>This is a <b>mutable</b> operation.
     *
     * @param sliceIndexToUpdate slice-index to update.
     * @param bufferToAssign buffer to assign.
     */
    public void replaceSlice(int sliceIndexToUpdate, VoxelBuffer<T> bufferToAssign) {
        voxels.replaceSlice(sliceIndexToUpdate, bufferToAssign);
    }

    /**
     * Interface that allows manipulation of voxel intensities via arithmetic operations.
     *
     * @return the interface.
     */
    public VoxelsArithmetic arithmetic() {
        return voxels.arithmetic();
    }

    @Override
    public String toString() {
        return boundingBox.toString();
    }

    /**
     * Assigns a value to a bounded-voxels accepting <i>global</i> coordinates for objects, boxes
     * etc.
     *
     * @param valueToAssign value to assign.
     * @return an assigner that expects global coordinates.
     */
    public VoxelsAssigner assignValue(int valueToAssign) {
        return VoxelsAssignerFactory.shiftBackBy(
                voxels.assignValue(valueToAssign), boundingBox.cornerMin());
    }

    /**
     * Extracts value from voxels using <i>global</i> coordinates (relative to the image as a
     * whole).
     *
     * @return an extracter instance.
     */
    public final VoxelsExtracter<T> extract() {
        return extracterGlobal;
    }

    /**
     * Creates a grown bounding-box relative to this current box (absolute coordinates).
     *
     * @param negative how much to grow in the negative direction.
     * @param positive how much to grow in the negative direction.
     * @param clipRegion a region to clip to, which we can't grow beyond.
     * @return a bounding box: the corner is the relative-position to the current bounding box, the
     *     extent is absolute.
     */
    private BoundingBox createGrownBoxAbsolute(
            Point3i negative, Point3i positive, Optional<Extent> clipRegion) {
        BoundingBox relBox = createGrownBoxRelative(negative, positive, clipRegion);
        return relBox.reflectThroughOrigin().shiftBy(boundingBox.cornerMin());
    }

    /**
     * Creates a grown bounding-box relative to this current box (relative coordinates).
     *
     * @param negative how much to grow in the negative direction.
     * @param positive how much to grow in the negative direction.
     * @param clipRegion a region to clip to, which we can't grow beyond.
     * @return a bounding box: the corner is the relative-position to the current bounding box
     *     (multiplied by -1), the extent is absolute.
     */
    private BoundingBox createGrownBoxRelative(
            Point3i negative, Point3i positive, Optional<Extent> clipRegion) {

        Point3i negClamped =
                new Point3i(
                        clampNegative(boundingBox.cornerMin().x(), negative.x()),
                        clampNegative(boundingBox.cornerMin().y(), negative.y()),
                        clampNegative(boundingBox.cornerMin().z(), negative.z()));

        ReadableTuple3i boxMax = boundingBox.calculateCornerMax();

        ReadableTuple3i maxPossible;
        if (clipRegion.isPresent()) {
            maxPossible = clipRegion.get().asTuple();
        } else {
            maxPossible = new Point3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        Point3i growBy =
                new Point3i(
                        clampPositive(boxMax.x(), positive.x(), maxPossible.x()) + negClamped.x(),
                        clampPositive(boxMax.y(), positive.y(), maxPossible.y()) + negClamped.y(),
                        clampPositive(boxMax.z(), positive.z(), maxPossible.z()) + negClamped.z());
        return new BoundingBox(negClamped, this.voxels.extent().growBy(growBy));
    }

    /**
     * Considers growing in the <i>negative</i> direction from corner by negative increments.
     *
     * @param corner the value to increment.
     * @param positiveIncrement how much to consider growing by in <i>negative</i> direction.
     * @return the maximum number of increments that are allowed without leading to a bounding box
     *     that is {@code <0}.
     */
    private static int clampNegative(int corner, int negativeIncrement) {
        int diff = corner - negativeIncrement;
        if (diff > 0) {
            return negativeIncrement;
        } else {
            return negativeIncrement + diff;
        }
    }

    /**
     * Considers growing in the <i>positive</i> direction from corner by negative increments.
     *
     * @param corner the value to increment.
     * @param positiveIncrement how much to consider growing by in <i>positive</i> direction.
     * @param max the maximum permitted value in the positive direction.
     * @return the maximum number of increments that are allowed without leading to a bounding box
     *     that is {@code >= max}.
     */
    private static int clampPositive(int corner, int positiveIncrement, int max) {
        int sum = corner + positiveIncrement;
        if (sum < max) {
            return positiveIncrement;
        } else {
            return positiveIncrement - (sum - max + 1);
        }
    }
}
