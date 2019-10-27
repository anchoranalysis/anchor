package org.anchoranalysis.image.stack.bufferedimage;

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


import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class BufferedImageFactory {
	
	public static BufferedImage createGrayscale( VoxelBox<ByteBuffer> vb ) throws CreateException {
		
		Extent e = vb.extnt();
		checkExtentZ(e);
		
		return createBufferedImageFromGrayscaleBuffer(
			vb.getPixelsForPlane(0).buffer(),
			e
		);
	}
	
	public static BufferedImage createRGB(
		VoxelBox<ByteBuffer> red,
		VoxelBox<ByteBuffer> green,
		VoxelBox<ByteBuffer> blue,
		Extent e
	) throws CreateException {
		checkExtentZ(e);
						
		BufferedImage bi = new BufferedImage(
			e.getX(),
			e.getY(),
			BufferedImage.TYPE_3BYTE_BGR
		);
		
		byte[] arrComb = createCombinedByteArray(
			e,
			firstBuffer(red, e, "red"),
			firstBuffer(green, e, "green"),
			firstBuffer(blue, e, "blue")
		);
		bi.getWritableTile(0, 0).setDataElements(0,0, e.getX(), e.getY(), arrComb );

		return bi;
	}
	
	private static ByteBuffer firstBuffer( VoxelBox<ByteBuffer> vb, Extent e, String dscr ) throws CreateException {
		
		if (!vb.extnt().equals(e)) {
			throw new CreateException(dscr + " channel extent does not match");
		}
		
		return vb.getPixelsForPlane(0).buffer();
	}
	
	private static BufferedImage createBufferedImageFromGrayscaleBuffer( ByteBuffer bbGray, Extent e ) {
		
		BufferedImage bi = new BufferedImage(
			e.getX(),
			e.getY(),
			BufferedImage.TYPE_BYTE_GRAY
		);
		
		byte[] arr = bbGray.array();
		bi.getWritableTile(0, 0).setDataElements(0,0, e.getX(), e.getY(), arr );
		
		return bi;
	}
		
	private static byte[] createCombinedByteArray( Extent e, ByteBuffer bbRed, ByteBuffer bbGreen, ByteBuffer bbBlue) {
		
		int size = e.getVolume();
		byte[] arrComb = new byte[ size*3 ];
		int cnt = 0;
		for( int i=0; i<size; i++ ) {
			
			arrComb[cnt++] = bbRed.get(i);
			arrComb[cnt++] = bbGreen.get(i);
			arrComb[cnt++] = bbBlue.get(i);
		}
		return arrComb;
	}
	
	
	private static void checkExtentZ( Extent e ) throws CreateException {
		if( e.getZ()!=1 ) {
			throw new CreateException("z dimension must be 1");
		}
	}
}
