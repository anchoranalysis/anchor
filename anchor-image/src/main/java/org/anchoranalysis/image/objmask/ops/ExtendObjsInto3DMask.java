package org.anchoranalysis.image.objmask.ops;

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

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;


/**
 * Extends 2D objects as much as possible in z-dimension, while staying within a 3D binary mask.
 * 
 * TODO remove slices which have nothing in them from top and bottom
 *
 */
public class ExtendObjsInto3DMask {
	
	private ExtendObjsInto3DMask() {}
	
	public static ObjMaskCollection extendObjs( ObjMaskCollection objs2D, BinaryVoxelBox<ByteBuffer> mask3D) {
		
		ObjMaskCollection out = new ObjMaskCollection();
		
		for (ObjMask obj : objs2D) {
			out.add(
				extendObj( obj, mask3D )
			);
		}
		return out;
	}
	
	private static ObjMask extendObj( ObjMask obj2D, BinaryVoxelBox<ByteBuffer> voxelBox3D ) {
		return new ObjMask( 
			extendObj(
				obj2D.getVoxelBoxBounded(),
				obj2D.getBinaryValuesByte(),
				voxelBox3D
			)
		);
	}
	
	private static BoundedVoxelBox<ByteBuffer> extendObj(
		BoundedVoxelBox<ByteBuffer> obj2D,
		BinaryValuesByte bvbObj,
		BinaryVoxelBox<ByteBuffer> mask3D
	) {

		BoundingBox newBBox = createBoundingBoxForAllZ( obj2D.getBoundingBox(), mask3D.extnt().getZ() );
		
		BoundedVoxelBox<ByteBuffer> newMask = new BoundedVoxelBox<>(
			newBBox,
			VoxelBoxFactory.instance().getByte()
		);
		
		Point3i max = newBBox.calcCrnrMax();
		Point3i pnt = new Point3i();

		BinaryValuesByte bv = mask3D.getBinaryValues().createByte();

		ByteBuffer bufferIn2D = obj2D.getVoxelBox().getPixelsForPlane(0).buffer();
		
		for (pnt.setZ(0); pnt.getZ() <=max.getZ(); pnt.incrZ()) {
			
			ByteBuffer bufferMask3D =  mask3D.getVoxelBox().getPlaneAccess().getPixelsForPlane(pnt.getZ()).buffer();
			ByteBuffer bufferOut3D = newMask.getVoxelBox().getPixelsForPlane(pnt.getZ()).buffer();
		
			int ind = 0;
			
			for (pnt.setY(newBBox.getCrnrMin().getY()); pnt.getY() <= max.getY(); pnt.incrY() ) {
			
				for (pnt.setX(newBBox.getCrnrMin().getX()); pnt.getX() <= max.getX(); pnt.incrX(), ind++ ) {	
					
					if (bufferIn2D.get(ind)!=bv.getOnByte() ) {
						continue;
					}
						
					int indexGlobal = mask3D.extnt().offset( pnt.getX(), pnt.getY());
					bufferOut3D.put(
						ind,
						bufferMask3D.get(indexGlobal)==bv.getOnByte() ? bv.getOnByte() : bv.getOffByte()
					);
				}
			}
		}
		
		assert(newMask.sizesMatch());
		
		return newMask;
	}
	
	private static BoundingBox createBoundingBoxForAllZ( BoundingBox exst, int z ) {
		Point3i crnrMin = copyPointChangeZ( exst.getCrnrMin(), 0 );
		Extent e = copyExtentChangeZ( exst.extnt(), z );

		return new BoundingBox( crnrMin, e );
	}
	
	private static Point3i copyPointChangeZ( Point3i in, int z ) {
		Point3i out = new Point3i( in );
		out.setZ(z);
		return out;
	}
		
	private static Extent copyExtentChangeZ( Extent in, int z ) {
		Extent out = new Extent( in );
		out.setZ( z );
		return out;
	}
}
