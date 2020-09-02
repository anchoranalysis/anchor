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
 * NativeImg} used in <a href="https://imagej.net/ImgLib2">ImgLib2</a>.
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
