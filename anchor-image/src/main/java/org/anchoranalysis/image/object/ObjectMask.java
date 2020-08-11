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

package org.anchoranalysis.image.object;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelsFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.object.factory.CreateFromConnectedComponentsFactory;
import org.anchoranalysis.image.object.intersecting.CountIntersectingVoxelsBinary;
import org.anchoranalysis.image.object.intersecting.DetermineWhetherIntersectingVoxelsBinary;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;
import org.anchoranalysis.image.voxel.thresholder.VoxelsThresholder;

/**
 * An object expressed in voxels, bounded within overall space
 *
 * <p>A bounding-box defines a box within the overall space, and a raster-mask defines which voxels
 * inside the bounding-box belong to the object.
 *
 * <p>Each voxel in the raster-mask must be one of two states, an ON value and an OFF value
 *
 * <p>These voxels are MUTABLE.
 */
@Accessors(fluent=true)
public class ObjectMask {

    private static final CreateFromConnectedComponentsFactory CONNECTED_COMPONENT_CREATOR =
            new CreateFromConnectedComponentsFactory(true);

    private static final VoxelsFactoryTypeBound<ByteBuffer> FACTORY = VoxelsFactory.getByte();

    private final BoundedVoxels<ByteBuffer> voxels;
    
    @Getter private final BinaryValues binaryValues;
    @Getter private final BinaryValuesByte binaryValuesByte;

    /**
     * Constructor - creates an object-mask assuming coordinates at the origin (i.e. corner is
     * 0,0,0)
     *
     * <p>Default binary-values of (OFF=0, ON=255) are used.
     *
     * @param voxels voxels to be used in the object-mask
     */
    public ObjectMask(Voxels<ByteBuffer> voxels) {
        this(new BoundedVoxels<>(voxels));
    }

    /**
     * Constructor - creates an object-mask to matching a bounding-box but all pixels are OFF (0)
     *
     * <p>Default binary-values of (OFF=0, ON=255) are used.
     *
     * @param box bounding-box
     */
    public ObjectMask(BoundingBox box) {
        this(new BoundedVoxels<>(box, FACTORY));
    }

    /**
     * Constructor - creates an object-mask to matching bounded-voxels
     *
     * <p>The voxels are reused without duplication.
     *
     * <p>Default binary-values of (OFF=0, ON=255) are used.
     *
     * @param voxels voxels to be used in the object-mask
     */
    public ObjectMask(BoundedVoxels<ByteBuffer> voxels) {
        this(voxels, BinaryValues.getDefault());
    }

    public ObjectMask(BinaryVoxels<ByteBuffer> voxels) {
        this(new BoundedVoxels<>(voxels.voxels()), voxels.binaryValues());
    }

    public ObjectMask(BoundingBox box, Voxels<ByteBuffer> voxels) {
        this(new BoundedVoxels<>(box, voxels));
    }

    public ObjectMask(BoundingBox box, Voxels<ByteBuffer> voxels, BinaryValues binaryValues) {
        this(new BoundedVoxels<>(box, voxels), binaryValues);
    }

    public ObjectMask(BoundingBox box, BinaryVoxels<ByteBuffer> voxels) {
        this(new BoundedVoxels<>(box, voxels.voxels()), voxels.binaryValues());
    }

    public ObjectMask(BoundedVoxels<ByteBuffer> voxels, BinaryValues binaryValues) {
        this.voxels = voxels;
        this.binaryValues = binaryValues;
        this.binaryValuesByte = binaryValues.createByte();
    }

    public ObjectMask(
            BoundingBox box, Voxels<ByteBuffer> voxels, BinaryValuesByte binaryValues) {
        this.voxels = new BoundedVoxels<>(box, voxels);
        this.binaryValues = binaryValues.createInt();
        this.binaryValuesByte = binaryValues;
    }

    /**
     * Copy constructor
     *
     * @param src to copy from
     */
    private ObjectMask(ObjectMask src) {
        this(new BoundedVoxels<>(src.voxels), src.binaryValues, src.binaryValuesByte);
    }

    private ObjectMask(
            BoundedVoxels<ByteBuffer> voxels,
            BinaryValues binaryValues,
            BinaryValuesByte binaryValuesByte) {
        this.voxels = voxels;
        this.binaryValues = binaryValues;
        this.binaryValuesByte = binaryValuesByte;
    }

