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

package org.anchoranalysis.image.voxel.object;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.binary.connected.ObjectsFromConnectedComponentsFactory;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.extracter.predicate.VoxelsPredicate;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;
import org.anchoranalysis.image.voxel.factory.VoxelsFactoryTypeBound;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.interpolator.InterpolatorFactory;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsEqualTo;
import org.anchoranalysis.image.voxel.iterator.intersecting.CountVoxelsIntersectingObjects;
import org.anchoranalysis.image.voxel.thresholder.VoxelsThresholder;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.axis.AxisType;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * An object expressed in voxels, bounded within overall space.
 *
 * <p>A bounding-box specifies a location within an image, and a raster-mask specifies which voxels
 * inside this box belong to the object.
 *
 * <p>Each voxel in the raster-mask must be one of two states, either <i>on</i> or <i>off</i>. The
 * object is specified by all voxels that are <i>on</i>.
 *
 * <p><i>on</i> voxels need not be contiguous (i.e. one connected component).
 *
 * <p>The interfaces for assigning, extracting voxels etc. all expect <i>global</i> coordinates.
 *
 * <p>These voxels are <b>mutable</b>.
 */
@Accessors(fluent = true)
public class ObjectMask {

    private static final ObjectsFromConnectedComponentsFactory CONNECTED_COMPONENT_CREATOR =
            new ObjectsFromConnectedComponentsFactory(true);

    private static final VoxelsFactoryTypeBound<UnsignedByteBuffer> FACTORY =
            VoxelsFactory.getUnsignedByte();

    private final BoundedVoxels<UnsignedByteBuffer> voxels;

    @Getter private final BinaryValues binaryValues;
    @Getter private final BinaryValuesByte binaryValuesByte;
    private final Interpolator interpolator;

    @Getter private final VoxelsExtracter<UnsignedByteBuffer> extract;

    /**
     * Creates an object-mask with a corner at the origin (i.e. corner is 0,0,0)
     *
     * <p>Default binary-values of (off=0, on=255) are used.
     *
     * @param voxels voxels to be used in the object-mask
     */
    public ObjectMask(Voxels<UnsignedByteBuffer> voxels) {
        this(new BoundedVoxels<>(voxels));
    }

    /**
     * Creates an object-mask to corresponding to a bounding-box with all pixels off (0)
     *
     * <p>Default binary-values of (off=0, on=255) are used.
     *
     * @param box bounding-box
     */
    public ObjectMask(BoundingBox box) {
        this(VoxelsFactory.getUnsignedByte().createBounded(box));
    }

    /**
     * Creates an object-mask to correspond to bounded-voxels.
     *
     * <p>The voxels are reused without duplication.
     *
     * <p>Default binary-values of (off=0, on=255) are used.
     *
     * @param voxels voxels to be used in the object-mask
     */
    public ObjectMask(BoundedVoxels<UnsignedByteBuffer> voxels) {
        this(voxels, BinaryValues.getDefault());
    }

    public ObjectMask(BinaryVoxels<UnsignedByteBuffer> voxels) {
        this(new BoundedVoxels<>(voxels.voxels()), voxels.binaryValues());
    }

    public ObjectMask(BoundingBox box, Voxels<UnsignedByteBuffer> voxels) {
        this(new BoundedVoxels<>(box, voxels));
    }

    public ObjectMask(
            BoundingBox box, Voxels<UnsignedByteBuffer> voxels, BinaryValues binaryValues) {
        this(new BoundedVoxels<>(box, voxels), binaryValues);
    }

    public ObjectMask(BoundingBox box, BinaryVoxels<UnsignedByteBuffer> voxels) {
        this(new BoundedVoxels<>(box, voxels.voxels()), voxels.binaryValues());
    }

    public ObjectMask(BoundedVoxels<UnsignedByteBuffer> voxels, BinaryValues binaryValues) {
        this.voxels = voxels;
        this.binaryValues = binaryValues;
        this.binaryValuesByte = binaryValues.createByte();
        this.interpolator = createInterpolator(binaryValues);
        this.extract = voxels.extract();
    }

