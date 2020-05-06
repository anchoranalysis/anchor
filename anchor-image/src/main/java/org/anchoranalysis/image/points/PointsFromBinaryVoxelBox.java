package org.anchoranalysis.image.points;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;

public class PointsFromBinaryVoxelBox {
	
	private PointsFromBinaryVoxelBox() {}
	

	// Add: is added to each point before they are added to the list 
	public static void addPointsFromVoxelBox( BinaryVoxelBox<ByteBuffer> voxelBox, Point3i add, List<Point2i> listOut  ) throws CreateException {
		
		Extent e = voxelBox.extnt();

		BinaryValuesByte bvb = voxelBox.getBinaryValues().createByte();
		
		ByteBuffer bb = voxelBox.getPixelsForPlane(0).buffer();
		
		for( int y=0; y<e.getY(); y++) {
			for( int x=0; x<e.getX(); x++) {
				
				if (bb.get()==bvb.getOnByte()) {
					
					int xAdj = add.getX() + x;
					int yAdj = add.getY() + y;
					
					listOut.add( new Point2i(xAdj,yAdj) );
				}
				
			}
		}
	}

	public static List<Point2i> pointsFromVoxelBox2D( BinaryVoxelBox<ByteBuffer> bvb ) throws CreateException {
		
		List<Point2i> listOut = new ArrayList<>();
				
		if (bvb.extnt().getZ()>1) {
			throw new CreateException("Only works in 2D. No z-stack alllowed");
		}
		
		addPointsFromVoxelBox(bvb, new Point3i(0,0,0), listOut);
		
		return listOut;
	}
	

	// Add: is added to each point before they are added to the list 
	public static void addPointsFromVoxelBox3D( BinaryVoxelBox<ByteBuffer> voxelBox, Point3i add, Collection<Point3i> out  ) throws CreateException {
		
		Extent e = voxelBox.extnt();

		BinaryValuesByte bvb = voxelBox.getBinaryValues().createByte();
		
		for( int z=0; z<e.getZ(); z++) {
			ByteBuffer bb = voxelBox.getPixelsForPlane(z).buffer();
			
			int zAdj = add.getZ() + z;
			
			for( int y=0; y<e.getY(); y++) {
				
				int yAdj = add.getY() + y;
				
				for( int x=0; x<e.getX(); x++) {
					
					if (bb.get()==bvb.getOnByte()) {
						
						int xAdj = add.getX() + x;
						
						
						out.add( new Point3i(xAdj,yAdj,zAdj) );
					}
					
				}
			}
		}
	}
	
	// Add: is added to each point before they are added to the list 
	public static void addPointsFromVoxelBox3DDouble( BinaryVoxelBox<ByteBuffer> voxelBox, Point3i add, Collection<Point3d> out  ) throws CreateException {
		
		Extent e = voxelBox.extnt();

		BinaryValuesByte bvb = voxelBox.getBinaryValues().createByte();
		
		for( int z=0; z<e.getZ(); z++) {
			ByteBuffer bb = voxelBox.getPixelsForPlane(z).buffer();
			
			int zAdj = add.getZ() + z;
			
			for( int y=0; y<e.getY(); y++) {
				
				int yAdj = add.getY() + y;
				
				for( int x=0; x<e.getX(); x++) {
					
					if (bb.get()==bvb.getOnByte()) {
						
						int xAdj = add.getX() + x;
						
						
						out.add( new Point3d(xAdj,yAdj,zAdj) );
					}
					
				}
			}
		}
	}

}
