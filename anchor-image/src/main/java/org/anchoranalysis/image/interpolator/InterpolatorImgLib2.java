package org.anchoranalysis.image.interpolator;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.anchoranalysis.image.convert.ImgLib2Wrap;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;

public abstract class InterpolatorImgLib2 extends Interpolator {
	
	private InterpolatorFactory<UnsignedByteType,RandomAccessible<UnsignedByteType>> factoryByte;
	private InterpolatorFactory<UnsignedShortType,RandomAccessible<UnsignedShortType>> factoryShort;

	public InterpolatorImgLib2(
			InterpolatorFactory<UnsignedByteType, RandomAccessible<UnsignedByteType>> factoryByte,
			InterpolatorFactory<UnsignedShortType, RandomAccessible<UnsignedShortType>> factoryShort) {
		super();
		this.factoryByte = factoryByte;
		this.factoryShort = factoryShort;
	}

	@Override
	public VoxelBuffer<ByteBuffer> interpolateByte(VoxelBuffer<ByteBuffer> src,
			VoxelBuffer<ByteBuffer> dest, Extent eSrc, Extent eDest) {
		
		Img<UnsignedByteType> imIng = ImgLib2Wrap.wrapByte(src, eSrc );
		Img<UnsignedByteType> imgOut = ImgLib2Wrap.wrapByte(dest, eDest );

		RealRandomAccessible<UnsignedByteType> interpolant = Views.interpolate( Views.extendMirrorSingle( imIng ), factoryByte );
		
		interpolate2D( interpolant, imgOut, eSrc );
		return dest;
	}

	@Override
	public VoxelBuffer<ShortBuffer> interpolateShort(VoxelBuffer<ShortBuffer> src,
			VoxelBuffer<ShortBuffer> dest, Extent eSrc, Extent eDest) {
		
		Img<UnsignedShortType> imIng = ImgLib2Wrap.wrapShort(src, eSrc);
		Img<UnsignedShortType> imgOut = ImgLib2Wrap.wrapShort(dest, eDest);
		
		RealRandomAccessible<UnsignedShortType> interpolant = Views.interpolate( Views.extendMirrorSingle( imIng ), factoryShort );
		
		interpolate2D( interpolant, imgOut, eSrc );
		return dest;
	}



	
  private static < T extends Type<T> > Img<T> interpolate2D( RealRandomAccessible<T> source,  Img<T> destination, Extent eSrc ) {
        // cursor to iterate over all pixels
        Cursor<T> cursor = destination.localizingCursor();
 
        // create a RealRandomAccess on the source (interpolator)
        RealRandomAccess<T> realRandomAccess = source.realRandomAccess();
 
        // the temporary array to compute the position
        double[] tmp = new double[2];
 
        // for all pixels of the output image
        while ( cursor.hasNext() ) {
        	
            cursor.fwd();
 
            tmp[0] = (cursor.getDoublePosition(0) / (destination.realMax(0)) * eSrc.getX());
            tmp[1] = (cursor.getDoublePosition(1) / (destination.realMax(1)) * eSrc.getY());
 
            // set the position
            realRandomAccess.setPosition( tmp );
 
            // set the new value
            cursor.get().set( realRandomAccess.get() );
        }
 
        return destination;
    }

	@Override
	public boolean isNewValuesPossible() {
		return true;
	}
}
