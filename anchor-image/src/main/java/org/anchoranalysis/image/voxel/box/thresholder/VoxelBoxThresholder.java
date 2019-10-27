package org.anchoranalysis.image.voxel.box.thresholder;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;


/**
 * Performs threshold operation on a VoxelBox
  */
public class VoxelBoxThresholder {

	public static void thresholdForLevel(
		VoxelBox<ByteBuffer> inputBuffer,
		int level,
		BinaryValuesByte bvOut
	) throws CreateException {
		// We know that as the inputType is byte, it will be performed in place
		thresholdForLevel(
			VoxelBoxWrapper.wrap(inputBuffer),
			level,
			bvOut,
			false
		);
	}
		
	// Perform inplace
	public static BinaryVoxelBox<ByteBuffer> thresholdForLevel(
		VoxelBoxWrapper inputBuffer,
		int level,
		BinaryValuesByte bvOut,
		boolean alwaysDuplicate
	) throws CreateException {
	
		VoxelBox<ByteBuffer> boxOut = inputBuffer.asByteOrCreateEmpty( alwaysDuplicate );
		
		Extent e = inputBuffer.any().extnt();
		for (int z=0; z<e.getZ(); z++) {
			
			VoxelBuffer<?> bb = inputBuffer.any().getPixelsForPlane(z);
			VoxelBuffer<ByteBuffer> bbOut = boxOut.getPixelsForPlane(z);
		
			transferSlice(e, bb, bbOut, level, bvOut);
		}
		
		return new BinaryVoxelBoxByte(boxOut, bvOut.createInt() );
	}
	
	private static void transferSlice(Extent e, VoxelBuffer<?> bb, VoxelBuffer<ByteBuffer> bbOut, int level, BinaryValuesByte bvOut) {
		for (int y=0; y<e.getY(); y++) {
			for (int x=0; x<e.getX(); x++) {
				
				int offset = e.offset(x, y);

				int val = bb.getInt(offset);
				
				bbOut.buffer().put(
					offset,
					val>=level ? bvOut.getOnByte() : bvOut.getOffByte()
				);
			}
		}
		
	}
}
