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
import lombok.Setter;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.thresholder.VoxelBoxThresholder;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

/**
 * A channel that is restricted to two values (ON and OFF) so as to act like a mask.
 *
 * <p>This is one of Anchor's core data-objects.
 *
 * @author Owen Feehan
 */
public class Mask {

    /**
     * The underlying channel which contains the binary-values. It is always has data-type of
     * unsigned 8-bit.
     */
    @Getter @Setter private Channel channel;

    @Getter private final BinaryValues binaryValues;

    private final BinaryValuesByte binaryValuesByte;

    public Mask(Channel channel, BinaryValues binaryValues) {
        this.channel = channel;
        this.binaryValues = binaryValues;
        this.binaryValuesByte = binaryValues.createByte();

        if (!channel.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            throw new IncorrectVoxelDataTypeException(
                    "Only unsigned 8-bit data type is supported for BinaryChnl");
        }
    }

    public Mask(BinaryVoxelBox<ByteBuffer> voxelBox) {
        this(
                voxelBox,
                new ImageResolution(),
                ChannelFactory.instance().get(VoxelDataTypeUnsignedByte.INSTANCE));
    }

    public Mask(
            BinaryVoxelBox<ByteBuffer> vb, ImageResolution res, ChannelFactorySingleType factory) {
        this.channel = factory.create(vb.getVoxelBox(), res);
        this.binaryValues = vb.getBinaryValues();
        this.binaryValuesByte = binaryValues.createByte();
    }

    public ImageDimensions getDimensions() {
        return channel.getDimensions();
    }

    public VoxelBox<ByteBuffer> getVoxelBox() {
        try {
            return channel.getVoxelBox().asByte();
        } catch (IncorrectVoxelDataTypeException e) {
            throw new IncorrectVoxelDataTypeException(
                    "Associated imgChnl does contain have unsigned 8-bit data (byte)");
        }
    }

    public BinaryVoxelBox<ByteBuffer> binaryVoxelBox() {
        return new BinaryVoxelBoxByte(getVoxelBox(), binaryValues);
    }

    public boolean isPointOn(Point3i point) {
        ByteBuffer bb = getVoxelBox().getPixelsForPlane(point.getZ()).buffer();

        int offset = getVoxelBox().extent().offset(point.getX(), point.getY());

        return bb.get(offset) == binaryValuesByte.getOnByte();
    }

    public Mask duplicate() {
        return new Mask(channel.duplicate(), binaryValues);
    }

    // Creates a mask from the binaryChnl
    public ObjectMask region(BoundingBox bbox, boolean reuseIfPossible) {
        Preconditions.checkArgument(channel.getDimensions().contains(bbox));
        return new ObjectMask(
                bbox, channel.getVoxelBox().asByte().region(bbox, reuseIfPossible), binaryValues);
    }

    public Mask maxIntensityProj() {
        return new Mask(channel.maxIntensityProjection(), binaryValues);
    }

    public boolean hasHighValues() {
        return channel.hasEqualTo(binaryValues.getOnInt());
    }

    public int countHighValues() {
        return channel.countEqualTo(binaryValues.getOnInt());
    }

    public Mask scaleXY(ScaleFactor scaleFactor, Interpolator interpolator) {

        if (scaleFactor.isNoScale()) {
            // Nothing to do
            return this;
        }

        Channel scaled = this.channel.scaleXY(scaleFactor, interpolator);

        Mask binaryChnl = new Mask(scaled, binaryValues);

        // We threshold to make sure it's still binary
        applyThreshold(binaryChnl);

        return binaryChnl;
    }

    public Mask extractSlice(int z) {
        return new Mask(channel.extractSlice(z), binaryValues);
    }

    public void replaceBy(BinaryVoxelBox<ByteBuffer> bvb) throws IncorrectImageSizeException {
        channel.getVoxelBox().asByte().replaceBy(bvb.getVoxelBox());
    }

    private void applyThreshold(Mask binaryChnl) {
        int thresholdVal = (binaryValues.getOnInt() + binaryValues.getOffInt()) / 2;

        VoxelBoxThresholder.thresholdForLevel(
                binaryChnl.getVoxelBox(), thresholdVal, binaryChnl.getBinaryValues().createByte());
    }
}