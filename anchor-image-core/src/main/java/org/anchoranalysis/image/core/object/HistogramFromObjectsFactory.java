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
import org.anchoranalysis.image.voxel.VoxelsUntyped;
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
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Create a {@link Histogram} of the voxel intensity values in an image, pertaining to a region
 * defined by a {@link ObjectMask} or a {@link Mask}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistogramFromObjectsFactory {

    /**
     * Creates a {@link Histogram} of voxel intensity values in {@code voxels}, all or only those in
     * {@code object}.
     *
     * @param voxels the intensity values for the entire scene.
     * @param object if defined, only intensity values corresponding to this {@code object} are
     *     retrieved, otherwise all voxels.
     * @return a newly created {@link Histogram}.
     */
    public static Histogram createFrom(VoxelsUntyped voxels, Optional<ObjectMask> object) {

        if (!isDataTypeSupported(voxels.getVoxelDataType())) {
            throw new IncorrectVoxelTypeException(
                    String.format("Data type %s is not supported", voxels.getVoxelDataType()));
        }

        if (object.isPresent()) {
            return createWithMask(voxels.any(), object.get());
        } else {
            return HistogramFactory.createFrom(voxels);
        }
    }

    /**
     * Creates a {@link Histogram} of voxel intensity values in {@code channel} corresponding to
     * {@code object}.
     *
     * @param channel the channel with voxels.
     * @param object only intensity values corresponding to this {@code object} are retrieved.
     * @return a newly created {@link Histogram}.
     */
    public static Histogram createFrom(Channel channel, ObjectMask object) {
        return createFrom(channel, ObjectCollectionFactory.of(object));
    }

    /**
     * Creates a {@link Histogram} of voxel intensity values in {@code channel} corresponding to
     * {@code objects}.
     *
     * @param channel the channel with voxels.
     * @param objects only intensity values corresponding to {@code objects} are retrieved.
     * @return a newly created {@link Histogram}.
     */
    public static Histogram createFrom(Channel channel, ObjectCollection objects) {
        return createWithMasks(channel.voxels(), objects);
    }

    /**
     * Creates a {@link Histogram} of <i>all</i> voxel intensity values in {@code channel}
     * corresponding to {@link Mask}.
     *
     * @param channel the channel with voxels.
     * @param mask the mask.
     * @return a newly created {@link Histogram}.
     * @throws CreateException if the size of the channel and mask do not match, or a histogram
     *     cannot otherwise created.
     */
    public static Histogram createFrom(Channel channel, Mask mask) throws CreateException {

        if (!channel.extent().equals(mask.extent())) {
            throw new CreateException("Size of channel and mask do not match");
        }

        Histogram total = new Histogram((int) channel.getVoxelDataType().maxValue());

        Histogram histogramForObject =
                createWithMask(channel.voxels().any(), new ObjectMask(mask.binaryVoxels()));
        try {
            total.addHistogram(histogramForObject);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }

        return total;
    }

    private static Histogram createWithMask(Voxels<?> inputBuffer, ObjectMask object) {

        Histogram histogram = new Histogram((int) inputBuffer.dataType().maxValue());

        Extent extent = inputBuffer.extent();

        ReadableTuple3i cornerMin = object.boundingBox().cornerMin();
        ReadableTuple3i cornerMax = object.boundingBox().calculateCornerMax();

        byte matchValue = object.binaryValuesByte().getOn();

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

    private static Histogram createWithMasks(VoxelsUntyped voxels, ObjectCollection objects) {

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
