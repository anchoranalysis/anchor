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
package org.anchoranalysis.image.voxel.convert.imglib2;

import java.util.List;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.DataAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Wraps the memory used in {@link Voxels} or {@link VoxelBuffer} into a {@link NativeImg}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class WrapNativeImg {

    /**
     * Wraps all slices of {@link Voxels} into a ImgLib2 {@link NativeImg}.
     *
     * @param <S> ImgLib2 type to describe the image.
     * @param <T> the native-array used for the memory.
     * @param <U> type of buffer used in {@link Voxels} for a single slice.
     * @param voxels the voxels to wrap.
     * @param extractArrayFromBuffer converts a buffer into a corresponding native-array.
     * @param deriveType converts from a {@link AbstractNativeImg} to the final type used in the
     *     {@link NativeImg}.
     * @return the {@code voxels} wrapped into a {@link NativeImg}.
     */
    public static <S extends NativeType<S>, T extends ArrayDataAccess<T>, U>
            NativeImg<S, T> allSlices(
                    Voxels<U> voxels,
                    Function<U, T> extractArrayFromBuffer,
                    Function<AbstractNativeImg<S, T>, S> deriveType) {

        long[] dim = WrapNativeImg.asArray3D(voxels.extent());

        PlanarImg<S, T> image =
                new PlanarImg<>(slicesFor(voxels, extractArrayFromBuffer), dim, new Fraction());
        return WrapNativeImg.updateType(image, deriveType);
    }

    /**
     * Wraps a {@link VoxelBuffer} into a ImgLib2 {@link NativeImg}.
     *
     * @param <S> ImgLib2 type to describe the image.
     * @param <T> the native-array used for the memory.
     * @param <U> type of buffer used in {@link Voxels} for a single slice.
     * @param buffer the buffer to wrap.
     * @param extent the size of {@code buffer}.
     * @param extractArrayFromBuffer converts a buffer into a corresponding native-array.
     * @param deriveType converts from a {@link AbstractNativeImg} to the final type used in the
     *     {@link NativeImg}.
     * @return the {@code buffer} wrapped into a {@link NativeImg}.
     */
    public static <S extends NativeType<S>, T extends DataAccess, U> NativeImg<S, T> buffer(
            VoxelBuffer<U> buffer,
            Extent extent,
            Function<U, T> extractArrayFromBuffer,
            Function<AbstractNativeImg<S, T>, S> deriveType) {
        ArrayImg<S, T> image =
                new ArrayImg<>(
                        extractArrayFromBuffer.apply(buffer.buffer()),
                        WrapNativeImg.asArray2D(extent),
                        new Fraction());
        return WrapNativeImg.updateType(image, deriveType);
    }

    private static <S extends NativeType<S>, T> NativeImg<S, T> updateType(
            AbstractNativeImg<S, T> image, Function<AbstractNativeImg<S, T>, S> createType) {
        image.setLinkedType(createType.apply(image));
        return image;
    }

    private static <T, U> List<T> slicesFor(Voxels<U> voxels, Function<U, T> transformSlice) {
        return FunctionalList.of(
                voxels.extent()
                        .streamOverZ()
                        .mapToObj(z -> transformSlice.apply(voxels.sliceBuffer(z))));
    }

    private static long[] asArray3D(Extent extent) {
        return new long[] {extent.x(), extent.y(), extent.z()};
    }

    private static long[] asArray2D(Extent extent) {
        return new long[] {extent.x(), extent.y()};
    }
}
