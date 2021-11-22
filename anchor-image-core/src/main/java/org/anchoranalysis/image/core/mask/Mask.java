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

package org.anchoranalysis.image.core.mask;

import com.google.common.base.Preconditions;
import java.util.Optional;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesInt;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.extracter.predicate.VoxelsPredicate;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.interpolator.InterpolatorFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.thresholder.VoxelsThresholder;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * A channel whose voxel-values are restricted to two states (<i>on</i> and<i>off</i>).
 *
 * <p>This is one of Anchor's core data-objects.
 *
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class Mask {

    private static final ChannelFactorySingleType FACTORY =
            ChannelFactory.instance().get(UnsignedByteVoxelType.INSTANCE);

    /**
     * The underlying channel which contains the binary-values. It is always has data-type of
     * unsigned 8-bit.
     */
    @Getter private Channel channel;

    /**
     * The two states which are permitted to be assigned to the voxels, stored as <i>unsigned
     * int</i>s.
     */
    @Getter private final BinaryValuesInt binaryValuesInt;

    /** The two states which are permitted to be assigned to the voxels, stored as <i>byte</i>s. */
    @Getter private final BinaryValuesByte binaryValuesByte;

    /**
     * Interpolator used for resizing the mask (making sure to use an out-of-bounds strategy of
     * <i>off</i> voxels).
     */
    private final Interpolator interpolator;

    /**
     * Creates a mask from an existing channel using default values for <i>off</i> (0) and <i>on</i>
     * (255)
     *
     * <p>The channel should have maximally two distinct intensity values, represeting <i>off</i>
     * and <i>on</i> states.
     *
     * <p>Precondition: no check occurs that only <i>off</i> and <i>on</i> voxels exist in a
     * channel, so please call only with valid input.
     *
     * @param channel the channel to form the mask, whose voxel-buffer is reused internally in the
     *     mask
     */
    public Mask(Channel channel) {
        this(channel, BinaryValuesInt.getDefault());
    }

    /**
     * Creates a mask from an existing channel
     *
     * <p>The channel should have maximally two distinct intensity values, represeting <i>off</i>
     * and ON states.
     *
     * <p>Precondition: no check occurs that only <i>off</i> and <i>on</i> voxels exist in a
     * channel, so please call only with valid input.
     *
     * @param channel the channel to form the mask, whose voxel-buffer is reused internally in the
     *     mask
     * @param binaryValues how to identify the <i>off</i> and <i>on</i> states from intensity
     *     voxel-values
     */
    public Mask(Channel channel, BinaryValuesInt binaryValues) {
        this.channel = channel;
        this.binaryValuesInt = binaryValues;
        this.binaryValuesByte = binaryValues.asByte();

        if (!channel.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException(
                    "Only unsigned 8-bit data type is supported for mask");
        }

        this.interpolator = createInterpolator(binaryValues);
    }

    /**
     * Creates a mask from an existing binary-voxels using default image resolution
     *
     * @param voxels the {@link BinaryVoxels} to be reused as the internal buffer of the mask
     */
    public Mask(BinaryVoxels<UnsignedByteBuffer> voxels) {
        this(voxels, Optional.empty());
    }

    /**
     * Creates a mask from an existing binary-voxels and a specific image resolution.
     *
     * @param voxels the binary-voxels to be reused as the internal buffer of the mask
     * @param resolution the image-resolution to assign
     */
    public Mask(BinaryVoxels<UnsignedByteBuffer> voxels, Optional<Resolution> resolution) {
        this.channel = FACTORY.create(voxels.voxels(), resolution);
        this.binaryValuesInt = voxels.binaryValues();
        this.binaryValuesByte = binaryValuesInt.asByte();

        this.interpolator = createInterpolator(binaryValuesInt);
    }

    /**
     * Creates a new empty mask of particular dimensions and with particular {@link
     * BinaryValuesInt}.
     *
     * <p>Default mask values for <i>off</i> (0) and <i>on</i> (255) are employed.
     *
     * @param dimensions the dimensions for the newly-created mask
     * @param binaryValues the binary-values to use for the newly created mask
     */
    public Mask(Dimensions dimensions, BinaryValuesInt binaryValues) {
        this(FACTORY.createEmptyInitialised(dimensions), binaryValues);
    }

    /**
     * The size and voxel-resolution of the channel.
     *
     * @return the dimensions.
     */
    public Dimensions dimensions() {
        return channel.dimensions();
    }

    /**
     * Resolution of voxels to physical measurements.
     *
     * <p>e.g. physical size of each voxel in a particular dimension.
     *
     * @return the image-resolution.
     */
    public Optional<Resolution> resolution() {
        return dimensions().resolution();
    }

    /**
     * The underlying voxels in the mask.
     *
     * @return the voxels.
     */
    public Voxels<UnsignedByteBuffer> voxels() {
        try {
            return channel.voxels().asByte();
        } catch (IncorrectVoxelTypeException e) {
            throw new IncorrectVoxelTypeException(
                    "Associated imgChannel does contain have unsigned 8-bit data (byte)");
        }
    }

    /**
     * The underlying voxels in the mask, exposed as {@link BinaryVoxels}.
     *
     * @return the voxels, with associated binary-values.
     */
    public BinaryVoxels<UnsignedByteBuffer> binaryVoxels() {
        return BinaryVoxelsFactory.reuseByte(voxels(), binaryValuesInt);
    }

    /**
     * Does a particular voxel have the <i>on</i> state?
     *
     * @param point the point indicating which voxel to check.
     * @return true if this voxel has an on state, false otherwise.
     */
    public boolean isVoxelOn(Point3i point) {
        UnsignedByteBuffer buffer = voxels().sliceBuffer(point.z());

        int offset = voxels().extent().offsetSlice(point);

        return buffer.getRaw(offset) == binaryValuesByte.getOn();
    }

    /**
     * Deep-copies the object.
     *
     * @return a deep-copy of the current object.
     */
    public Mask duplicate() {
        return new Mask(channel.duplicate(), binaryValuesInt);
    }

    /**
     * Creates an {@link ObjectMask} corresponding to the on/off state in a bounding-box.
     *
     * @param box the bounding-box.
     * @param reuseIfPossible if true, the existing boxels will be reused if possible (e.g. if the
     *     box refers to the entire image). if false, new voxel memory will always be allocated.
     * @return the derived {@link ObjectMask}.
     */
    public ObjectMask region(BoundingBox box, boolean reuseIfPossible) {
        Preconditions.checkArgument(channel.dimensions().contains(box));
        return new ObjectMask(
                box,
                channel.voxels().asByte().extract().region(box, reuseIfPossible),
                binaryValuesInt);
    }

    /**
     * Creates an otherwise identical {@link Mask} but a maximum-intensity-projection applied to the
     * z-dimension.
     *
     * @return a newly created mask, as above.
     */
    public Mask flattenZ() {
        return new Mask(channel.projectMax(), binaryValuesInt);
    }

    /**
     * Operations on whether particular voxels are <i>on</i>.
     *
     * @return a newly instantiated object to perform queries on voxels who fulfill the above
     *     condition.
     */
    public VoxelsPredicate voxelsOn() {
        return channel.voxelsEqualTo(binaryValuesInt.getOn());
    }

    /**
     * Operations on whether particular voxels are <i>off</i>.
     *
     * @return a newly instantiated object to perform queries on voxels who fulfill the above
     *     condition.
     */
    public VoxelsPredicate voxelsOff() {
        return channel.voxelsEqualTo(binaryValuesInt.getOff());
    }

    /**
     * Creates a new {@Mask} whose X- and Y- dimensions are scaled by {@code scaleFactor}.
     *
     * @param scaleFactor how to the scale the X- and Y- dimensions.
     * @return a newly created {@Mask} as above, except if {@code scaleFactor} is effectively 1, in
     *     which case the existing {@link Mask} is reused.
     */
    public Mask scaleXY(ScaleFactor scaleFactor) {

        if (scaleFactor.isNoScale()) {
            // Nothing to do
            return this;
        }

        Channel scaled = this.channel.scaleXY(scaleFactor, interpolator);

        Mask mask = new Mask(scaled, binaryValuesInt);

        // We threshold to make sure it's still binary
        applyThreshold(mask);

        return mask;
    }

    /**
     * Creates a new {@link Mask} containing only one particular slice.
     *
     * <p>The existing {@link Voxels} are reused, without creating new buffers.
     *
     * @param z the index of the slice to extract (index in z-dimension)
     * @return a newly created {@link Mask} consisting of the slice at {@code sliceIndex} only.
     */
    public Mask extractSlice(int z) {
        return new Mask(channel.extractSlice(z), binaryValuesInt);
    }

    /**
     * Replaces the underlying voxels in the mask with new voxels.
     *
     * <p>The resolution and binary-values remain unchanged.
     *
     * @param voxels the new voxels to assign.
     * @throws IncorrectImageSizeException if the size of {@code voxels} is not identical to the
     *     existing boxels.
     */
    public void replaceBy(BinaryVoxels<UnsignedByteBuffer> voxels)
            throws IncorrectImageSizeException {
        channel.replaceVoxels(voxels.voxels());
    }

    /**
     * Interface that allows assignment of an <i>on</i> state to all or subsets of the voxels.
     *
     * @return the interface.
     */
    public VoxelsAssigner assignOn() {
        return channel.assignValue(binaryValuesInt.getOn());
    }

    /**
     * Interface that allows assignment of an <i>off</i> state to all or subsets of the voxels.
     *
     * @return the interface.
     */
    public VoxelsAssigner assignOff() {
        return channel.assignValue(binaryValuesInt.getOff());
    }

    /**
     * A buffer corresponding to a particular z-slice.
     *
     * <p>This buffer is either a NIO class or another class that wraps the underlying array storing
     * voxel intensities.
     *
     * @param z the index (beginning at 0) of all z-slices.
     * @return the corresponding buffer for {@code z}.
     */
    public UnsignedByteBuffer sliceBuffer(int z) {
        return channel.voxels().asByte().sliceBuffer(z);
    }

    @Override
    public boolean equals(Object other) {
        return binaryValuesInt.equals(other);
    }

    @Override
    public int hashCode() {
        return binaryValuesInt.hashCode();
    }

    /**
     * The size of the voxels across three dimensions.
     *
     * @return the size.
     */
    public Extent extent() {
        return channel.extent();
    }

    private void applyThreshold(Mask mask) {
        int thresholdVal = (binaryValuesInt.getOn() + binaryValuesInt.getOff()) / 2;

        VoxelsThresholder.thresholdByte(mask.voxels(), thresholdVal, mask.binaryValuesByte());
    }

    private Interpolator createInterpolator(BinaryValuesInt binaryValues) {
        return InterpolatorFactory.getInstance().binaryResizing(binaryValues.getOff());
    }
}
