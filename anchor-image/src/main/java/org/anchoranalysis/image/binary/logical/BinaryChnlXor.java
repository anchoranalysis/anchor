package org.anchoranalysis.image.binary.logical;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class BinaryChnlXor {

	private BinaryChnlXor() {}
	
	public static void apply( BinaryChnl chnlCrnt, BinaryChnl chnlReceiver ) throws CreateException {
		apply(
			chnlCrnt.getVoxelBox(),
			chnlReceiver.getVoxelBox(),
			chnlCrnt.getBinaryValues().createByte(),
			chnlReceiver.getBinaryValues().createByte()
		);
	}
	
	public static void apply(
		VoxelBox<ByteBuffer> voxelBoxCrnt,
		VoxelBox<ByteBuffer> voxelBoxReceiver,
		BinaryValuesByte bvbCrnt,
		BinaryValuesByte bvbReceiver
	) throws CreateException {
		
		Extent e = voxelBoxCrnt.extent();
		
		// All the on voxels in the receive, are put onto crnt
		for( int z=0; z<e.getZ(); z++ ) {
			
			ByteBuffer bufSrc = voxelBoxCrnt.getPixelsForPlane(z).buffer();
			ByteBuffer bufReceive = voxelBoxReceiver.getPixelsForPlane(z).buffer();
			
			int offset = 0;
			for( int y=0; y<e.getY(); y++ ) {
				for( int x=0; x<e.getX(); x++ ) {
					
					byte byteSrc = bufSrc.get(offset);
					byte byteRec = bufReceive.get(offset);
					
					boolean srcOn = byteSrc==bvbCrnt.getOnByte();
					boolean recOn = byteRec==bvbReceiver.getOnByte();
					
					if (srcOn!=recOn) {
						bufSrc.put(offset, bvbCrnt.getOnByte());
					} else {
						bufSrc.put(offset, bvbCrnt.getOffByte());
					}
					
					offset++;
				}
			}
		}

	}
}
