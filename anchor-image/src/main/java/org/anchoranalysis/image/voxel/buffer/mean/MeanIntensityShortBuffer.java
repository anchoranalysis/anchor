package org.anchoranalysis.image.voxel.buffer.mean;

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


import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class MeanIntensityShortBuffer {
	
	private VoxelBox<ShortBuffer> flatVoxelBox;
	private VoxelBox<FloatBuffer> sumVoxelBox;
	private int cntVoxelBox = 0;

	/** Simple constructor since no preprocessing is necessary. */
	public MeanIntensityShortBuffer( Extent srcExtnt ) {
		flatVoxelBox = VoxelBoxFactory.getShort().create( new Extent( srcExtnt.getX(), srcExtnt.getY(), 1 ));
		sumVoxelBox = VoxelBoxFactory.getFloat().create( new Extent( srcExtnt.getX(), srcExtnt.getY(), 1 ));
	}
	
	public void projectSlice(ShortBuffer pixels) {
		
		int maxIndex = flatVoxelBox.extnt().getVolumeXY();
		for( int i=0; i<maxIndex; i++) {
			processPixel( pixels, i );
    	}
		cntVoxelBox++;
	}
	
	private void processPixel( ShortBuffer pixels, int index ) {
		short inPixel = pixels.get( index );
		
		int p = ByteConverter.unsignedShortToInt( inPixel );
		sumVoxelBox.getPixelsForPlane(0).buffer().put( index, sumVoxelBox.getPixelsForPlane(0).buffer().get(index) + p );
	}
	
	public void finalize() {
		int maxIndex = flatVoxelBox.extnt().getVolumeXY();
		
		ShortBuffer bbFlat = flatVoxelBox.getPixelsForPlane(0).buffer();
		FloatBuffer bbSum = sumVoxelBox.getPixelsForPlane(0).buffer();
		for( int i=0; i<maxIndex; i++) {
			bbFlat.put( i, (byte) (bbSum.get(i)/cntVoxelBox) );
    	}
	}
	
	public VoxelBox<ShortBuffer> getFlatBuffer() {
		return flatVoxelBox;
	}
}
