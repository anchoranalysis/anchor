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

package org.anchoranalysis.image.voxel.box;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.nio.Buffer;
import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.OperationFailedRuntimeException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;

/**
 * A a voxel buffer representing an object i.e. a region bounded in space
 *
 * @author Owen Feehan
 * @param <T> BufferType
 */
@AllArgsConstructor
public class BoundedVoxelBox<T extends Buffer> {

    private static final Point3i ALL_ONES_2D = new Point3i(1, 1, 0);
    private static final Point3i ALL_ONES_3D = new Point3i(1, 1, 1);

    /** A bounding-box that associates voxels to a particular part of an image */
    @Getter private BoundingBox boundingBox;
    
    /** Voxel-data that fits inside the bounding-box (its extent is invariant with the exent of the bounding-box). */
    @Getter private VoxelBox<T> voxels;

    /**
     * Constructor - initialises a voxel box to match a bounding-box size with all values set to 0
     *
     * @param boundingBox bounding-box
     * @param factory factory for voxel-box
     */
    public BoundedVoxelBox(BoundingBox boundingBox, VoxelBoxFactoryTypeBound<T> factory) {
        this.boundingBox = boundingBox;
        this.voxels = factory.create(boundingBox.extent());
    }

    /**
     * Constructor - creates a voxel-box bounded to match the entire voxel-data at the origin
     * 
     * @param voxels voxel-data
     */
    public BoundedVoxelBox(VoxelBox<T> voxels) {
        this.boundingBox = new BoundingBox(voxels.extent());
        this.voxels = voxels;
    }

    /**
     * Copy constructor
     *
     * @param source where to copy from
     */
    public BoundedVoxelBox(BoundedVoxelBox<T> source) {
        this.boundingBox = source.getBoundingBox();
        this.voxels = source.voxels.duplicate();
    }

    public boolean equalsDeep(BoundedVoxelBox<?> other) {

        if (!boundingBox.equals(other.boundingBox)) {
            return false;
        }
        return voxels.equalsDeep(other.voxels);
    }

    /**
     * Replaces the voxels in the box.
     *
     * <p>This is an IMMUTABLE operation, and a new voxel-box is created.
     *
     * @param voxelBoxToAssign voxels to be assigned.
     * @return a new voxel-box with the replacement voxels but identical bounding-box
     */
    public BoundedVoxelBox<T> replaceVoxels(VoxelBox<T> voxelBoxToAssign) {
        Preconditions.checkArgument(voxelBoxToAssign.extent().equals(extent()));
        return new BoundedVoxelBox<>(boundingBox, voxelBoxToAssign);
    }

    public BoundedVoxelBox<T> growToZ(int sz, VoxelBoxFactoryTypeBound<T> factory) {
        assert (this.boundingBox.extent().getZ() == 1);
        assert (this.voxels.extent().getZ() == 1);

        BoundingBox bboxNew =
                new BoundingBox(boundingBox.cornerMin(), boundingBox.extent().duplicateChangeZ(sz));

        VoxelBox<T> buffer = factory.create(bboxNew.extent());

        Extent e = this.boundingBox.extent();
        BoundingBox bboxSrc = new BoundingBox(e);

        // we copy in one by one
        for (int z = 0; z < buffer.extent().getZ(); z++) {
            this.voxels.copyPixelsTo(bboxSrc, buffer, new BoundingBox(new Point3i(0, 0, z), e));
        }

        return new BoundedVoxelBox<>(bboxNew, buffer);
    }

    // Considers growing in the negative direction from crnr by neg increments
    //  returns the maximum number of increments that are allowed without leading
    //  to a bounding box that is <0
    private static int clipNeg(int crnr, int neg) {
        int diff = crnr - neg;
        if (diff > 0) {
            return neg;
        } else {
            return neg + diff;
        }
    }

