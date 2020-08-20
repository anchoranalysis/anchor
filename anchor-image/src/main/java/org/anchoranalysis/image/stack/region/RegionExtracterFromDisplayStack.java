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

package org.anchoranalysis.image.stack.region;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.converter.attached.ChannelConverterAttached;
import org.anchoranalysis.image.channel.converter.voxels.VoxelsConverter;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;

@AllArgsConstructor
public class RegionExtracterFromDisplayStack implements RegionExtracter {

    /** Used to convert our source buffer to bytes, not called if it's already bytes */
    private List<Optional<ChannelConverterAttached<Channel, ByteBuffer>>> listChannelConverter;

    /** Current displayStack */
    private Stack stack;

    @Override
    public DisplayStack extractRegionFrom(BoundingBox box, double zoomFactor)
            throws OperationFailedException {

        Stack out = null;
        for (int c = 0; c < stack.getNumberChannels(); c++) {

            Channel channel =
                    extractRegionFrom(
                            stack.getChannel(c),
                            box,
                            zoomFactor,
                            listChannelConverter
                                    .get(c)
                                    .map(ChannelConverterAttached::getVoxelsConverter));

            if (c == 0) {
                out = new Stack(channel);
            } else {
                try {
                    out.addChannel(channel);
                } catch (IncorrectImageSizeException e) {
                    assert false;
                }
            }
        }
        try {
            return DisplayStack.create(out);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    // TODO put in some form of interpolation when zoomFactor<1
    private Channel extractRegionFrom(
            Channel extractedSlice,
            BoundingBox box,
            double zoomFactor,
            Optional<VoxelsConverter<ByteBuffer>> channelConverter)
            throws OperationFailedException {

        ScaleFactor sf = new ScaleFactor(zoomFactor);

        // We calculate how big our outgoing voxels will be
        Dimensions dimensions = extractedSlice.dimensions().scaleXYBy(sf);

        Extent extentTrgt = box.extent().scaleXYBy(sf);

        Voxels<ByteBuffer> voxels = VoxelsFactory.getByte().createInitialized(extentTrgt);

        MeanInterpolator interpolator = (zoomFactor < 1) ? new MeanInterpolator(zoomFactor) : null;

        if (extractedSlice.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
            interpolateRegionFromByte(
                    extractedSlice.voxels().asByte(),
                    voxels,
                    extractedSlice.extent(),
                    extentTrgt,
                    box,
                    zoomFactor,
                    interpolator);

            if (channelConverter.isPresent()) {
                channelConverter.get().convertFromByte(voxels, voxels);
            }

        } else if (extractedSlice.getVoxelDataType().equals(UnsignedShortVoxelType.INSTANCE)
                && channelConverter.isPresent()) {

            Voxels<ShortBuffer> bufferIntermediate =
                    VoxelsFactory.getShort().createInitialized(extentTrgt);
            interpolateRegionFromShort(
                    extractedSlice.voxels().asShort(),
                    bufferIntermediate,
                    extractedSlice.dimensions().extent(),
                    extentTrgt,
                    box,
                    zoomFactor,
                    interpolator);

            // We now convert the ShortBuffer into bytes
            channelConverter.get().convertFromShort(bufferIntermediate, voxels);

        } else {
            throw new IncorrectVoxelTypeException(
                    String.format(
                            "dataType %s is unsupported without channelConverter",
                            extractedSlice.getVoxelDataType()));
        }

        return ChannelFactory.instance()
                .get(UnsignedByteVoxelType.INSTANCE)
                .create(voxels, dimensions.resolution());
    }

    // extentTrgt is the target-size (where we write this region)
    // extentSrcSlice is the source-size (the single slice we've extracted from the buffer to
    // interpolate from)
    private static void interpolateRegionFromByte(
            Voxels<ByteBuffer> voxelsSrc,
            Voxels<ByteBuffer> voxelsDest,
            Extent extentSrc,
            Extent extentTrgt,
            BoundingBox box,
            double zoomFactor,
            MeanInterpolator interpolator)
            throws OperationFailedException {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();
        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            ByteBuffer bbIn = voxelsSrc.sliceBuffer(z);
            ByteBuffer bbOut = voxelsDest.sliceBuffer(z - cornerMin.z());

            // We go through every pixel in the new width, and height, and sample from the original
            // image
            int indOut = 0;
            for (int y = 0; y < extentTrgt.y(); y++) {

                int yOrig = ((int) (y / zoomFactor)) + cornerMin.y();
                for (int x = 0; x < extentTrgt.x(); x++) {

                    int xOrig = ((int) (x / zoomFactor)) + cornerMin.x();

                    // We get the byte to write
                    byte b =
                            (interpolator != null)
                                    ? interpolator.getInterpolatedPixelByte(
                                            xOrig, yOrig, bbIn, extentSrc)
                                    : bbIn.get(extentSrc.offset(xOrig, yOrig));

                    bbOut.put(indOut++, b);
                }
            }
        }
    }

    // extentTrgt is the target-size (where we write this region)
    // extentSrcSlice is the source-size (the single slice we've extracted from the buffer to
    // interpolate from)
    private static void interpolateRegionFromShort(
            Voxels<ShortBuffer> voxelsSrc,
            Voxels<ShortBuffer> voxelsDest,
            Extent extentSrc,
            Extent extentTrgt,
            BoundingBox box,
            double zoomFactor,
            MeanInterpolator interpolator)
            throws OperationFailedException {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();
        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            assert (voxelsSrc.slice(z) != null);
            assert (voxelsDest.slice(z - cornerMin.z()) != null);

            ShortBuffer bbIn = voxelsSrc.sliceBuffer(z);
            ShortBuffer bbOut = voxelsDest.slice(z - cornerMin.z()).buffer();

            // We go through every pixel in the new width, and height, and sample from the original
            // image
            int indOut = 0;
            for (int y = 0; y < extentTrgt.y(); y++) {

                int yOrig = ((int) (y / zoomFactor)) + cornerMin.y();
                for (int x = 0; x < extentTrgt.x(); x++) {

                    int xOrig = ((int) (x / zoomFactor)) + cornerMin.x();

                    // We get the byte to write
                    short s =
                            (interpolator != null)
                                    ? interpolator.getInterpolatedPixelShort(
                                            xOrig, yOrig, bbIn, extentSrc)
                                    : bbIn.get(extentSrc.offset(xOrig, yOrig));

                    bbOut.put(indOut++, s);
                }
            }
        }
    }
}
