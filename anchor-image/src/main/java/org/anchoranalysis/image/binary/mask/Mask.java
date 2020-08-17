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

package org.anchoranalysis.image.binary.mask;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelsFactory;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsPredicate;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.thresholder.VoxelsThresholder;

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
            ChannelFactory.instance().get(VoxelDataTypeUnsignedByte.INSTANCE);

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
     * Constructor - creates a mask from an existing channel using default values for OFF (0) and ON
     * (255)
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
     * Constructor - creates a mask from an existing channel
     *
     * <p>The channel should have maximally two distinct intensity values, represeting OFF and ON
     * states
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

        if (!channel.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException(
                    "Only unsigned 8-bit data type is supported for BinaryChnl");
        }

        this.interpolator = createInterpolator(binaryValues);
    }

    /**
     * Constructor - creates a mask from an existing binary-voxels using default image resolution
     *
     * @param voxels the binary-voxels to be reused as the internal buffer of the mask
     */
    public Mask(BinaryVoxels<ByteBuffer> voxels) {
        this(voxels, new ImageResolution());
    }

    /**
     * Constructor - creates a mask from an existing binary-voxels using default image resolution
     *
     * @param voxels the binary-voxels to be reused as the internal buffer of the mask
     * @param resolution the image-resolution to assign
     */
    public Mask(BinaryVoxels<ByteBuffer> voxels, ImageResolution resolution) {
        this.channel = FACTORY.create(voxels.voxels(), resolution);
        this.binaryValues = voxels.binaryValues();
        this.binaryValuesByte = binaryValues.createByte();

        this.interpolator = createInterpolator(binaryValues);
    }

    /**
     * Constructor - creates a new empty mask of particular dimensions and with particular
     * binaryvalues
     *
     * <p>Default mask values for OFF (0) and ON (255) are employed.
     *
     * @param dimensions the dimensions for the newly-created mask
     * @param binaryValues the binary-values to use for the newly created mask
     */
    public Mask(ImageDimensions dimensions, BinaryValues binaryValues) {
        this(FACTORY.createEmptyInitialised(dimensions), binaryValues);
    }

    public ImageDimensions dimensions() {
        return channel.dimensions();
    }

    public Voxels<ByteBuffer> voxels() {
        try {
            return channel.voxels().asByte();
        } catch (IncorrectVoxelDataTypeException e) {
            throw new IncorrectVoxelDataTypeException(
                    "Associated imgChnl does contain have unsigned 8-bit data (byte)");
        }
    }

    public BinaryVoxels<ByteBuffer> binaryVoxels() {
        return BinaryVoxelsFactory.reuseByte(voxels(), binaryValues);
    }

    public boolean isPointOn(Point3i point) {
        ByteBuffer bb = voxels().sliceBuffer(point.z());

        int offset = voxels().extent().offsetSlice(point);

        return bb.get(offset) == binaryValuesByte.getOnByte();
    }

    public Mask duplicate() {
        return new Mask(channel.duplicate(), binaryValues);
    }

    public ObjectMask region(BoundingBox box, boolean reuseIfPossible) {
        Preconditions.checkArgument(channel.dimensions().contains(box));
        return new ObjectMask(
                box,
                channel.voxels().asByte().extracter().region(box, reuseIfPossible),
                binaryValues);
    }

    public Mask flattenZ() {
        return new Mask(channel.maxIntensityProjection(), binaryValues);
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

    public void replaceBy(BinaryVoxels<ByteBuffer> voxels) throws IncorrectImageSizeException {
        channel.replaceVoxels(voxels.voxels());
    }

    public VoxelsAssigner assignOn() {
        return channel.assignValue(binaryValues.getOnInt());
    }

    public VoxelsAssigner assignOff() {
        return channel.assignValue(binaryValues.getOffInt());
    }

    private void applyThreshold(Mask mask) {
        int thresholdVal = (binaryValues.getOnInt() + binaryValues.getOffInt()) / 2;

        VoxelsThresholder.thresholdForLevel(
                mask.voxels(), thresholdVal, mask.binaryValues().createByte());
    }

    private Interpolator createInterpolator(BinaryValues binaryValues) {
        return InterpolatorFactory.getInstance().binaryResizing(binaryValues.getOffInt());
    }
}
