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

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.convert.attached.ChannelConverterAttached;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.convert.UnsignedBufferAsInt;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.convert.VoxelsConverter;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;

@AllArgsConstructor
public class RegionExtracterFromDisplayStack implements RegionExtracter {

    /** Used to convert our source buffer to bytes, not called if it's already bytes */
    private List<Optional<ChannelConverterAttached<Channel, UnsignedByteBuffer>>>
            listChannelConverter;

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
            Optional<VoxelsConverter<UnsignedByteBuffer>> channelConverter)
            throws OperationFailedException {

        ScaleFactor sf = new ScaleFactor(zoomFactor);

        // We calculate how big our outgoing voxels will be
        Dimensions dimensions = extractedSlice.dimensions().scaleXYBy(sf);

        Extent extentTarget = box.extent().scaleXYBy(sf);

        Voxels<UnsignedByteBuffer> voxels = VoxelsFactory.getUnsignedByte().createInitialized(extentTarget);

        Optional<MeanInterpolator> interpolator = OptionalUtilities.createFromFlag(zoomFactor < 1, () -> new MeanInterpolator(zoomFactor));

        if (extractedSlice.getVoxelDataType().equals(UnsignedByteVoxelType.INSTANCE)) {
            interpolateRegion(
                    extractedSlice.voxels().asByte(),
                    voxels,
                    extractedSlice.extent(),
                    extentTarget,
                    box,
                    zoomFactor,
                    interpolator);

            if (channelConverter.isPresent()) {
                channelConverter.get().copyFromUnsignedByte(voxels, voxels);
            }

        } else if (extractedSlice.getVoxelDataType().equals(UnsignedShortVoxelType.INSTANCE)
                && channelConverter.isPresent()) {

            Voxels<UnsignedShortBuffer> bufferIntermediate =
                    VoxelsFactory.getUnsignedShort().createInitialized(extentTarget);
            interpolateRegion(
                    extractedSlice.voxels().asShort(),
                    bufferIntermediate,
                    extractedSlice.dimensions().extent(),
                    extentTarget,
                    box,
                    zoomFactor,
                    interpolator);

            // We now convert the ShortBuffer into bytes
            channelConverter.get().copyFromUnsignedShort(bufferIntermediate, voxels);

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
    
    private static <T extends UnsignedBufferAsInt> void interpolateRegion(
            Voxels<T> from,
            Voxels<T> to,
            Extent extentSource,
            Extent extentTarget,
            BoundingBox box,
            double zoomFactor,
            Optional<MeanInterpolator> interpolator)
            throws OperationFailedException {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMax = box.calculateCornerMax();
        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            assert (from.slice(z) != null);
            assert (to.slice(z - cornerMin.z()) != null);

            VoxelBuffer<T> sliceFrom = from.slice(z);
            
            VoxelBuffer<T> sliceTo = to.slice(z - cornerMin.z());

            // We go through every pixel in the new width, and height, and sample from the original
            // image
            int index = 0;
            for (int y = 0; y < extentTarget.y(); y++) {

                int yOriginal = scaleToOriginal(y,zoomFactor) + cornerMin.y();
                for (int x = 0; x < extentTarget.x(); x++) {

                    int xOriginal = scaleToOriginal(x,zoomFactor) + cornerMin.x();

                    Point2i point = new Point2i(xOriginal, yOriginal);
                    
                    transferPoint(interpolator, point, index, sliceFrom, sliceTo, extentSource);
                    
                    index++;
                }
            }
        }
    }
    
    private static int scaleToOriginal(int valueUnscaled, double zoomFactor) {
        return (int) (valueUnscaled / zoomFactor);
    }
    
    private static <T extends UnsignedBufferAsInt> void transferPoint(Optional<MeanInterpolator> interpolator, Point2i point, int sourceIndex, VoxelBuffer<T> sliceFrom, VoxelBuffer<T> sliceTo, Extent extentSource) throws OperationFailedException {
        if (interpolator.isPresent()) {
            double value = interpolator.get().interpolateVoxelsAt(point, sliceFrom.buffer(), extentSource);
            sliceTo.buffer().putDouble(sourceIndex, value);
        } else {
            sliceTo.transferFrom(sourceIndex, sliceFrom, extentSource.offset(point));
        }
    }
}