    public ObjectMask duplicate() {
        return new ObjectMask(this);
    }

    /** The number of "ON" voxels on the object-mask */
    public int numberVoxelsOn() {
        return voxels.voxels().countEqual(binaryValues.getOnInt());
    }

    /**
     * Replaces the voxels in the object-mask.
     *
     * <p>This is an IMMUTABLE operation, and a new object-mask is created.
     *
     * @param voxelsToAssign voxels to be assigned.
     * @return a new object with the replacement voxels but identical in other respects.
     */
    public ObjectMask replaceVoxels(Voxels<ByteBuffer> voxelsToAssign) {
        return new ObjectMask(voxels.replaceVoxels(voxelsToAssign), binaryValues);
    }

    public ObjectMask growToZ(int sz) {
        return new ObjectMask(voxels.growToZ(sz, FACTORY));
    }

    public ObjectMask growBuffer(Point3i neg, Point3i pos, Optional<Extent> clipRegion)
            throws OperationFailedException {
        return new ObjectMask(voxels.growBuffer(neg, pos, clipRegion, FACTORY));
    }

    public boolean equalsDeep(ObjectMask other) {
        if (!voxels.equalsDeep(other.voxels)) {
            return false;
        }
        if (!binaryValues.equals(other.binaryValues)) {
            return false;
        }
        return binaryValuesByte.equals(other.binaryValuesByte);
    }

    public int countIntersectingVoxels(ObjectMask other) {
        return new CountIntersectingVoxelsBinary(binaryValuesByte(), other.binaryValuesByte())
                .countIntersectingVoxels(voxels, other.voxels);
    }

    public boolean hasIntersectingVoxels(ObjectMask other) {
        return new DetermineWhetherIntersectingVoxelsBinary(
                        binaryValuesByte(), other.binaryValuesByte())
                .hasIntersectingVoxels(voxels, other.voxels);
    }

    
    /**
     * Produces a scaled-version of an object-mask.
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @param factor scale-factor
     * @param interpolator interpolator
     * @return a scaled object-mask
     */
    public ObjectMask scale(ScaleFactor factor, Interpolator interpolator) {
        return scale(factor, interpolator, Optional.empty());
    }
    
    /**
     * Produces a scaled-version of an object-mask.
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @param factor scale-factor
     * @param interpolator interpolator
     * @param clipTo an extent which the object-masks should always fit inside after scaling (to catch any rounding errors that push the bounding box outside the scene-boundary)
     * @return a scaled object-mask
     */
    public ObjectMask scale(ScaleFactor factor, Interpolator interpolator, Optional<Extent> clipTo) {

        if ((binaryValues.getOnInt() == 255 && binaryValues.getOffInt() == 0)
                || (binaryValues.getOnInt() == 0 && binaryValues.getOffInt() == 255)) {

            BoundedVoxels<ByteBuffer> scaled = voxels.scale(factor, interpolator, clipTo);

            // We should do a thresholding afterwards to make sure our values correspond to the two
            // binry values
            if (interpolator.isNewValuesPossible()) {

                // We threshold to make sure it's still binary
                int thresholdVal = (binaryValues.getOnInt() + binaryValues.getOffInt()) / 2;

                VoxelsThresholder.thresholdForLevel(
                        scaled.voxels(), thresholdVal, binaryValues.createByte());
            }
            return new ObjectMask(scaled, binaryValues);
        } else {
            throw new AnchorFriendlyRuntimeException(
                    "Operation not supported for these binary values");
        }
    }

    /** Calculates center-of-gravity across all axes */
    public Point3d centerOfGravity() {
        return CenterOfGravityCalculator.calcCenterOfGravity(this);
    }

    /**
     * Calculates center-of-gravity for one specific axis only
     *
     * @param axis the axis
     * @return a point on the specific axis that is the center-of-gravity.
     */
    public double centerOfGravity(AxisType axis) {
        return CenterOfGravityCalculator.calcCenterOfGravityForAxis(this, axis);
    }

