package org.anchoranalysis.image.convert.imglib2;

import java.nio.Buffer;
import java.util.List;
import java.util.function.Function;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class Wrap {
    
    public static <S extends NativeType<S>, T extends ArrayDataAccess<T>, U extends Buffer>
            NativeImg<S, T> allSlices(
                    Voxels<U> voxels,
                    Function<U, T> transform,
                    Function<AbstractNativeImg<S, T>, S> createType) {
        
        long[] dim = Wrap.asArray3D(voxels.extent());

        PlanarImg<S, T> image = new PlanarImg<>(slicesFor(voxels, transform), dim, new Fraction());
        return Wrap.updateType(image, createType);
    }
    
    public static <S extends NativeType<S>, T, U extends Buffer> NativeImg<S, T> buffer(
            VoxelBuffer<U> buffer,
            Extent extent,
            Function<U, T> transform,
            Function<AbstractNativeImg<S, T>, S> createType) {
        ArrayImg<S, T> image = new ArrayImg<>(transform.apply(buffer.buffer()), Wrap.asArray2D(extent), new Fraction());
        return Wrap.updateType(image, createType);
    }
    
    private static <S extends NativeType<S>, T> NativeImg<S, T> updateType(
            AbstractNativeImg<S, T> image, Function<AbstractNativeImg<S, T>, S> createType) {
        image.setLinkedType(createType.apply(image));
        return image;
    }
        
    private static <T, U extends Buffer> List<T> slicesFor(
            Voxels<U> voxels, Function<U, T> transformSlice) {
        return FunctionalList.of( voxels.extent().streamOverZ()
                .mapToObj(z -> transformSlice.apply(voxels.sliceBuffer(z))));
    }
    
    private static long[] asArray3D(Extent extent) {
        return new long[] {extent.x(), extent.y(), extent.z()};
    }
    
    private static long[] asArray2D(Extent extent) {
        return new long[] {extent.x(), extent.y()};
    }
}
