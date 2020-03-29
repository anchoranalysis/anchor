package org.anchoranalysis.anchor.mpp.overlap;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class MaxIntensityProjectionPair {
	
	private BoundedVoxelBox<ByteBuffer> bufferMIP1;
	private BoundedVoxelBox<ByteBuffer> bufferMIP2;
	
	public MaxIntensityProjectionPair(
		BoundedVoxelBox<ByteBuffer> buffer1,
		BoundedVoxelBox<ByteBuffer> buffer2,
		RegionMembershipWithFlags rmFlags1,
		RegionMembershipWithFlags rmFlags2
	) {
		BinaryVoxelBox<ByteBuffer> bvb1 = createBinaryVoxelBoxForFlag(buffer1.getVoxelBox(), rmFlags1);
		BinaryVoxelBox<ByteBuffer> bvb2 = createBinaryVoxelBoxForFlag(buffer2.getVoxelBox(), rmFlags2);
		
		BoundedVoxelBox<ByteBuffer> bvbBounded1 = new BoundedVoxelBox<>(buffer1.getBoundingBox(), bvb1.getVoxelBox());
		BoundedVoxelBox<ByteBuffer> bvbBounded2 = new BoundedVoxelBox<>(buffer2.getBoundingBox(), bvb2.getVoxelBox());
		
		bufferMIP1 = bvbBounded1.createMaxIntensityProjection();
		bufferMIP2 = bvbBounded2.createMaxIntensityProjection();
	}
	
	public int countIntersectingPixels() {
		// Relies on the binary voxel buffer ON being 255
		return new CountIntersectingPixelsRegionMembership(
			(byte) 1
		).countIntersectingPixels(
			bufferMIP1,
			bufferMIP2
		);
	}
	
	public int minArea() {
		int cnt1 = bufferMIP1.getVoxelBox().countEqual(BinaryValues.getDefault().getOnInt());
		int cnt2 = bufferMIP2.getVoxelBox().countEqual(BinaryValues.getDefault().getOnInt());
		return Math.min(cnt1, cnt2);
	}

	private static BinaryVoxelBox<ByteBuffer> createBinaryVoxelBoxForFlag(
		VoxelBox<ByteBuffer> vb,
		RegionMembershipWithFlags rmFlags
	) {
		
		VoxelBox<ByteBuffer> vbOut = VoxelBoxFactory.getByte().create(vb.extnt());
		
		BinaryValuesByte bvb = BinaryValuesByte.getDefault().duplicate();
		
		for( int z=0; z<vb.extnt().getZ(); z++) {
			
			ByteBuffer bb = vb.getPixelsForPlane(z).buffer();
			ByteBuffer bbOut = vbOut.getPixelsForPlane(z).buffer();
			
			int offset = 0;
			for( int y=0; y<vb.extnt().getY(); y++) {
				for( int x=0; x<vb.extnt().getX(); x++) {
					
					byte b = bb.get(offset);
					if ( rmFlags.isMemberFlag(b)) {
						bbOut.put( offset, bvb.getOnByte() );
					} else {
						if (bvb.getOffByte()!=0) {
							bbOut.put( offset, bvb.getOffByte() );
						}
					}
					
					offset++;
				}
			}
		}
		
		return new BinaryVoxelBoxByte(vbOut,bvb.createInt());
	}	
}
