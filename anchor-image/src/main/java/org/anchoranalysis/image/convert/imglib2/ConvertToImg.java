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

package org.anchoranalysis.image.convert.imglib2;

import java.nio.FloatBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Converts the {@link Voxels} and {@link VoxelBuffer} data-types used in Anchor to the {@link Img}
 * used in <a href="https://imagej.net/ImgLib2">ImgLib2</a>.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertToImg {

    /**
     * Converts from a {@link VoxelsWrapper} (Anchor structure) to a {@link Img} (ImgLib2
     * structure).
     *
     * <p>The voxel buffers are reused (without duplication).
     *
     * @param voxels the voxels to convert
     * @return an {@link Img} object reusing the buffers of {@code voxels}.
     */
    public static Img<? extends RealType<?>> from(VoxelsWrapper voxels) { // NOSONAR

        VoxelDataType dataType = voxels.getVoxelDataType();

        if (dataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return ConvertToNativeImg.fromByte(voxels.asByte());
        } else if (dataType.equals(UnsignedShortVoxelType.INSTANCE)) {
            return ConvertToNativeImg.fromShort(voxels.asShort());
        } else if (dataType.equals(FloatVoxelType.INSTANCE)) {
            return ConvertToNativeImg.fromFloat(voxels.asFloat());
        } else {
            throw new IncorrectVoxelTypeException(
                    "Only unsigned byte, short and float are supported");
        }
    }

    public static Img<? extends RealType<?>> fromSlice(
            VoxelsWrapper voxels, int sliceIndex) { // NOSONAR
        return fromBuffer(voxels.slice(sliceIndex), voxels.extent());
    }

    public static Img<UnsignedByteType> fromByte(
            VoxelBuffer<UnsignedByteBuffer> buffer, Extent extent) {
        return Wrap.buffer(buffer, extent, ArrayFactory::fromByte, UnsignedByteType::new);
    }

    public static Img<UnsignedShortType> fromShort(
            VoxelBuffer<UnsignedShortBuffer> buffer, Extent extent) {
        return Wrap.buffer(buffer, extent, ArrayFactory::fromShort, UnsignedShortType::new);
    }

    public static Img<FloatType> fromFloat(VoxelBuffer<FloatBuffer> buffer, Extent extent) {
        return Wrap.buffer(buffer, extent, ArrayFactory::fromFloat, FloatType::new);
    }

    public static Img<UnsignedByteType> fromByte(Voxels<UnsignedByteBuffer> voxels) {
        return ConvertToNativeImg.fromByte(voxels);
    }

    public static Img<UnsignedShortType> fromShort(Voxels<UnsignedShortBuffer> voxels) {
        return ConvertToNativeImg.fromShort(voxels);
    }

    public static Img<FloatType> fromFloat(Voxels<FloatBuffer> voxels) {
        return ConvertToNativeImg.fromFloat(voxels);
    }

    @SuppressWarnings("unchecked")
    private static Img<? extends RealType<?>> fromBuffer(
            VoxelBuffer<?> voxels, Extent extent) { // NOSONAR

        VoxelDataType dataType = voxels.dataType();

        if (dataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return fromByte((VoxelBuffer<UnsignedByteBuffer>) voxels, extent);
        } else if (dataType.equals(UnsignedShortVoxelType.INSTANCE)) {
            return fromShort((VoxelBuffer<UnsignedShortBuffer>) voxels, extent);
        } else if (dataType.equals(FloatVoxelType.INSTANCE)) {
            return fromFloat((VoxelBuffer<FloatBuffer>) voxels, extent);
        } else {
            throw new IncorrectVoxelTypeException(
                    "Only unsigned byte, short and float are supported");
        }
    }
}
