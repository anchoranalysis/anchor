package org.anchoranalysis.image.convert.imglib2;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Converts the {@link Voxels} and {@link VoxelBuffer} data-types used in Anchor to the {@link
 * NativeImg} used in <a href="https://imagej.net/ImgLib2>ImgLib2</a>.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ConvertToNativeImg {
    
  public static NativeImg<UnsignedByteType, ByteArray> fromByte(Voxels<ByteBuffer> voxels) {
      return Wrap.allSlices(voxels, Transform::asArray, UnsignedByteType::new);
  }

  public static NativeImg<UnsignedShortType, ShortArray> fromShort(Voxels<ShortBuffer> voxels) {
      return Wrap.allSlices(voxels, Transform::asArray, UnsignedShortType::new);
  }

  public static NativeImg<FloatType, FloatArray> fromFloat(Voxels<FloatBuffer> voxels) {
      return Wrap.allSlices(voxels, Transform::asArray, FloatType::new);
  }
}
