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
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesInt;
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
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * A localized-mask in an image, expressed as a {@link BoundingBox}, with a corresponding mask sized
 * to match the bounding-box.
 *
 * <p>This represents the concept of an <i>object</i> residing within the image.
 *
 * <p>Each voxel in the mask must be one of two states, either <i>on</i> or <i>off</i>. The object
 * is specified by all voxels that are <i>on</i>.
 *
 * <p>The <i>on</i> voxels need not be contiguous i.e. they need not form a single
 * connected-component.
 *
 * <p>The interfaces for assigning, extracting voxels etc. all expect <i>global</i> coordinates,
 * expressed relative to the image's coordinates as a whole.
 *
 * <p>The voxels in the mask are <i>mutable</i>.
 *
 * <p>A point is informally referred to as belonging <i>on</i> or <i>in</i> in the object-mask, or
 * being contained by the object-mask if it fulfills both the two conditions of:
 *
 * <ul>
 *   <li>The point exists inside the bounding-box.
 *   <li>The corresponding voxel in {@code #voxels()} has an <i>on</i> state.
 * </ul>
 */
@Accessors(fluent = true)
public class ObjectMask {

    private static final ObjectsFromConnectedComponentsFactory CONNECTED_COMPONENT_CREATOR =
            new ObjectsFromConnectedComponentsFactory(true);

    private static final VoxelsFactoryTypeBound<UnsignedByteBuffer> FACTORY =
            VoxelsFactory.getUnsignedByte();

    /** The voxels defining the mask. */
    private final BoundedVoxels<UnsignedByteBuffer> voxels;

    /**
     * What values constitute an <i>on</i> and <i>off</i> state in {@code voxels} - as <i>int</i>s.
     */
    @Getter private final BinaryValuesInt binaryValues;

    /**
     * What values constitute an <i>on</i> and <i>off</i> state in {@code voxels} - as <i>byte</i>s.
     */
    @Getter private final BinaryValuesByte binaryValuesByte;

    /** The interpolator to use when resizing. */
    private final Interpolator interpolator;

    /** Provides methods to read/copy/duplicate regions of voxels. */
    @Getter private final VoxelsExtracter<UnsignedByteBuffer> extract;

    /**
     * Creates from a {@link Voxels} mask that is cornered at the origin.
     *
     * <p>i.e. the bounding box corner is set as <code>0,0,0</code>.
     *
     * <p>Default {@link BinaryValuesInt} of ({@code off=0}, {@code on=255}) are used to interpret
     * {@link Voxels} as a mask.
     *
     * <p>The {@link Voxels} are reused internally in memory without duplication.
     *
     * @param voxels voxels to be used in the object-mask.
     */
    public ObjectMask(Voxels<UnsignedByteBuffer> voxels) {
        this(new BoundedVoxels<>(voxels));
    }

    /**
     * Creates as a bounding-box with all corresponding mask voxels set to <i>off</i>.
     *
     * <p>Default {@link BinaryValuesInt} of (off=0, on=255) are used for the mask.
     *
     * @param box bounding-box.
     */
    public ObjectMask(BoundingBox box) {
        this(VoxelsFactory.getUnsignedByte().createBounded(box));
    }

    /**
     * Creates from a {@link BoundedVoxels} with {@link UnsignedByteBuffer}.
     *
     * <p>The voxels are reused without duplication.
     *
     * <p>Default {@link BinaryValuesInt} of (off=0, on=255) are assumed, and {@code voxels} should
     * only contain these values.
     *
     * @param voxels voxels to be used in the object-mask.
     */
    public ObjectMask(BoundedVoxels<UnsignedByteBuffer> voxels) {
        this(voxels, BinaryValuesInt.getDefault());
    }

    /**
     * Creates from a {@link BinaryVoxels} to be located at the origin.
     *
     * <p>The voxels are reused without duplication.
     *
     * @param voxels the voxels.
     */
    public ObjectMask(BinaryVoxels<UnsignedByteBuffer> voxels) {
        this(new BoundedVoxels<>(voxels.voxels()), voxels.binaryValues());
    }

    /**
     * Creates from {@link Voxels} and a {@link BoundingBox} with default {@link BinaryValuesInt}.
     *
     * <p>The voxels are reused without duplication.
     *
     * <p>Default {@link BinaryValuesInt} of (off=0, on=255) are assumed, and {@code voxels} should
     * only contain these values.
     *
     * @param box the bounding-box.
     * @param voxels the voxels, which must be the same size as {@code box}.
     */
    public ObjectMask(BoundingBox box, Voxels<UnsignedByteBuffer> voxels) {
        this(new BoundedVoxels<>(box, voxels));
    }

    /**
     * Creates from {@link Voxels} and a corresponding {@link BoundingBox} and {@link BinaryValuesInt}.
     *
     * <p>The voxels are reused without duplication.
     *
     * @param box the bounding-box.
     * @param voxels the voxels, which must be the same size as {@code box}, and should only contain
     *     values in {@code binaryValues}.
     * @param binaryValues the binary-values to use (as {@code int}s).
     */
    public ObjectMask(
            BoundingBox box, Voxels<UnsignedByteBuffer> voxels, BinaryValuesInt binaryValues) {
        this(new BoundedVoxels<>(box, voxels), binaryValues);
    }

    /**
     * Creates from a {@link BinaryVoxels} and a corresponding bounding-box.
     *
     * <p>The voxels are reused without duplication.
     *
     * @param box the bounding-box.
     * @param voxels the voxels, which must be the same size as {@code box}.
     */
    public ObjectMask(BoundingBox box, BinaryVoxels<UnsignedByteBuffer> voxels) {
        this(new BoundedVoxels<>(box, voxels.voxels()), voxels.binaryValues());
    }

    /**
     * Creates from {@link BoundedVoxels} and corresponding {@link BinaryValuesInt}.
     *
     * <p>The voxels are reused without duplication.
     *
     * @param voxels the voxels.
     * @param binaryValues the binary-values to use.
     */
    public ObjectMask(BoundedVoxels<UnsignedByteBuffer> voxels, BinaryValuesInt binaryValues) {
        this.voxels = voxels;
        this.binaryValues = binaryValues;
        this.binaryValuesByte = binaryValues.asByte();
        this.interpolator = createInterpolator(binaryValues);
        this.extract = voxels.extract();
    }

    /**
     * Like {@link #ObjectMask(BoundingBox, Voxels, BinaryValuesInt)} but specifies the binary-values
     * as bytes.
     *
     * @param box the bounding-box.
     * @param voxels the voxels, which must be the same size as {@code box}, and should only contain
     *     values in {@code binaryValues}.
     * @param binaryValues the binary-values to use (as {@code byte}s).
     */
    public ObjectMask(
            BoundingBox box, Voxels<UnsignedByteBuffer> voxels, BinaryValuesByte binaryValues) {
        this.voxels = new BoundedVoxels<>(box, voxels);
        this.binaryValues = binaryValues.asInt();
        this.binaryValuesByte = binaryValues;
        this.interpolator = createInterpolator(this.binaryValues);
        this.extract = voxels.extract();
    }

    /**
     * Copy constructor.
     *
     * <p>It is a deep copy. The voxel memory buffer is duplicated.
     *
     * @param source to copy from.
     */
    private ObjectMask(ObjectMask source) {
        this(new BoundedVoxels<>(source.voxels), source.binaryValues, source.binaryValuesByte);
    }

    private ObjectMask(
            BoundedVoxels<UnsignedByteBuffer> voxels,
            BinaryValuesInt binaryValues,
            BinaryValuesByte binaryValuesByte) {
        this.voxels = voxels;
        this.binaryValues = binaryValues;
        this.binaryValuesByte = binaryValuesByte;
        this.interpolator = createInterpolator(binaryValues);
        this.extract = voxels.extract();
    }

    /**
     * Creates a deep-copy of the current object-mask.
     *
     * @return a newly created mask that is a duplicate, including duplicating the voxel memory
     *     buffers.
     */
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

    /**
     * Grows a single z-sliced {@link ObjectMask} by duplicating the slice across the z-dimension
     * {@code sizeZ} number of times.
     *
     * @param sizeZ the size in the z-dimension to grow to i.e. the number of duplicated sizes.
     * @return a new {@link BoundedVoxels} with an identical corner, but with a 3D bounding-box (and
     *     duplicated slices) instead of the previous 2D.
     * @throws OperationFailedException if the existing voxels aren't 2D (a single slice).
     */
    public ObjectMask growToZ(int sizeZ) throws OperationFailedException {
        return new ObjectMask(voxels.growToZ(sizeZ, FACTORY));
    }

    /**
     * Grows the object-mask's voxel buffers in the positive and negative directions by a certain
     * amount.
     *
     * <p>This operation is <i>immutable</i>.
     *
     * @param growthNegative how much to grow in the <i>negative</i> direction (i.e. downards
     *     direction on an axis).
     * @param growthPositive how much to grow in the <i>positive</i> direction (i.e. upwards
     *     direction on an axis).
     * @param clipRegion if defined, clips the buffer to this region.
     * @return the grown object-mask with newly-created buffers.
     * @throws OperationFailedException if the voxels are located outside the clipping region.
     */
    public ObjectMask growBuffer(
            Point3i growthNegative, Point3i growthPositive, Optional<Extent> clipRegion)
            throws OperationFailedException {
        return new ObjectMask(
                voxels.growBuffer(growthNegative, growthPositive, clipRegion, FACTORY));
    }

    /**
     * A deep equality check with another {@link ObjectMask}.
     *
     * <p>Each voxel must be identical, as well as identical binary-values and bounding-box.
     *
     * @param other the other {@link ObjectMask} to compare against.
     * @return true iff the two object-masks are identical.
     */
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
     * @param other the other object-mask to consider.
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
     * @param other the other object-mask to consider.
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
     * @param factor scale-factor.
     * @return a scaled object-mask.
     */
    public ObjectMask scale(ScaleFactor factor) {
        return scale(factor, Optional.empty());
    }

    /**
     * Produces a new object-mask that uses the same voxel-buffer but switches the <i>off</i> and
     * <i>on</i> mapping.
     *
     * @return a newly created object-mask (reusing the same buffer).
     */
    public ObjectMask invert() {
        return new ObjectMask(voxels, binaryValues.createInverted());
    }

    /**
     * Produces a scaled-version of an object-mask.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @param factor scale-factor.
     * @param clipTo an extent which the object-masks should always fit inside after scaling (to
     *     catch any rounding errors that push the bounding box outside the scene-boundary).
     * @return a scaled object-mask.
     */
    public ObjectMask scale(ScaleFactor factor, Optional<Extent> clipTo) {

        if ((binaryValues.getOnInt() == 255 && binaryValues.getOffInt() == 0)
                || (binaryValues.getOnInt() == 0 && binaryValues.getOffInt() == 255)) {

            BoundedVoxels<UnsignedByteBuffer> scaled = voxels.scale(factor, interpolator, clipTo);

            // We should do a thresholding afterwards to make sure our values correspond to the two
            // binary values
            if (interpolator.canValueRangeChange()) {

                // We threshold to make sure it's still binary
                int thresholdVal = (binaryValues.getOnInt() + binaryValues.getOffInt()) / 2;

                VoxelsThresholder.thresholdByte(
                        scaled.voxels(), thresholdVal, binaryValues.asByte());
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
     * <p>This is <b>not</b> an <i>immutable</i> operation, returning the current object unchanged
     * if it already fits inside.
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

    /**
     * Calculates center-of-gravity across all axes.
     *
     * <p>This is the mean of the position coordinates in each dimension.
     *
     * @return the center-of-gravity or {@code (NaN, NaN, NaN)} if there are no <i>on</i> voxels.
     */
    public Point3d centerOfGravity() {
        return CenterOfGravityCalculator.centerOfGravity(this);
    }

    /**
     * Calculates center-of-gravity for one specific axis only.
     *
     * @param axis the specific axis.
     * @return a point on the specific axis that is the center-of-gravity, or {@code NaN} if there
     *     are no <i>on</i> voxels.
     */
    public double centerOfGravity(Axis axis) {
        return CenterOfGravityCalculator.centerOfGravityForAxis(this, axis);
    }

    /**
     * Determines if an object-mask is connected.
     *
     * <p>Adjacent is defined with a <i>big</i> neighborhood definition i.e. with 8 or 26
     * connectivity.
     *
     * @return true if all <i>on</i> voxels in the mask are spatially adjacent to at least one other
     *     <i>on</i> voxel with.
     */
    public boolean checkIfConnected() {
        ObjectCollection objects =
                CONNECTED_COMPONENT_CREATOR.createUnsignedByte(this.binaryVoxels().duplicate());
        return objects.size() <= 1;
    }

    /**
     * Provides a {@link VoxelsPredicate} that finds or counts all <i>on</i> voxels in the
     * object-mask.
     *
     * @return the predicate.
     */
    public VoxelsPredicate voxelsOn() {
        return extract.voxelsEqualTo(binaryValues.getOnInt());
    }

    /**
     * Provides a {@link VoxelsPredicate} that finds or counts all <i>off</i> voxels in the
     * object-mask.
     *
     * @return the predicate.
     */
    public VoxelsPredicate voxelsOff() {
        return extract.voxelsEqualTo(binaryValues.getOffInt());
    }

    /**
     * The number of <i>on</i> voxels on the object-mask.
     *
     * @return the number of voxels that are <i>on</i>.
     */
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

        BinaryValuesInt binaryValuesOut = BinaryValuesInt.getDefault();

        // We initially set all pixels to ON
        BoundedVoxels<UnsignedByteBuffer> voxelsMaskOut =
                VoxelsFactory.getUnsignedByte().createBounded(boxIntersect.get());
        voxelsMaskOut.assignValue(binaryValuesOut.getOnInt()).toAll();

        // Then we set any pixels NOT on either object to OFF..... leaving only the intersecting
        // pixels as <i>on</i> in the output buffer
        voxelsMaskOut
                .assignValue(binaryValuesOut.getOffInt())
                .toEitherTwoObjects(invert(), other.invert(), boxIntersect.get());

        ObjectMask object = new ObjectMask(voxelsMaskOut, binaryValuesOut);

        // If there no pixels left that haven't been set, then the intersection object-mask is zero
        return OptionalUtilities.createFromFlag(object.voxelsOn().anyExists(), object);
    }

    /**
     * Whether a particular point exists within the object-mask?
     *
     * <p>For this to occur, two condition needs to be fulfilled for the {@code point}:
     *
     * <ul>
     *   <li>It exists inside the bounding-box.
     *   <li>The corresponding voxel in {@code #voxels()} has an <i>on</i> state.
     * </ul>
     *
     * @param point the point to query.
     * @return true iff both conditions as true, as per above.
     */
    public boolean contains(Point3i point) {
        if (voxels.boundingBox().contains().point(point)) {
            return extract.voxel(point) == binaryValues.getOnInt();
        } else {
            return false;
        }
    }

    /**
     * A maximum-intensity projection.
     *
     * <p>This flattens across z-dimension, setting a voxel to <i>on</i> if it is <i>on</i> in any
     * one slice.
     *
     * <p>This is an <b>immutable</b> operation.
     *
     * @return a new object-mask flattened in Z dimension.
     */
    public ObjectMask flattenZ() {
        return new ObjectMask(voxels.projectMax());
    }

    /**
     * The bounding-box which gives a location for the object-mask on an image.
     *
     * @return the bounding-box.
     */
    public BoundingBox boundingBox() {
        return voxels.boundingBox();
    }

    /**
     * The underlying voxel memory buffers for the object-mask, exposed via a {@link BinaryVoxels}.
     *
     * @return the voxels.
     */
    public BinaryVoxels<UnsignedByteBuffer> binaryVoxels() {
        return BinaryVoxelsFactory.reuseByte(voxels.voxels(), binaryValues);
    }

    /**
     * The underlying voxel memory buffers for the object-mask, exposed via {@link Voxels}.
     *
     * @return the voxels.
     */
    public Voxels<UnsignedByteBuffer> voxels() {
        return voxels.voxels();
    }

    /**
     * The underlying voxel memory buffers for the object-mask, exposed via {@link BoundedVoxels}.
     *
     * @return the voxels.
     */
    public BoundedVoxels<UnsignedByteBuffer> boundedVoxels() {
        return voxels;
    }

    /**
     * The size of the object-mask's bounding-box across three dimensions.
     *
     * @return the size.
     */
    public Extent extent() {
        return voxels.extent();
    }

    /**
     * Calculates an offset for locating a voxel inside the buffer, with <b>local</b> encoding of
     * coordinates.
     *
     * @param x the <i>X</i>-dimension value for the voxel, relative to the object-mask's
     *     bounding-box.
     * @param y the <i>Y</i>-dimension value for the voxel, relative to the object-mask's
     *     bounding-box.
     * @return the offset to use in the buffer specifying the location of this specific voxel.
     */
    public int offsetRelative(int x, int y) {
        return voxels.extent().offset(x, y);
    }

    /**
     * Calculates an offset for locating a voxel inside the buffer, with <b>global</b> encoding of
     * coordinates.
     *
     * @param x the <i>X</i>-dimension value for the voxel, relative to the entire image scene.
     * @param y the <i>Y</i>-dimension value for the voxel, relative to the entire image scene.
     * @return the offset to use in the buffer specifying the location of this specific voxel.
     */
    public int offsetGlobal(int x, int y) {
        return offsetRelative(x - boundingBox().cornerMin().x(), y - boundingBox().cornerMin().y());
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
     * <p>This will always reuse the existing {@link Voxels}.
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
     * <p>The region outputted will have the same size and coordinates as the bounding-box
     * <i>not</i> the existing {@link ObjectMask}.
     *
     * <p>It will contain the correct object-mask values for the intersecting region, and <i>off</i>
     * values for the rest.
     *
     * <p>A new voxel-buffer is always created for this operation i.e. the existing box is never
     * reused like sometimes in {@link #region}.
     *
     * @param box bounding-box in absolute coordinates, that must at least partially intersect with
     *     the current object-mask bounds.
     * @return a newly created object-mask containing partially some parts of the existing
     *     object-mask as well as <i>off</i> voxels for any other region.
     * @throws CreateException if the boxes do not intersect.
     */
    public ObjectMask regionIntersecting(BoundingBox box) throws CreateException {
        return new ObjectMask(
                voxels.regionIntersecting(box, binaryValues.getOffInt()), this.binaryValues);
    }

    /**
     * Finds any arbitrary <i>on</i> voxel on the object.
     *
     * <p>First it tries the center-of-gravity voxel, and if that's not on, it iterates through the
     * box until it finds an <i>on</i> voxel.
     *
     * <p>This is a DETERMINISTIC operation, so one can rely on the same voxel being found for a
     * given object.
     *
     * @return the location (in absolute coordinates) of an arbitrary <i>on</i> voxel on the object,
     *     if it exists.
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
     * A slice buffer with <i>local</i> coordinates.
     *
     * <p>i.e. with coordinates relative to the bounding-box corner.
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
     * @return a new object-mask reusing the existing voxel-buffers.
     */
    public ObjectMask shiftToOrigin() {
        return mapBoundingBoxPreserveExtent(BoundingBox::shiftToOrigin);
    }

    /**
     * Shifts the object-mask by moving its bounding-box forwards.
     *
     * <p>i.e. by adding {@code shift} from its corner.
     *
     * <p>This is an <i>immutable</i> operation: but beware the existing voxel-buffers are reused in
     * the new object.
     *
     * @param shift what to add from the corner position
     * @return a new object-mask reusing the existing voxel-buffers.
     */
    public ObjectMask shiftBy(ReadableTuple3i shift) {
        return mapBoundingBoxPreserveExtent(box -> box.shiftBy(shift));
    }

    /**
     * Shifts the object-mask by moving its bounding-box backwards.
     *
     * <p>i.e. by subtracting {@code shift} from its corner.
     *
     * <p>This is an <i>immutable</i> operation: but beware the existing voxel-buffers are reused in
     * the new object.
     *
     * @param shift what to subtract from the corner position.
     * @return a new object-mask reusing the existing voxel-buffers.
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
     * @param mapOperation map function to perform mapping of bounding-box.
     * @return a new object-mask with the updated bounding box (but unchanged voxels).
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
     * Assigns the <i>on</i> value to voxels, expecting <i>global</i> coordinates.
     *
     * @return the assigner
     */
    public VoxelsAssigner assignOn() {
        return voxels.assignValue(binaryValues.getOnInt());
    }

    /**
     * Assigns the <i>off</i> value to voxels, expecting <i>global</i> coordinates.
     *
     * @return the assigner
     */
    public VoxelsAssigner assignOff() {
        return voxels.assignValue(binaryValues.getOffInt());
    }

    /**
     * Creates a list of all <i>on</i> voxels as points, using <i>local</i> coordinates i.e.
     * relative to the bounding-box corner.
     *
     * @return a newly created list with newly created points.
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
     *   <li>the number of <i>on</i> voxels on the object
     * </ol>
     */
    @Override
    public String toString() {
        return String.format(
                "Obj%s(cog=%s,numPixels=%d)",
                super.hashCode(), centerOfGravity().toString(), numberVoxelsOn());
    }

    private Interpolator createInterpolator(BinaryValuesInt binaryValues) {
        return InterpolatorFactory.getInstance().binaryResizing(binaryValues.getOffInt());
    }

    /**
     * Applies a function to map the bounding-box to a new-value.
     *
     * <p>The {@link Extent} of the bounding-box should remain constant in this mapping.
     *
     * <p>This is an <i>immutable</i> operation: but beware the existing voxel-buffers are reused in
     * the new object.
     *
     * @param boundingBoxToAssign bounding-box to assign.
     * @return a new object-mask with the updated bounding box (but unchanged voxels).
     */
    private ObjectMask mapBoundingBoxPreserveExtent(BoundingBox boundingBoxToAssign) {
        return new ObjectMask(
                voxels.mapBoundingBoxPreserveExtent(boundingBoxToAssign),
                binaryValues,
                binaryValuesByte);
    }
}
