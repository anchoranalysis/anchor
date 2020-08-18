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

package org.anchoranalysis.image.convert;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.Img;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.Float;
import org.anchoranalysis.image.voxel.datatype.UnsignedByte;
import org.anchoranalysis.image.voxel.datatype.UnsignedShort;

/**
 * Converts the {@link Voxels} and {@link VoxelBuffer} data-types used in Anchor to the {@link
 * NativeImg} used in ImgLib2
 *
 * @author Owen Feehan
 */
public class ImgLib2Wrap {

    private ImgLib2Wrap() {
        // Force static access
    }

    public static Img<? extends RealType<?>> wrap(VoxelsWrapper box) {

        VoxelDataType dataType = box.getVoxelDataType();

        if (dataType.equals(UnsignedByte.INSTANCE)) {
            return wrapByte(box.asByte());
        } else if (dataType.equals(UnsignedShort.INSTANCE)) {
            return wrapShort(box.asShort());
        } else if (dataType.equals(Float.INSTANCE)) {
            return wrapFloat(box.asFloat());
        } else {
            throw new IncorrectVoxelDataTypeException(
                    "Only unsigned byte, short and float are supported");
        }
    }

    @SuppressWarnings("unchecked")
    public static Img<? extends RealType<?>> wrap(VoxelBuffer<?> voxels, Extent e) {

        VoxelDataType dataType = voxels.dataType();

        if (dataType.equals(UnsignedByte.INSTANCE)) {
            return wrapByte((VoxelBuffer<ByteBuffer>) voxels, e);
        } else if (dataType.equals(UnsignedShort.INSTANCE)) {
            return wrapShort((VoxelBuffer<ShortBuffer>) voxels, e);
        } else if (dataType.equals(Float.INSTANCE)) {
            return wrapFloat((VoxelBuffer<FloatBuffer>) voxels, e);
        } else {
            throw new IncorrectVoxelDataTypeException(
                    "Only unsigned byte, short and float are supported");
        }
    }

    public static NativeImg<UnsignedByteType, ByteArray> wrapByte(Voxels<ByteBuffer> box) {
        return wrapBox(box, b -> new ByteArray(b.array()), UnsignedByteType::new);
    }

    public static NativeImg<UnsignedShortType, ShortArray> wrapShort(Voxels<ShortBuffer> box) {
        return wrapBox(box, b -> new ShortArray(b.array()), UnsignedShortType::new);
    }

    public static NativeImg<FloatType, FloatArray> wrapFloat(Voxels<FloatBuffer> box) {
        return wrapBox(box, b -> new FloatArray(b.array()), FloatType::new);
    }

    /** Only uses X and Y of e, ignores Z */
    public static NativeImg<UnsignedByteType, ByteArray> wrapByte(
            VoxelBuffer<ByteBuffer> buffer, Extent e) {
        return wrapBuffer(buffer, e, b -> new ByteArray(b.array()), UnsignedByteType::new);
    }

    /** Only uses X and Y of e, ignores Z */
    public static NativeImg<UnsignedShortType, ShortArray> wrapShort(
            VoxelBuffer<ShortBuffer> buffer, Extent e) {
        return wrapBuffer(buffer, e, b -> new ShortArray(b.array()), UnsignedShortType::new);
    }

    /** Only uses X and Y of e, ignores Z */
    public static NativeImg<FloatType, FloatArray> wrapFloat(
            VoxelBuffer<FloatBuffer> buffer, Extent e) {
        return wrapBuffer(buffer, e, b -> new FloatArray(b.array()), FloatType::new);
    }

    private static <S extends NativeType<S>, T extends ArrayDataAccess<T>, U extends Buffer>
            NativeImg<S, T> wrapBox(
                    Voxels<U> box,
                    Function<U, T> transform,
                    Function<AbstractNativeImg<S, T>, S> createType) {
        return wrapAllSlicesFor(box, transform, createType);
    }

    private static <S extends NativeType<S>, T extends ArrayDataAccess<T>, U extends Buffer>
            NativeImg<S, T> wrapAllSlicesFor(
                    Voxels<U> box,
                    Function<U, T> transform,
                    Function<AbstractNativeImg<S, T>, S> createType) {
        Extent e = box.extent();

        long[] dim = new long[] {e.x(), e.y(), e.z()};

        PlanarImg<S, T> img = new PlanarImg<>(slicesFor(box, transform), dim, new Fraction());
        return updateLinkedTypeOnImage(img, createType);
    }

    private static <T, U extends Buffer> List<T> slicesFor(
            Voxels<U> box, Function<U, T> transformSlice) {
        return IntStream.range(0, box.extent().z())
                .mapToObj(z -> transformSlice.apply(box.sliceBuffer(z)))
                .collect(Collectors.toList());
    }

    private static <S extends NativeType<S>, T, U extends Buffer> NativeImg<S, T> wrapBuffer(
            VoxelBuffer<U> buffer,
            Extent e,
            Function<U, T> transform,
            Function<AbstractNativeImg<S, T>, S> createType) {
        long[] dim = new long[] {e.x(), e.y()};
        ArrayImg<S, T> img = new ArrayImg<>(transform.apply(buffer.buffer()), dim, new Fraction());
        return updateLinkedTypeOnImage(img, createType);
    }

    private static <S extends NativeType<S>, T> NativeImg<S, T> updateLinkedTypeOnImage(
            AbstractNativeImg<S, T> img, Function<AbstractNativeImg<S, T>, S> createType) {
        img.setLinkedType(createType.apply(img));
        return img;
    }
}
