package org.anchoranalysis.image.objmask;

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

import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.OperationFailedRuntimeException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.voxel.box.VoxelBox;

class CenterOfGravityCalculator {
	
	public static Point3d calcCenterOfGravity( ObjMask om ) {
		
		VoxelBox<ByteBuffer> vb = om.getVoxelBox();

		int cnt = 0;
		double sumX = 0.0;
		double sumY = 0.0;
		double sumZ = 0.0;
		
		for( int z=0; z<vb.extnt().getZ(); z++ ) {
			
			ByteBuffer bb = vb.getPixelsForPlane(z).buffer();
			
			int offset = 0;
			for( int y=0; y<vb.extnt().getY(); y++ ) {
				for( int x=0; x<vb.extnt().getX(); x++ ) {
					
					if (bb.get(offset)==om.getBinaryValuesByte().getOnByte()) {
						
						cnt++;
						
						sumX += x;
						sumY += y;
						sumZ += z;
					}
					offset++;
				}
			}
			
		}
		
		checkCnt(cnt);
		
		
		double meanX = sumX / cnt;
		double meanY = sumY / cnt;
		double meanZ = sumZ / cnt;
		
		return new Point3d(
			meanX + om.getBoundingBox().getCrnrMin().getX(),
			meanY + om.getBoundingBox().getCrnrMin().getY(),
			meanZ + om.getBoundingBox().getCrnrMin().getZ()
		);
	}
	
	
	public static double calcCenterOfGravityForAxis( ObjMask om, AxisType axisType ) {
		
		VoxelBox<ByteBuffer> vb = om.getVoxelBox();

		int cnt = 0;
		double sum = 0.0;
		
		for( int z=0; z<vb.extnt().getZ(); z++ ) {
			
			ByteBuffer bb = vb.getPixelsForPlane(z).buffer();
			
			int offset = 0;
			for( int y=0; y<vb.extnt().getY(); y++ ) {
				for( int x=0; x<vb.extnt().getX(); x++ ) {
					
					if (bb.get(offset)==om.getBinaryValuesByte().getOnByte()) {
						
						cnt++;
						
						switch(axisType) {
						case X:
							sum += x;
							break;
						case Y:
							sum += y;
							break;
						case Z:
							sum += z;
							break;
						}
					}
					offset++;
				}
			}
			
		}
		
		checkCnt(cnt);
		
		double mean = sum / cnt;
		return mean + om.getBoundingBox().getCrnrMinForAxis(axisType);
	}
	
	private static void checkCnt( int cnt ) {
		if (cnt==0) {
			throw new OperationFailedRuntimeException("An object-mask has zero pixels. Cannot calculate center-of-gravity.");
		}
	}

}