    /**
     * Determines if an object-mask is connected.
     *
     * <p>TODO this is not particular efficient. We can avoid making the object-collection.
     *
     * @return
     * @throws OperationFailedException
     */
    public boolean checkIfConnected() throws OperationFailedException {

        try {
            ObjectCollection objects =
                    CONNECTED_COMPONENT_CREATOR.createConnectedComponents(
                            this.binaryVoxels().duplicate());
            return objects.size() <= 1;
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    public boolean numPixelsLessThan(int num) {

        Extent e = voxels.voxels().extent();

        int cnt = 0;

        for (int z = 0; z < e.z(); z++) {
            ByteBuffer bb = voxels.voxels().slice(z).buffer();

            while (bb.hasRemaining()) {
                byte b = bb.get();

                if (b == binaryValuesByte.getOnByte()) {
                    cnt++;

                    if (cnt >= num) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean hasPixelsGreaterThan(int num) {

        Extent e = voxels.voxels().extent();

        int cnt = 0;

        for (int z = 0; z < e.z(); z++) {
            ByteBuffer bb = voxels.voxels().slice(z).buffer();

            while (bb.hasRemaining()) {
                byte b = bb.get();

                if (b == binaryValuesByte.getOnByte()) {
                    cnt++;

                    if (cnt > num) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Intersects this object-mask with another
     *
     * @param other the other object-mask to intersect with
     * @param dimensions dimensions to constrain any intersection
     * @return a new object of the intersecting region iff it exists
     */
    public Optional<ObjectMask> intersect(ObjectMask other, ImageDimensions dimensions) {

        // we combine the two objects
        Optional<BoundingBox> boxIntersect =
                boundingBox()
                        .intersection()
                        .withInside(other.boundingBox(), dimensions.extent());

        if (!boxIntersect.isPresent()) {
            return Optional.empty();
        }

        // We calculate a bounding box, which we write into in the omDest

        BinaryValues bvOut = BinaryValues.getDefault();

        // We initially set all pixels to ON
        Voxels<ByteBuffer> voxelsMaskOut = FACTORY.createInitialized(boxIntersect.get().extent());
        voxelsMaskOut.setAllPixelsTo(bvOut.getOnInt());

        // Then we set any pixels NOT on either object to OFF..... leaving only the intersecting
        // pixels as ON in the output buffer
        setVoxelsTwoMasks(
                voxelsMaskOut,
                voxels(),
                other.voxels(),
                boxIntersect.get().relPosToBox(boundingBox()),
                boxIntersect.get().relPosToBox(other.boundingBox()),
                bvOut.getOffInt(),
                this.binaryValuesByte().getOffByte(),
                other.binaryValuesByte().getOffByte());

        ObjectMask object =
                new ObjectMask(boxIntersect.get(), BinaryVoxelsFactory.reuseByte(voxelsMaskOut, bvOut));

        // If there no pixels left that haven't been set, then the intersection object-mask is zero
        return OptionalUtilities.createFromFlag(object.hasPixelsGreaterThan(0), object);
    }

    public boolean contains(Point3i point) {

        if (!voxels.boundingBox().contains().point(point)) {
            return false;
        }

        int xRel = point.x() - voxels.boundingBox().cornerMin().x();
        int yRel = point.y() - voxels.boundingBox().cornerMin().y();
        int zRel = point.z() - voxels.boundingBox().cornerMin().z();

        return voxels.voxels().getVoxel(xRel, yRel, zRel) == binaryValues.getOnInt();
    }

    public boolean containsIgnoreZ(Point3i point) {

        if (!voxels.boundingBox().contains().pointIgnoreZ(point)) {
            return false;
        }

        int xRel = point.x() - voxels.boundingBox().cornerMin().x();
        int yRel = point.y() - voxels.boundingBox().cornerMin().y();

        Extent e = voxels.boundingBox().extent();
        for (int z = 0; z < e.z(); z++) {
            if (voxels.voxels().getVoxel(xRel, yRel, z) == binaryValues.getOnInt()) {
                return true;
            }
        }
        return false;
    }

    /**
     * A maximum-intensity projection (flattens in z dimension)
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @return a new object-mask flattened in Z dimension.
     */
    public ObjectMask flattenZ() {
        return new ObjectMask(voxels.maxIntensityProjection());
    }

    public BoundingBox boundingBox() {
        return voxels.boundingBox();
    }

    public BinaryVoxels<ByteBuffer> binaryVoxels() {
        return BinaryVoxelsFactory.reuseByte(voxels.voxels(), binaryValues);
    }

    public Voxels<ByteBuffer> voxels() {
        return voxels.voxels();
    }

    public BoundedVoxels<ByteBuffer> boundedVoxels() {
        return voxels;
    }

    // If keepZ is true the slice keeps its z coordinate, otherwise its set to 0
    public ObjectMask extractSlice(int z, boolean keepZ) {
        return new ObjectMask(voxels.extractSlice(z, keepZ), this.binaryValues);
    }

    /**
     * Creates an object-mask with a subrange of the slices.
     *
     * <p>This will always reuse the existing voxel-buffers.</p.
     *
     * @param zMin minimum z-slice index, inclusive.
     * @param zMax maximum z-slice index, inclusive.
     * @return a newly created object-mask for the slice-range requested.
     * @throws CreateException
     */
    public ObjectMask regionZ(int zMin, int zMax) throws CreateException {
        return new ObjectMask(voxels.regionZ(zMin, zMax, FACTORY), this.binaryValues);
    }

    /**
     * A (sub-)region of the object-mask.
     *
     * <p>The region may some smaller portion of the voxels, or the voxels as a whole.
     *
     * <p>It should <b>never</b> be larger than the voxels.
     *
     * <p>See {@link org.anchoranalysis.image.voxel.Voxels#region) for more details.
     *
     * @param box bounding-box in absolute coordinates.
     * @param reuseIfPossible if TRUE the existing object will be reused if possible, otherwise a new object is always created.
     * @return an object-mask corresponding to the requested region, either newly-created or reused
     * @throws CreateException
     */
    public ObjectMask region(BoundingBox box, boolean reuseIfPossible) throws CreateException {
        return new ObjectMask(voxels.region(box, reuseIfPossible), this.binaryValues);
    }

    /**
     * Creates an object-mask covering the a bounding-box (that is required to intersect at least partially)
     *
     * <p>The region outputted will have the same size and coordinates as the bounding-box NOT the
     * existing object-mask.
     *
     * <p>It will contain the correct object-mask values for the intersecting region, and OFF values for
     * the rest.
     *
     * <p>A new voxel-buffer is always created for this operation i.e. the existing box is never
     * reused like sometimes in {@link region}.</p.
     *
     * @param box bounding-box in absolute coordinates, that must at least partially intersect with
     *     the current object-mask bounds.
     * @return a newly created object-mask containing partially some parts of the existing object-mask as well as
     *     OFF voxels for any other region.
     * @throws CreateException if the boxes do not intersect
     */
    public ObjectMask regionIntersecting(BoundingBox box) throws CreateException {
        return new ObjectMask(voxels.regionIntersecting(box, binaryValues.getOffInt()), this.binaryValues);
    }

    /**
     * Finds any arbitrary "ON" voxel on the object.
     *
     * <p>First it tries the center-of-gravity voxel, and if that's not on, it iterates through the
     * box until it finds an "ON" voxel.
     *
     * <p>This is a DETERMINISTIC operation, so one can rely on the same voxel being found for a
     * given object.
     *
     * @return the location (in absolute coordinates) of an arbitrary "ON" voxel on the object, if it
     *     exists.
     */
    public Optional<Point3i> findArbitraryOnVoxel() {

        // First we try the center-of-gravity
        Point3i point = boundingBox().centerOfGravity();

        if (contains(point)) {
            return Optional.of(point);
        }

        // Second, if needed, we iterate until we find any "ON" value
        return IterateVoxels.findFirstPointOnObjectMask(this);
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent should be unchanged
     * in value)
     *
     * <p>This is an almost <i>immutable</i> operation: the existing voxel-buffers are reused in the new
     * object.
     *
     * @param mapFunc map function to perform mapping of bounding-box
     * @return a new object-mask with the updated bounding box (but unchanged voxels)
     */
    public ObjectMask mapBoundingBoxPreserveExtent(UnaryOperator<BoundingBox> mapFunc) {
        return mapBoundingBoxPreserveExtent(mapFunc.apply(voxels.boundingBox()));
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent should be unchanged
     * in value)
     *
     * <p>This is an almost IMMUTABLE operation: the existing voxel-buffers are reused in the new
     * object.
     *
     * @param boundingBoxToAssign bounding-box to assign
     * @return a new object-mask with the updated bounding box (but unchanged voxels)
     */
    public ObjectMask mapBoundingBoxPreserveExtent(BoundingBox boundingBoxToAssign) {
        return new ObjectMask(voxels.mapBoundingBoxPreserveExtent(boundingBoxToAssign), binaryValues, binaryValuesByte);
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent is expected to change
     * in value)
     *
     * <p>This is a almost <i>immutable</i> operation, and NEW voxel-buffers are usually created for the
     * new object, but not if the bounding-box or its extent need no change.
     *
     * <p>Precondition: the new bounding-box's extent must be greater than or equal to the existing
     * extent in all dimensions.
     *
     * @param boxToAssign bounding-box to assign
     * @param function to perform mapping of bounding-box
     * @return a new object-mask with the updated bounding box (and changed voxels)
     */
    public ObjectMask mapBoundingBoxChangeExtent(BoundingBox boxToAssign) {

        Preconditions.checkArgument(
                !voxels.extent().anyDimensionIsLargerThan(boxToAssign.extent()));

        if (voxels.boundingBox().equals(boxToAssign)) {
            // Nothing to do, bounding-boxes are equal, early exit
            return this;
        }

        if (voxels.boundingBox().equals(boxToAssign)) {
            // Nothing to do, extents are equal, take the easier path of mapping only the bounding
            // box
            return mapBoundingBoxPreserveExtent(boxToAssign);
        }

        Voxels<ByteBuffer> voxelsLarge = VoxelsFactory.getByte().createInitialized(boxToAssign.extent());

        BoundingBox bbLocal = voxels.boundingBox().relPosToBox(boxToAssign);

        voxelsLarge.setPixelsCheckMask(
                new ObjectMask(bbLocal, binaryVoxels()), binaryValuesByte.getOnByte());

        return new ObjectMask(boxToAssign, voxelsLarge, binaryValuesByte);
    }

    /**
     * Creates a new obj-mask with coordinates changed to be relative to another box.
     *
     * <p>This is an IMMUTABLE operation.
     *
     * @param box box used as a reference point, against which new relative coordinates are
     *     calculated.
     * @return a newly created object-mask with updated coordinates.
     */
    public ObjectMask relMaskTo(BoundingBox box) {
        Point3i point = voxels.boundingBox().relPosTo(box);

        return new ObjectMask(new BoundingBox(point, voxels.extent()), voxels.voxels());
    }

    /** Sets a point (expressed in global coordinates) to be ON */
    public void setOn(Point3i pointGlobal) {
        setVoxel(pointGlobal, binaryValues.getOnInt());
    }

    /** Sets a point (expressed in global coordinates) to be OFF */
    public void setOff(Point3i pointGlobal) {
        setVoxel(pointGlobal, binaryValues.getOffInt());
    }

    /**
     * A string representation of the object-mask showing:
     *
     * <ol>
     *   <li>the center-of-gravity
     *   <li>the number of "ON" voxels on the object
     * </ol>
     */
    @Override
    public String toString() {
        return String.format(
                "Obj%s(cog=%s,numPixels=%d)",
                super.hashCode(), centerOfGravity().toString(), numberVoxelsOn());
    }
    
    private void setVoxel(Point3i pointGlobal, int val) {
        Point3i cornerMin = voxels.boundingBox().cornerMin();
        voxels.voxels()
                .setVoxel(
                        pointGlobal.x() - cornerMin.x(),
                        pointGlobal.y() - cornerMin.y(),
                        pointGlobal.z() - cornerMin.z(),
                        val);
    }
    
    /**
     * Sets voxels to match either of two objects
     *
     * @param voxelsMaskOut voxels to write to
     * @param voxels1 voxels for first object
     * @param voxels2 voxels for second object
     * @param box1 bounding-box for first object
     * @param box2 bounding-box for second object
     * @param value value to write
     * @param matchValue1 object-mask value to match against for first object
     * @param matchValue2 object-mask value to match against for second object
     * @return the total number of pixels written
     */
    private static int setVoxelsTwoMasks( // NOSONAR
            Voxels<ByteBuffer> voxelsMaskOut,
            Voxels<ByteBuffer> voxels1,
            Voxels<ByteBuffer> voxels2,
            BoundingBox box1,
            BoundingBox box2,
            int value,
            byte matchValue1,
            byte matchValue2) {
        BoundingBox allOut = new BoundingBox(voxelsMaskOut.extent());
        int cntSetFirst =
                voxelsMaskOut.setPixelsCheckMask(
                        allOut, voxels1, box1, value, matchValue1);
        int cntSetSecond =
                voxelsMaskOut.setPixelsCheckMask(
                        allOut, voxels2, box2, value, matchValue2);
        return cntSetFirst + cntSetSecond;
    }
}
