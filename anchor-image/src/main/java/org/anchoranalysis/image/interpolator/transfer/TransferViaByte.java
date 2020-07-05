package org.anchoranalysis.image.interpolator.transfer;

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

import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

// Lots of copying bytes, which doesn't make it very efficient
// Doesn't seem to be a way to make a BufferedImage using existing butes without ending up with a BufferedImage.CUSTOM_TYPE
//   type which messes up our scaling
public class TransferViaByte implements Transfer {
		
		private VoxelBox<ByteBuffer> src;
		private VoxelBox<ByteBuffer> trgt;
		private VoxelBuffer<ByteBuffer> buffer;
		
		private ResampleOp resampleOp;
		
		
		public TransferViaByte( VoxelBoxWrapper src, VoxelBoxWrapper trgt  ) {
			this.src = src.asByte();
			this.trgt = trgt.asByte();
			
			int trgtX = trgt.any().extent().getX();
			int trgtY = trgt.any().extent().getY();
			assert( trgtX > 0 );
			assert( trgtY > 0 );
			resampleOp = new ResampleOp(trgtX,trgtY);
			resampleOp.setFilter( ResampleFilters.getBiCubicFilter() );
			
		}
		
		@Override
		public void assignSlice( int z ) {
			buffer = src.getPixelsForPlane(z);
		}
		
		@Override
		public void transferCopyTo( int z ) {
			trgt.setPixelsForPlane(z, buffer.duplicate() );
		}
		
		@Override
		public void transferTo( int z, Interpolator interpolator ) {
			VoxelBuffer<ByteBuffer> bufIn = trgt.getPixelsForPlane(z);
			VoxelBuffer<ByteBuffer> bufOut = interpolator.interpolateByte(buffer, bufIn, src.extent(), trgt.extent() ) ;
			if (!bufOut.equals(bufIn)) {
				trgt.setPixelsForPlane(z, bufOut);
			}
		}
	
	}