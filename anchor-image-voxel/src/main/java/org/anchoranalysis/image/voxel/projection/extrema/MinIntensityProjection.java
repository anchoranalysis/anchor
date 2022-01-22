/*-
 * #%L
 * anchor-image-voxel
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
package org.anchoranalysis.image.voxel.projection.extrema;

import java.nio.FloatBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.buffer.ProjectableBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Creates buffers for performing a <i>minimum intensity projection</i>, similarly to {@link
 * MaxIntensityProjection} but calculating the minimum.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MinIntensityProjection {

    /**
     * Creates a buffer for a <i>minimum-intensity projection</i> for <b>unsigned byte</b> voxels.
     *
     * @param dataType the data-type to use for creating the buffer.
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @param <T> voxel data type to be equal to {@code dataType}.
     * @return a newly created buffer that can be used for projection.
     * @throws OperationFailedException if {@code dataType} is unsupported.
     */
    @SuppressWarnings("unchecked")
    public static <T> ProjectableBuffer<T> create(VoxelDataType dataType, Extent extent)
            throws OperationFailedException {
        if (dataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return (ProjectableBuffer<T>) createUnsignedByte(extent);
        } else if (dataType.equals(UnsignedShortVoxelType.INSTANCE)) {
            return (ProjectableBuffer<T>) createUnsignedShort(extent);
        } else if (dataType.equals(UnsignedIntVoxelType.INSTANCE)) {
            return (ProjectableBuffer<T>) createUnsignedInt(extent);
        } else if (dataType.equals(FloatVoxelType.INSTANCE)) {
            return (ProjectableBuffer<T>) createFloat(extent);
        } else {
            throw new OperationFailedException(
                    "No minimum-intensity projection buffer can be created, as voxel-data-type is unsupported: "
                            + dataType.toString());
        }
    }

    /**
     * Creates a buffer for a <i>minimum-intensity projection</i> for <b>unsigned byte</b> voxels.
     *
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @return a newly created buffer that can be used for projection.
     */
    public static ProjectableBuffer<UnsignedByteBuffer> createUnsignedByte(Extent extent) {
        return new UnsignedByteImplementation(
                extent, MinIntensityProjection::isValueLessThanExistingInt);
    }

    /**
     * Creates a buffer for a <i>minimum-intensity projection</i> for <b>unsigned short</b> voxels.
     *
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @return a newly created buffer that can be used for projection.
     */
    public static ProjectableBuffer<UnsignedShortBuffer> createUnsignedShort(Extent extent) {
        return new UnsignedShortImplementation(
                extent, MinIntensityProjection::isValueLessThanExistingInt);
    }

    /**
     * Creates a buffer for a <i>minimum-intensity projection</i> for <b>unsigned int</b> voxels.
     *
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @return a newly created buffer that can be used for projection.
     */
    public static ProjectableBuffer<UnsignedIntBuffer> createUnsignedInt(Extent extent) {
        return new UnsignedIntImplementation(
                extent, MinIntensityProjection::isValueLessThanExistingLong);
    }

    /**
     * Creates a buffer for a <i>minimum-intensity projection</i> for <b>float</b> voxels.
     *
     * @param extent the size of the projected image. The z-dimension is ignored.
     * @return a newly created buffer that can be used for projection.
     */
    public static ProjectableBuffer<FloatBuffer> createFloat(Extent extent) {
        return new FloatImplementation(
                extent, MinIntensityProjection::isValueLessThanExistingFloat);
    }

    private static boolean isValueLessThanExistingInt(int value, int existing) {
        return value < existing;
    }

    private static boolean isValueLessThanExistingLong(long value, long existing) {
        return value < existing;
    }

    private static boolean isValueLessThanExistingFloat(float value, float existing) {
        return value < existing;
    }
}
