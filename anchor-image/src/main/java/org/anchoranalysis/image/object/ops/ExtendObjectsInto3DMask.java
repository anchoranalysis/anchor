package org.anchoranalysis.image.object.ops;

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
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * Extends 2D objects as much as possible in z-dimension, while staying within a 3D binary mask.
 * 
 * TODO remove slices which have nothing in them from top and bottom
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ExtendObjectsInto3DMask {
	
	public static ObjectCollection extendObjects( ObjectCollection objects2D, BinaryVoxelBox<ByteBuffer> mask3D) {
		return objects2D.stream().map( object->
			extendObj( object, mask3D )
		);
	}
	
	private static ObjectMask extendObj( ObjectMask object2D, BinaryVoxelBox<ByteBuffer> voxelBox3D ) {
		return new ObjectMask( 
			extendObj(
				object2D.getVoxelBoxBounded(),
				voxelBox3D
			)
		);
	}
	
	private static BoundedVoxelBox<ByteBuffer> extendObj(
		BoundedVoxelBox<ByteBuffer> obj2D,
		BinaryVoxelBox<ByteBuffer> mask3D
	) {

		BoundingBox newBBox = createBoundingBoxForAllZ( obj2D.getBoundingBox(), mask3D.extent().getZ() );
		
		BoundedVoxelBox<ByteBuffer> newMask = new BoundedVoxelBox<>(
			newBBox,
			VoxelBoxFactory.getByte()
		);
		
		ReadableTuple3i max = newBBox.calcCornerMax();
		Point3i point = new Point3i();

		BinaryValuesByte bv = mask3D.getBinaryValues().createByte();

		ByteBuffer bufferIn2D = obj2D.getVoxelBox().getPixelsForPlane(0).buffer();
		
		for (point.setZ(0); point.getZ() <=max.getZ(); point.incrementZ()) {
			
			ByteBuffer bufferMask3D =  mask3D.getVoxelBox().getPlaneAccess().getPixelsForPlane(point.getZ()).buffer();
			ByteBuffer bufferOut3D = newMask.getVoxelBox().getPixelsForPlane(point.getZ()).buffer();
		
			int ind = 0;
			
			for (point.setY(newBBox.cornerMin().getY()); point.getY() <= max.getY(); point.incrementY() ) {
			
				for (point.setX(newBBox.cornerMin().getX()); point.getX() <= max.getX(); point.incrementX(), ind++ ) {	
					
					if (bufferIn2D.get(ind)!=bv.getOnByte() ) {
						continue;
					}
						
					int indexGlobal = mask3D.extent().offset( point.getX(), point.getY());
					bufferOut3D.put(
						ind,
						bufferMask3D.get(indexGlobal)==bv.getOnByte() ? bv.getOnByte() : bv.getOffByte()
					);
				}
			}
		}
		return newMask;
	}
	
	private static BoundingBox createBoundingBoxForAllZ( BoundingBox exst, int z ) {
		Point3i cornerMin = copyPointChangeZ( exst.cornerMin(), 0 );
		Extent e = copyExtentChangeZ( exst.extent(), z );

		return new BoundingBox( cornerMin, e );
	}
	
	private static Point3i copyPointChangeZ( ReadableTuple3i in, int z ) {
		Point3i out = new Point3i( in );
		out.setZ(z);
		return out;
	}
		
	private static Extent copyExtentChangeZ( Extent in, int z ) {
		return new Extent(
			in.getX(),
			in.getY(),
			z
		);
	}
}
