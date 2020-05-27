package org.anchoranalysis.image.voxel.box.pixelsforplane;

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


import java.nio.IntBuffer;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferInt;

public class PixelsFromIntBufferArr implements IPixelsForPlane<IntBuffer> {

	private final VoxelBuffer<IntBuffer>[] buffer;
	private final Extent extnt;
	
	private PixelsFromIntBufferArr( Extent extnt ) {
		assert( extnt.getZ() > 0 );
		
		this.extnt = extnt;
		
		buffer = new VoxelBufferInt[extnt.getZ()];
	}
	
	private void init() {
		int volumeXY = extnt.getVolumeXY();
		for (int z=0; z<extnt.getZ(); z++) {
			buffer[z] = VoxelBufferInt.allocate(volumeXY);
		}		
	}
	
	// START FACTORY METHODS
	public static PixelsFromIntBufferArr createInitialised(Extent extnt) {
		PixelsFromIntBufferArr p = new PixelsFromIntBufferArr(extnt);
		p.init();
		return p;
	}
	
	public static PixelsFromIntBufferArr createEmpty(Extent extnt) {
		return new PixelsFromIntBufferArr(extnt);
	}
	// END FACTORY METHODS	
	
	@Override
	public void setPixelsForPlane(int z, VoxelBuffer<IntBuffer> pixels) {
		pixels.buffer().clear();
		buffer[z] = pixels;
	}

	@Override
	public VoxelBuffer<IntBuffer> getPixelsForPlane(int z) {
		VoxelBuffer<IntBuffer> buf = buffer[z];
		buf.buffer().clear();
		return buf;
	}

	@Override
	public Extent extent() {
		return extnt;
	}
}