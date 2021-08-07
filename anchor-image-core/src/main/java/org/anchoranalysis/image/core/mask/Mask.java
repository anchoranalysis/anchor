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
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.extracter.predicate.VoxelsPredicate;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.interpolator.InterpolatorFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.thresholder.VoxelsThresholder;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * A channel that is restricted to two values (ON and OFF) so as to act like a mask.
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

    @Getter private final BinaryValues binaryValues;

    private final BinaryValuesByte binaryValuesByte;

    /**
     * Interpolator used for resizing the mask (making sure to use an out-of-bounds strategy of OFF
     * voxels)
     */
    private final Interpolator interpolator;

    /**
     * Creates a mask from an existing channel using default values for OFF (0) and ON (255)
     *
     * <p>The channel should have maximally two distinct intensity values, represeting OFF and ON
     * states
     *
     * <p>Precondition: no check occurs that only OFF and ON voxels exist in a channel, so please
     * call only with valid input.
     *
     * @param channel the channel to form the mask, whose voxel-buffer is reused internally in the
     *     mask
     */
    public Mask(Channel channel) {
        this(channel, BinaryValues.getDefault());
    }

    /**
     * Creates a mask from an existing channel
     *
     * <p>The channel should have maximally two distinct intensity values, represeting OFF and ON
     * states.
     *
     * <p>Precondition: no check occurs that only OFF and ON voxels exist in a channel, so please
     * call only with valid input.
     *
     * @param channel the channel to form the mask, whose voxel-buffer is reused internally in the
     *     mask
     * @param binaryValues how to identify the OFF and ON states from intensity voxel-values
     */
    public Mask(Channel channel, BinaryValues binaryValues) {
        this.channel = channel;
        this.binaryValues = binaryValues;
        this.binaryValuesByte = binaryValues.createByte();

        if (!channel.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
            throw new IncorrectVoxelTypeException(
                    "Only unsigned 8-bit data type is supported for mask");
        }

        this.interpolator = createInterpolator(binaryValues);
    }

    /**
     * Creates a mask from an existing binary-voxels using default image resolution
     *
     * @param voxels the binary-voxels to be reused as the internal buffer of the mask
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
        this.binaryValues = voxels.binaryValues();
        this.binaryValuesByte = binaryValues.createByte();

        this.interpolator = createInterpolator(binaryValues);
    }

    /**
     * Creates a new empty mask of particular dimensions and with particular binaryvalues
     *
     * <p>Default mask values for OFF (0) and ON (255) are employed.
     *
     * @param dimensions the dimensions for the newly-created mask
     * @param binaryValues the binary-values to use for the newly created mask
     */
    public Mask(Dimensions dimensions, BinaryValues binaryValues) {
        this(FACTORY.createEmptyInitialised(dimensions), binaryValues);
    }

    public Dimensions dimensions() {
        return channel.dimensions();
    }

    public Optional<Resolution> resolution() {
        return dimensions().resolution();
    }

    public Voxels<UnsignedByteBuffer> voxels() {
        try {
            return channel.voxels().asByte();
        } catch (IncorrectVoxelTypeException e) {
            throw new IncorrectVoxelTypeException(
                    "Associated imgChannel does contain have unsigned 8-bit data (byte)");
        }
    }

    public BinaryVoxels<UnsignedByteBuffer> binaryVoxels() {
        return BinaryVoxelsFactory.reuseByte(voxels(), binaryValues);
    }

    public boolean isPointOn(Point3i point) {
        UnsignedByteBuffer buffer = voxels().sliceBuffer(point.z());

        int offset = voxels().extent().offsetSlice(point);

        return buffer.getRaw(offset) == binaryValuesByte.getOnByte();
    }

    public Mask duplicate() {
        return new Mask(channel.duplicate(), binaryValues);
    }

    public ObjectMask region(BoundingBox box, boolean reuseIfPossible) {
        Preconditions.checkArgument(channel.dimensions().contains(box));
        return new ObjectMask(
                box,
                channel.voxels().asByte().extract().region(box, reuseIfPossible),
                binaryValues);
    }

    public Mask flattenZ() {
        return new Mask(channel.projectMax(), binaryValues);
    }

    public VoxelsPredicate voxelsOn() {
        return channel.voxelsEqualTo(binaryValues.getOnInt());
    }

    public VoxelsPredicate voxelsOff() {
        return channel.voxelsEqualTo(binaryValues.getOffInt());
    }

    public Mask scaleXY(ScaleFactor scaleFactor) {

        if (scaleFactor.isNoScale()) {
            // Nothing to do
            return this;
        }

        Channel scaled = this.channel.scaleXY(scaleFactor, interpolator);

        Mask mask = new Mask(scaled, binaryValues);

        // We threshold to make sure it's still binary
        applyThreshold(mask);

        return mask;
    }

    public Mask extractSlice(int z) {
        return new Mask(channel.extractSlice(z), binaryValues);
    }

    public void replaceBy(BinaryVoxels<UnsignedByteBuffer> voxels)
            throws IncorrectImageSizeException {
        channel.replaceVoxels(voxels.voxels());
    }

    public VoxelsAssigner assignOn() {
        return channel.assignValue(binaryValues.getOnInt());
    }

    public VoxelsAssigner assignOff() {
        return channel.assignValue(binaryValues.getOffInt());
    }

    public UnsignedByteBuffer sliceBuffer(int z) {
        return channel.voxels().asByte().sliceBuffer(z);
    }

    public int getOffInt() {
        return binaryValues.getOffInt();
    }

    public int getOnInt() {
        return binaryValues.getOnInt();
    }

    public boolean equals(Object o) {
        return binaryValues.equals(o);
    }

    public int hashCode() {
        return binaryValues.hashCode();
    }

    public BinaryValuesByte createByte() {
        return binaryValues.createByte();
    }

    public BinaryValues createInverted() {
        return binaryValues.createInverted();
    }

    public String toString() {
        return binaryValues.toString();
    }

    public byte getOffByte() {
        return binaryValuesByte.getOffByte();
    }

    public byte getOnByte() {
        return binaryValuesByte.getOnByte();
    }

    public Extent extent() {
        return channel.extent();
    }

    private void applyThreshold(Mask mask) {
        int thresholdVal = (binaryValues.getOnInt() + binaryValues.getOffInt()) / 2;

        VoxelsThresholder.thresholdForLevelByte(
                mask.voxels(), thresholdVal, mask.binaryValues().createByte());
    }

    private Interpolator createInterpolator(BinaryValues binaryValues) {
        return InterpolatorFactory.getInstance().binaryResizing(binaryValues.getOffInt());
    }
}