    public ObjectMask(
            BoundingBox box, Voxels<UnsignedByteBuffer> voxels, BinaryValuesByte binaryValuesByte) {
        this.voxels = new BoundedVoxels<>(box, voxels);
        this.binaryValues = binaryValuesByte.createInt();
        this.binaryValuesByte = binaryValuesByte;
        this.interpolator = createInterpolator(binaryValues);
        this.extract = voxels.extract();
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
            BoundedVoxels<UnsignedByteBuffer> voxels,
            BinaryValues binaryValues,
            BinaryValuesByte binaryValuesByte) {
        this.voxels = voxels;
        this.binaryValues = binaryValues;
        this.binaryValuesByte = binaryValuesByte;
        this.interpolator = createInterpolator(binaryValues);
        this.extract = voxels.extract();
    }

    public ObjectMask duplicate() {
        return new ObjectMask(this);
    }

    /**
     * Replaces the voxels in the object-mask.
     *
     * <p>This is an <b>immutable</b> operation, and a new object-mask is created.
     *
     * @param voxelsToAssign voxels to be assigned.
     * @return a new object with the replacement voxels but identical in other respects.
     */
    public ObjectMask replaceVoxels(Voxels<UnsignedByteBuffer> voxelsToAssign) {
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

    /**
     * Counts the number of intersecting-voxels between two object-masks.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param other the other object-mask to consider
     * @return number of <i>on</i>-voxels the two object-masks have in common.
     */
    public int countIntersectingVoxels(ObjectMask other) {
        return CountVoxelsIntersectingObjects.countIntersectingVoxels(this, other);
    }

    /**
     * Determines whether there are any intersecting voxels on two object-masks.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * <p>The algorithm exits as soon as an intersecting voxel is encountered i.e. as early as
     * possible.
     *
     * @param other the other object-mask to consider
     * @return true if at least one voxel exists that is <i>on</i> in both object-masks.
     */
    public boolean hasIntersectingVoxels(ObjectMask other) {
        return CountVoxelsIntersectingObjects.hasIntersectingVoxels(this, other);
    }

    /**
     * Produces a scaled-version of an object-mask.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param factor scale-factor
     * @return a scaled object-mask
     */
    public ObjectMask scale(ScaleFactor factor) {
        return scale(factor, Optional.empty());
    }

    /**
     * Produces a new object-mask that uses the same voxel-buffer but inverts the OFF and ON
     *
     * @return a newly created object-mask (reusing the same buffer)
     */
    public ObjectMask invert() {
        return new ObjectMask(voxels, binaryValues.createInverted());
    }

    /**
     * Produces a scaled-version of an object-mask.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param factor scale-factor
     * @param clipTo an extent which the object-masks should always fit inside after scaling (to
     *     catch any rounding errors that push the bounding box outside the scene-boundary)
     * @return a scaled object-mask
     */
    public ObjectMask scale(ScaleFactor factor, Optional<Extent> clipTo) {

        if ((binaryValues.getOnInt() == 255 && binaryValues.getOffInt() == 0)
                || (binaryValues.getOnInt() == 0 && binaryValues.getOffInt() == 255)) {

            BoundedVoxels<UnsignedByteBuffer> scaled = voxels.scale(factor, interpolator, clipTo);

            // We should do a thresholding afterwards to make sure our values correspond to the two
            // binary values
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

    /**
     * Makes sure an object fits inside an extent, removing any parts which do not.
     *
     * <p>This is NOT an <i>immutable</i> operation, returning the current object unchanged if it
     * already fits inside.
     *
     * @param extent the extent an object must fit in
     * @return either the current object-mask unchanged (if it already fits inside) or a new
     *     object-mask clipped to fit inside
     */
    public ObjectMask clipTo(Extent extent) {
        if (extent.contains(boundingBox())) {
            // nothing to do
            return this;
        } else {
            BoundingBox clippedBox = boundingBox().clampTo(extent);
            return mapBoundingBoxChangeExtent(clippedBox);
        }
    }

    /** Calculates center-of-gravity across all axes */
    public Point3d centerOfGravity() {
        return CenterOfGravityCalculator.centerOfGravity(this);
    }

    /**
     * Calculates center-of-gravity for one specific axis only
     *
     * @param axis the axis
     * @return a point on the specific axis that is the center-of-gravity.
     */
    public double centerOfGravity(AxisType axis) {
        return CenterOfGravityCalculator.centerOfGravityForAxis(this, axis);
    }

    /**
     * Determines if an object-mask is connected.
     *
     * <p>Adjacent is defined with a <i>big</i> neighborhood definition (i.e. 8 or 26 connectivity).
     *
     * @return true if all <i>on</i> voxels in the mask are spatially adjacent to at least one other
     *     <i>on</i> voxel with.
     */
    public boolean checkIfConnected() {
        ObjectCollection objects =
                CONNECTED_COMPONENT_CREATOR.createUnsignedByte(this.binaryVoxels().duplicate());
        return objects.size() <= 1;
    }

    public VoxelsPredicate voxelsOn() {
        return extract.voxelsEqualTo(binaryValues.getOnInt());
    }

    public VoxelsPredicate voxelsOff() {
        return extract.voxelsEqualTo(binaryValues.getOffInt());
    }

    /** The number of "ON" voxels on the object-mask */
    public int numberVoxelsOn() {
        return voxelsOn().count();
    }

    /**
     * Intersects this object-mask with another
     *
     * <p>This is an <i>immutable</i> operation (the existing two masks are not modified).
     *
     * @param other the other object-mask to intersect with
     * @param extent extent to constrain any intersection
     * @return a new object of the intersecting region iff it exists
     */
    public Optional<ObjectMask> intersect(ObjectMask other, Extent extent) {

        // we combine the two objects
        Optional<BoundingBox> boxIntersect =
                boundingBox().intersection().withInside(other.boundingBox(), extent);

        if (!boxIntersect.isPresent()) {
            return Optional.empty();
        }

        // We calculate a bounding box, which we write into in the omDest

        BinaryValues binaryValuesOut = BinaryValues.getDefault();

        // We initially set all pixels to ON
        BoundedVoxels<UnsignedByteBuffer> voxelsMaskOut =
                VoxelsFactory.getUnsignedByte().createBounded(boxIntersect.get());
        voxelsMaskOut.assignValue(binaryValuesOut.getOnInt()).toAll();

        // Then we set any pixels NOT on either object to OFF..... leaving only the intersecting
        // pixels as ON in the output buffer
        voxelsMaskOut
                .assignValue(binaryValuesOut.getOffInt())
                .toEitherTwoObjects(invert(), other.invert(), boxIntersect.get());

        ObjectMask object = new ObjectMask(voxelsMaskOut, binaryValuesOut);

        // If there no pixels left that haven't been set, then the intersection object-mask is zero
        return OptionalUtilities.createFromFlag(object.voxelsOn().anyExists(), object);
    }

    public boolean contains(Point3i point) {

        if (!voxels.boundingBox().contains().point(point)) {
            return false;
        }

        return extract.voxel(point) == binaryValues.getOnInt();
    }

    /**
     * A maximum-intensity projection (flattens in z dimension)
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @return a new object-mask flattened in Z dimension.
     */
    public ObjectMask flattenZ() {
        return new ObjectMask(voxels.projectMax());
    }

    public BoundingBox boundingBox() {
        return voxels.boundingBox();
    }

    public BinaryVoxels<UnsignedByteBuffer> binaryVoxels() {
        return BinaryVoxelsFactory.reuseByte(voxels.voxels(), binaryValues);
    }

    public Voxels<UnsignedByteBuffer> voxels() {
        return voxels.voxels();
    }

    public Extent extent() {
        return voxels.extent();
    }

    public int offsetRelative(int x, int y) {
        return voxels.extent().offset(x, y);
    }

    public int offsetGlobal(int x, int y) {
        return offsetRelative(x - boundingBox().cornerMin().x(), y - boundingBox().cornerMin().y());
    }

    public BoundedVoxels<UnsignedByteBuffer> boundedVoxels() {
        return voxels;
    }

    /**
     * Extracts one particular slice as an object-mask
     *
     * @param sliceIndex the z-slice in global coordinates
     * @param keepIndex iff true the z coordinate is reused for the slice, otherwise 0 is set
     * @return a newly-created object reusing the slice's buffers
     */
    public ObjectMask extractSlice(int sliceIndex, boolean keepIndex) {
        ObjectMask slice = new ObjectMask(voxels.extractSlice(sliceIndex), this.binaryValues);

        if (keepIndex) {
            return slice.mapBoundingBoxPreserveExtent(box -> box.shiftToZ(sliceIndex));
        } else {
            return slice;
        }
    }

    /**
     * Creates an object-mask with a subrange of the slices.
     *
     * <p>This will always reuse the existing voxel-buffers..
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
     * <p>See {@link VoxelsExtracter#region} for more details.
     *
     * @param box bounding-box in absolute coordinates.
     * @param reuseIfPossible if true the existing object will be reused if possible, otherwise a
     *     new object is always created.
     * @return an object-mask corresponding to the requested region, either newly-created or reused
     * @throws CreateException
     */
    public ObjectMask region(BoundingBox box, boolean reuseIfPossible) throws CreateException {
        return new ObjectMask(voxels.region(box, reuseIfPossible), this.binaryValues);
    }

    /**
     * Creates an object-mask covering the a bounding-box (that is required to intersect at least
     * partially)
     *
     * <p>The region outputted will have the same size and coordinates as the bounding-box NOT the
     * existing object-mask.
     *
     * <p>It will contain the correct object-mask values for the intersecting region, and OFF values
     * for the rest.
     *
     * <p>A new voxel-buffer is always created for this operation i.e. the existing box is never
     * reused like sometimes in {@link #region}..
     *
     * @param box bounding-box in absolute coordinates, that must at least partially intersect with
     *     the current object-mask bounds.
     * @return a newly created object-mask containing partially some parts of the existing
     *     object-mask as well as OFF voxels for any other region.
     * @throws CreateException if the boxes do not intersect
     */
    public ObjectMask regionIntersecting(BoundingBox box) throws CreateException {
        return new ObjectMask(
                voxels.regionIntersecting(box, binaryValues.getOffInt()), this.binaryValues);
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
     * @return the location (in absolute coordinates) of an arbitrary "ON" voxel on the object, if
     *     it exists.
     */
    public Optional<Point3i> findArbitraryOnVoxel() {

        // First we try the center-of-gravity
        Point3i point = boundingBox().centerOfGravity();

        if (contains(point)) {
            return Optional.of(point);
        }

        // Second, if needed, we iterate until we find any "ON" value
        return IterateVoxelsEqualTo.untilFirstIntensityEqualTo(
                boundedVoxels(), binaryValuesByte().getOnByte());
    }

    /**
     * A slice buffer with <i>local</i> coordinates i.e. relative to the bounding-box corner
     *
     * @param sliceIndexRelative sliceIndex (z) relative to the bounding-box of the object-mask
     * @return the buffer
     */
    public UnsignedByteBuffer sliceBufferLocal(int sliceIndexRelative) {
        return voxels.sliceBufferLocal(sliceIndexRelative);
    }

    /**
     * A slice buffer with <i>global</i> coordinates
     *
     * @param sliceIndexGlobal sliceIndex (z) in global coordinates
     * @return the buffer
     */
    public UnsignedByteBuffer sliceBufferGlobal(int sliceIndexGlobal) {
        return voxels.sliceBufferGlobal(sliceIndexGlobal);
    }

    /**
     * Creates a new object-mask with identical voxels but with the bounding-box beginning at the
     * origin (0,0,0)
     *
     * <p>This is an <i>immutable</i> operation: but beware the existing voxel-buffers are reused in
     * the new object.
     *
     * @return a new object-mask reusing the existing voxel-buffers
     */
    public ObjectMask shiftToOrigin() {
        return mapBoundingBoxPreserveExtent(BoundingBox::shiftToOrigin);
    }

    /**
     * Shifts the object-mask by moving its bounding-box forwards (i.e. adding {@code shift} from
     * its corner)
     *
     * <p>This is an <i>immutable</i> operation: but beware the existing voxel-buffers are reused in
     * the new object.
     *
     * @param shift what to add from the corner position
     * @return a new object-mask reusing the existing voxel-buffers
     */
    public ObjectMask shiftBy(ReadableTuple3i shift) {
        return mapBoundingBoxPreserveExtent(box -> box.shiftBy(shift));
    }

    /**
     * Shifts the object-mask by moving its bounding-box backwards (i.e. sutracting {@code shift}
     * from its corner)
     *
     * <p>This is an <i>immutable</i> operation: but beware the existing voxel-buffers are reused in
     * the new object.
     *
     * @param shift what to subtract from the corner position
     * @return a new object-mask reusing the existing voxel-buffers
     */
    public ObjectMask shiftBackBy(ReadableTuple3i shift) {
        return mapBoundingBoxPreserveExtent(box -> box.shiftBackBy(shift));
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent should be unchanged
     * in value)
     *
     * <p>This is an <i>immutable</i> operation: but beware the existing voxel-buffers are reused in
     * the new object.
     *
     * @param mapOperation map function to perform mapping of bounding-box
     * @return a new object-mask with the updated bounding box (but unchanged voxels)
     */
    public ObjectMask mapBoundingBoxPreserveExtent(UnaryOperator<BoundingBox> mapOperation) {
        return mapBoundingBoxPreserveExtent(mapOperation.apply(voxels.boundingBox()));
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent is expected to change
     * in value)
     *
     * <p>This is a almost <i>immutable</i> operation, and NEW voxel-buffers are usually created for
     * the new object, but not if the bounding-box or its extent need no change.
     *
     * <p>Precondition: the new bounding-box's extent must be greater than or equal to the existing
     * extent in all dimensions.
     *
     * @param boxToAssign bounding-box to assign
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

        Voxels<UnsignedByteBuffer> voxelsLarge =
                VoxelsFactory.getUnsignedByte().createInitialized(boxToAssign.extent());

        BoundingBox bbLocal = voxels.boundingBox().relativePositionToBox(boxToAssign);

        voxelsLarge
                .assignValue(binaryValuesByte.getOnByte())
                .toObject(new ObjectMask(bbLocal, binaryVoxels()));

        return new ObjectMask(boxToAssign, voxelsLarge, binaryValuesByte);
    }

    /**
     * Creates a new object-mask with coordinates changed to be relative to another box.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @param box box used as a reference point, against which new relative coordinates are
     *     calculated.
     * @return a newly created object-mask with updated coordinates.
     */
    public ObjectMask relativeMaskTo(BoundingBox box) {
        Point3i point = voxels.boundingBox().relativePositionTo(box);

        return new ObjectMask(new BoundingBox(point, voxels.extent()), voxels.voxels());
    }

    /**
     * Assigns ON value to voxels, expecting <i>global</i> coordinates
     *
     * @return the assigner
     */
    public VoxelsAssigner assignOn() {
        return voxels.assignValue(binaryValues.getOnInt());
    }

    /**
     * Assigns OFF value to voxels, expecting <i>global</i> coordinates
     *
     * @return the assigner
     */
    public VoxelsAssigner assignOff() {
        return voxels.assignValue(binaryValues.getOffInt());
    }

    /**
     * Creates a list of all ON voxels as points, using <i>local</i> coordinates i.e. relative to
     * the bounding-box corner
     *
     * @return a newly created list with newly created points
     */
    public List<Point3i> derivePointsLocal() {
        List<Point3i> points = new ArrayList<>();
        IterateVoxelsEqualTo.equalToPrimitive(
                voxels.voxels(),
                binaryValuesByte().getOnByte(),
                (x, y, z) -> points.add(new Point3i(x, y, z)));
        return points;
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

    private Interpolator createInterpolator(BinaryValues binaryValues) {
        return InterpolatorFactory.getInstance().binaryResizing(binaryValues.getOffInt());
    }

    /**
     * Applies a function to map the bounding-box to a new-value (whose extent should be unchanged
     * in value)
     *
     * <p>This is an <i>immutable</i> operation: but beware the existing voxel-buffers are reused in
     * the new object.
     *
     * @param boundingBoxToAssign bounding-box to assign
     * @return a new object-mask with the updated bounding box (but unchanged voxels)
     */
    private ObjectMask mapBoundingBoxPreserveExtent(BoundingBox boundingBoxToAssign) {
        return new ObjectMask(
                voxels.mapBoundingBoxPreserveExtent(boundingBoxToAssign),
                binaryValues,
                binaryValuesByte);
    }
}
