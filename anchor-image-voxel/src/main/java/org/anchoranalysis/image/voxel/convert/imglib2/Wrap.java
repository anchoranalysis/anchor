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
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.spatial.box.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Wrap {

    public static <S extends NativeType<S>, T extends ArrayDataAccess<T>, U>
            NativeImg<S, T> allSlices(
                    Voxels<U> voxels,
                    Function<U, T> transform,
                    Function<AbstractNativeImg<S, T>, S> createType) {

        long[] dim = Wrap.asArray3D(voxels.extent());

        PlanarImg<S, T> image = new PlanarImg<>(slicesFor(voxels, transform), dim, new Fraction());
        return Wrap.updateType(image, createType);
    }

    public static <S extends NativeType<S>, T, U> NativeImg<S, T> buffer(
            VoxelBuffer<U> buffer,
            Extent extent,
            Function<U, T> transform,
            Function<AbstractNativeImg<S, T>, S> createType) {
        ArrayImg<S, T> image =
                new ArrayImg<>(
                        transform.apply(buffer.buffer()), Wrap.asArray2D(extent), new Fraction());
        return Wrap.updateType(image, createType);
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
