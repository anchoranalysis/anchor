/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.object;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.statistics.HistogramFactory;
import org.anchoranalysis.math.histogram.Histogram;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistogramFromObjectsFactory {

    public static Histogram create(VoxelsWrapper inputBuffer, Optional<ObjectMask> object) {

        if (!isDataTypeSupported(inputBuffer.getVoxelDataType())) {
            throw new IncorrectVoxelTypeException(
                    String.format("Data type %s is not supported", inputBuffer.getVoxelDataType()));
        }

        if (object.isPresent()) {
            return createWithMask(inputBuffer.any(), object.get());
        } else {
            return HistogramFactory.create(inputBuffer);
        }
    }

    public static Histogram createHistogramIgnoreZero(
            Channel channel, ObjectMask object, boolean ignoreZero) {
        Histogram histogram = create(channel, object);
        if (ignoreZero) {
            histogram.zeroValue(0);
        }
        return histogram;
    }

    public static Histogram create(Channel channel) throws CreateException {

        try {
            return HistogramFactory.create(channel.voxels());
        } catch (IncorrectVoxelTypeException e) {
            throw new CreateException("Cannot create histogram from channel", e);
        }
    }

    public static Histogram create(Channel channel, Mask mask) throws CreateException {

        if (!channel.extent().equals(mask.extent())) {
            throw new CreateException("Size of channel and mask do not match");
        }

        Histogram total = new Histogram((int) channel.getVoxelDataType().maxValue());

        Histogram histogramForObject =
                createWithMask(channel.voxels().any(), new ObjectMask(mask.binaryVoxels()));
        try {
            total.addHistogram(histogramForObject);
        } catch (OperationFailedException e) {
            assert false;
        }

        return total;
    }

    public static Histogram create(Channel channel, ObjectMask object) {
        return create(channel, ObjectCollectionFactory.of(object));
    }

    public static Histogram create(Channel channel, ObjectCollection objects) {
        return createWithMasks(channel.voxels(), objects);
    }

    private static Histogram createWithMask(Voxels<?> inputBuffer, ObjectMask object) {

        Histogram histogram = new Histogram((int) inputBuffer.dataType().maxValue());

        Extent extent = inputBuffer.extent();

        ReadableTuple3i cornerMin = object.boundingBox().cornerMin();
        ReadableTuple3i cornerMax = object.boundingBox().calculateCornerMax();

        byte matchValue = object.binaryValuesByte().getOnByte();

        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            VoxelBuffer<?> buffer = inputBuffer.slice(z);
            UnsignedByteBuffer bufferMask = object.sliceBufferGlobal(z);

            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {

                    int offset = extent.offset(x, y);
                    int offsetMask = object.offsetGlobal(x, y);

                    byte valueOnMask = bufferMask.getRaw(offsetMask);

                    if (valueOnMask == matchValue) {
                        int val = buffer.getInt(offset);
                        histogram.incrementValue(val);
                    }
                }
            }
        }
        return histogram;
    }

    private static Histogram createWithMasks(VoxelsWrapper voxels, ObjectCollection objects) {

        Histogram total = new Histogram((int) voxels.getVoxelDataType().maxValue());

        try {
            for (ObjectMask objectMask : objects) {
                Histogram histogram = createWithMask(voxels.any(), objectMask);
                total.addHistogram(histogram);
            }

        } catch (OperationFailedException e) {
            assert false;
        }

        return total;
    }

    private static boolean isDataTypeSupported(VoxelDataType dataType) {
        return dataType.equals(UnsignedByteVoxelType.INSTANCE)
                || dataType.equals(UnsignedShortVoxelType.INSTANCE);
    }
}
