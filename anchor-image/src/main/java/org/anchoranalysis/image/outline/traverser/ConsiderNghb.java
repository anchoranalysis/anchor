package org.anchoranalysis.image.outline.traverser;

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
import java.util.List;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

class ConsiderNghb {

	private ObjectMask omOutline;
	private int dist;
	private List<Point3iWithDist> localQueue;
	private IConsiderVisit icv;
	private Point3i pnt;
	
	public ConsiderNghb(ObjectMask omOutline, int dist, List<Point3iWithDist> localQueue, IConsiderVisit icv,
			Point3i pnt) {
		super();
		this.omOutline = omOutline;
		this.dist = dist;
		this.localQueue = localQueue;
		this.icv = icv;
		this.pnt = pnt;
	}
	
	public void considerNghbs( boolean useZ, boolean nghb8 ) {

		// Look for any neighbouring pixels and call them recursively
		considerAndQueue( 1, 0, 0 );
		
		considerAndQueue( -1, 0, 0 );
		considerAndQueue( 0, 1, 0 );
		considerAndQueue( 0, -1, 0 );
		
		if (useZ) {
			considerAndQueue( 0, 0, -1 );
			considerAndQueue( 0, 0, 1 );	
		}
		
		if (nghb8) {
			considerAndQueue( -1, -1, 0 );
			considerAndQueue( -1, 1, 0 );
			considerAndQueue( 1, -1, 0 );
			considerAndQueue( 1, 1, 0 );
			
			if (useZ) {
				considerAndQueue( -1, -1, -1 );
				considerAndQueue( -1, 1, -1 );
				considerAndQueue( 1, -1, -1 );
				considerAndQueue( 1, 1, -1 );
				
				considerAndQueue( -1,-1, 1 );
				considerAndQueue( -1, 1, 1 );
				considerAndQueue( 1, -1, 1 );
				considerAndQueue( 1, 1, 1 );
			}
		}
	}
	
	private void considerAndQueue( int xShift, int yShift, int zShift ) {
		considerVisitAndQueueNghbPoint(
			icv,
			new Point3i(
				pnt.getX()+xShift,
				pnt.getY()+yShift,
				pnt.getZ()+zShift
			),
			dist,
			localQueue
		);
	}
	
	private boolean considerVisitAndQueueNghbPoint( IConsiderVisit considerVisit, Point3i pnt, int dist, List<Point3iWithDist> pnts ) {
		
		int distNew = dist + 1;
		
		if( considerVisitMarkRaster(considerVisit, pnt, distNew, omOutline ) ) {
			pnts.add( new Point3iWithDist(pnt, distNew) );
			return true;
		}
		
		return false;
	}
	
	public static boolean considerVisitMarkRaster( IConsiderVisit considerVisit, Point3i pnt, int dist, ObjectMask omOutline ) {
		
		VoxelBox<ByteBuffer> vb = omOutline.getVoxelBox();
		BinaryValuesByte bvb = omOutline.getBinaryValuesByte();
		
		if ( !vb.extent().contains(pnt) ) {
			return false;
		}

		ByteBuffer bb = vb.getPixelsForPlane( pnt.getZ() ).buffer();
		int offset = vb.extent().offset( pnt.getX(), pnt.getY() );
		
		// Check if the buffer allows us to read the pixel
		if (bb.get(offset)==bvb.getOffByte()) {
			return false;
		}
		
		// We do the check after first verifying that visiting the pixel is possible from the buffer
		if ( !considerVisit.considerVisit(pnt,dist) ) {
			return false;
		}

		bb.put( offset, bvb.getOffByte() );
		
		return true;
	}
}