    // Considers growing in the positive direction from crnr by neg increments
    //  returns the maximum number of increments that are allowed without leading
    //  to a bounding box that is >= max
    private static int clipPos(int crnr, int pos, int max) {
        int sum = crnr + pos;
        if (sum < max) {
            return pos;
        } else {
            return pos - (sum - max + 1);
        }
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
     * @param a region to clip to, which we can't grow beyond
     * @return a bounding box: the crnr is the relative-position to the current bounding box
     *     (multipled by -1), the extent is absolute
     */
    private BoundingBox createGrownBoxRelative(
            Point3i neg, Point3i pos, Optional<Extent> clipRegion) {

        Point3i negClip =
                new Point3i(
                        clipNeg(boundingBox.cornerMin().getX(), neg.getX()),
                        clipNeg(boundingBox.cornerMin().getY(), neg.getY()),
                        clipNeg(boundingBox.cornerMin().getZ(), neg.getZ()));

        ReadableTuple3i bboxMax = boundingBox.calcCornerMax();

        ReadableTuple3i maxPossible;
        if (clipRegion.isPresent()) {
            maxPossible = clipRegion.get().asTuple();
        } else {
            maxPossible = new Point3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        Point3i growBy =
                new Point3i(
                        clipPos(bboxMax.getX(), pos.getX(), maxPossible.getX()) + negClip.getX(),
                        clipPos(bboxMax.getY(), pos.getY(), maxPossible.getY()) + negClip.getY(),
                        clipPos(bboxMax.getZ(), pos.getZ(), maxPossible.getZ()) + negClip.getZ());
        return new BoundingBox(negClip, this.voxels.extent().growBy(growBy));
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
    public BoundedVoxelBox<T> growBuffer(
            Point3i growthNegative,
            Point3i growthPositive,
            Optional<Extent> clipRegion,
            VoxelBoxFactoryTypeBound<T> factory)
            throws OperationFailedException {

        if (clipRegion.isPresent() && !clipRegion.get().contains(this.boundingBox)) {
            throw new OperationFailedException(
                    "Cannot grow the bounding-box of the object-mask, as it is already outside the clipping region.");
        }

        Extent e = this.voxels.extent();

        BoundingBox grownBox = createGrownBoxRelative(growthNegative, growthPositive, clipRegion);

        // We allocate a new buffer
        VoxelBox<T> bufferNew = factory.create(grownBox.extent());
        this.voxels.copyPixelsTo(
                new BoundingBox(e), bufferNew, new BoundingBox(grownBox.cornerMin(), e));

        // We create a new bounding box
        BoundingBox bbo =
                new BoundingBox(
                        Point3i.immutableSubtract(
                                this.boundingBox.cornerMin(), grownBox.cornerMin()),
                        grownBox.extent());

        return new BoundedVoxelBox<>(bbo, bufferNew);
    }

    /**
     * Creates a scaled-version (in XY dimensions only) of the current bounding-box
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @param scaleFactor what to scale X and Y dimensions by?
     * @param interpolator means of interpolating between pixels
     * @return a new bounded-voxels box of specified size containing scaled contents of the existing
     */
    public BoundedVoxelBox<T> scale(ScaleFactor scaleFactor, Interpolator interpolator) {

        VoxelBox<T> voxelBoxOut =
                voxels.resizeXY(
                        ScaleFactorUtilities.scaleQuantity(
                                scaleFactor.getX(), boundingBox.extent().getX()),
                        ScaleFactorUtilities.scaleQuantity(
                                scaleFactor.getY(), boundingBox.extent().getY()),
                        interpolator);

        return new BoundedVoxelBox<>(
                boundingBox.scale(scaleFactor, voxelBoxOut.extent()), voxelBoxOut);
    }

    /**
     * A maximum-intensity projection (flattens in z dimension)
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @return a new bounded-voxel-box flattened in Z dimension.
     */
    public BoundedVoxelBox<T> maxIntensityProjection() {
        return new BoundedVoxelBox<>(boundingBox.flattenZ(), voxels.maxIntensityProjection());
    }

    public BoundedVoxelBox<T> duplicate() {
        return new BoundedVoxelBox<>(this);
    }

    public T getPixelsForPlane(int z) {
        return voxels.getPixelsForPlane(z).buffer();
    }

    public Extent extent() {
        return voxels.extent();
    }

    /**
     * Creates an box with a subrange of the slices.
     *
     * <p>This will always reuse the existing voxel-buffers.</p.
     *
     * @param zMin minimum z-slice index, inclusive.
     * @param zMax maximum z-slice index, inclusive.
     * @param factory factory to use to create new voxels.
     * @return a newly created box for the slice-range requested.
     * @throws CreateException
     */
    public BoundedVoxelBox<T> regionZ(int zMin, int zMax, VoxelBoxFactoryTypeBound<T> factory)
            throws CreateException {

        if (!boundingBox.contains().z(zMin)) {
            throw new CreateException("zMin outside range");
        }
        if (!boundingBox.contains().z(zMax)) {
            throw new CreateException("zMax outside range");
        }

        int relZ = zMin - boundingBox.cornerMin().getZ();

        BoundingBox target =
                new BoundingBox(
                        boundingBox.cornerMin().duplicateChangeZ(zMin),
                        boundingBox.extent().duplicateChangeZ(zMax - zMin + 1));

        SubrangeVoxelAccess<T> voxelAccess = new SubrangeVoxelAccess<>(relZ, target.extent(), this);
        return new BoundedVoxelBox<>(target, factory.create(voxelAccess));
    }

    /**
     * A (sub-)region of the voxels.
     *
     * <p>The region may some smaller portion of the voxel-box, or the voxel-box as a whole.
     *
     * <p>It should <b>never</b> be larger than the voxel-box.
     *
     * @see org.anchoranalysis.image.voxel.box.VoxelBox#region
     * @param bbox bounding-box in absolute coordinates.
     * @param reuseIfPossible if TRUE the existing box will be reused if possible, otherwise a new
     *     box is always created.
     * @return a bounded voxel-box corresponding to the requested region, either newly-created or
     *     reused
     * @throws CreateException
     */
    public BoundedVoxelBox<T> region(BoundingBox bbox, boolean reuseIfPossible)
            throws CreateException {

        if (!boundingBox.contains().box(bbox)) {
            throw new CreateException("Source box does not contain target box");
        }

        BoundingBox target = bbox.relPosToBox(boundingBox);
        return new BoundedVoxelBox<>(bbox, voxels.region(target, reuseIfPossible));
    }

    /**
     * Like {@link region} but only expects a bounding-box that intersects at least partially.
     *
     * <p>This is a weakened condition compared to {@link region}.
     *
     * <p>The region outputted will have the same size and coordinates as the bounding-box, but with
     * the correct voxel-values for the part within the voxel-box. Any other voxels are set to
     * {@code voxelValueForRest}.
     *
     * <p>A new voxel-buffer is always created for this operation i.e. the existing box is never
     * reused like sometimes in {@link region}.</p.
     *
     * @param bbox bounding-box in absolute coordinates, that must at least partially intersect with
     *     the current bounds.
     * @param voxelValueForRest a voxel-value for the parts of the buffer not covered by the
     *     intersection.
     * @return a newly created voxel-box containing partially some parts of the existing voxels and
     *     other regions.
     * @throws CreateException if the boxes do not intersect
     */
    public BoundedVoxelBox<T> regionIntersecting(BoundingBox bbox, int voxelValueForRest)
            throws CreateException {

        Optional<BoundingBox> bboxIntersect = boundingBox.intersection().with(bbox);
        if (!bboxIntersect.isPresent()) {
            throw new CreateException(
                    "Requested bounding-box does not intersect with current bounds");
        }

        VoxelBox<T> bufNew = voxels.getFactory().create(bbox.extent());

        // We can rely on the newly created voxels being 0 by default, otherwise we must update.
        if (voxelValueForRest != 0) {
            voxels.setAllPixelsTo(voxelValueForRest);
        }

        voxels.copyPixelsTo(
                bboxIntersect.get().relPosToBox(this.boundingBox),
                bufNew,
                bboxIntersect.get().relPosToBox(bbox));

        return new BoundedVoxelBox<>(bbox, bufNew);
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent should be unchanged
     * in value)
     *
     * <p>This is an IMMUTABLE operation, but the existing voxel-buffers are reused in the new
     * object.
     *
     * @return a new object-mask with the updated bounding box
     */
    public BoundedVoxelBox<T> mapBoundingBoxPreserveExtent(BoundingBox boundingBoxToAssign) {

        if (!boundingBoxToAssign.extent().equals(boundingBox.extent())) {
            throw new OperationFailedRuntimeException(
                    "The extent changed while mapping bounding-box, which is not allowed.");
        }

        return new BoundedVoxelBox<>(boundingBoxToAssign, voxels);
    }

    /**
     * Extracts a particular slice.
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @param z which slice to extract
     * @param keepZ if true the slice keeps its z coordinate, otherwise its set to 0
     * @return the extracted-slice (bounded)
     */
    public BoundedVoxelBox<T> extractSlice(int z, boolean keepZ) {

        BoundingBox bboxFlattened = boundingBox.flattenZ();

        return new BoundedVoxelBox<>(
                keepZ ? bboxFlattened.shiftToZ(z) : bboxFlattened, voxels.extractSlice(z));
    }
}
