package org.anchoranalysis.image.binary.logical;

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

import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class BinaryChnlOr {
		
	/**
	 * A binary OR of chnlCrnt and chnlReceiver where chnlReceiver is overwritten with the output
	 *  
	 * @param chnlCrnt first-channel for OR
	 * @param chnlReceiver second-channel for OR (and the channel where the result is overwritten)
	 */
	public static void binaryOr( BinaryChnl chnlCrnt, BinaryChnl chnlReceiver ) {
		
		BinaryValuesByte bvbCrnt = chnlCrnt.getBinaryValues().createByte();
		BinaryValuesByte bvbReceiver = chnlReceiver.getBinaryValues().createByte();
			
		Extent e = chnlCrnt.getDimensions().getExtnt();
		
		byte crntOn = bvbCrnt.getOnByte();
		byte receiveOn = bvbReceiver.getOnByte();
		
		// All the on voxels in the receive, are put onto crnt
		for( int z=0; z<e.getZ(); z++ ) {
			
			ByteBuffer bufSrc = chnlCrnt.getVoxelBox().getPixelsForPlane(z).buffer();
			ByteBuffer bufReceive = chnlReceiver.getVoxelBox().getPixelsForPlane(z).buffer();
			
			int offset = 0;
			for( int y=0; y<e.getY(); y++ ) {
				for( int x=0; x<e.getX(); x++ ) {
					
					byte byteRec = bufReceive.get(offset);
					if (byteRec==receiveOn) {
						bufSrc.put(offset, crntOn);
					}
					
					offset++;
				}
			}
		}
		
	}
	
}
