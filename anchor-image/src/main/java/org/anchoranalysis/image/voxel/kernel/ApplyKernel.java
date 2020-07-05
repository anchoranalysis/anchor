package org.anchoranalysis.image.voxel.kernel;

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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.kernel.count.CountKernel;

/**
 * Applies a kernel to a Voxel Box
 * 
 * @author FEEHANO
 *
 */
public class ApplyKernel {

	private static final VoxelBoxFactoryTypeBound<ByteBuffer> FACTORY = VoxelBoxFactory.getByte();
	
	private ApplyKernel() {}
	
	public static VoxelBox<ByteBuffer> apply( BinaryKernel kernel, VoxelBox<ByteBuffer> in ) {
		return apply(kernel, in, BinaryValuesByte.getDefault());
	}
	
	// 3 pixel diameter kernel
	public static VoxelBox<ByteBuffer> apply( BinaryKernel kernel, VoxelBox<ByteBuffer> in, BinaryValuesByte outBinary ) {
		
		VoxelBox<ByteBuffer> out = FACTORY.create( in.extent() );
		
		int localSlicesSize = 3;
		 
		Extent extent = in.extent();
		
		kernel.init(in);
		
		Point3i pnt = new Point3i();
		for (pnt.setZ(0); pnt.getZ()<extent.getZ(); pnt.incrementZ()) {

			LocalSlices localSlices = new LocalSlices(pnt.getZ(),localSlicesSize, in);
			ByteBuffer outArr = out.getPixelsForPlane(pnt.getZ()).buffer();
			
			int ind = 0;
			
			kernel.notifyZChange(localSlices, pnt.getZ());
			
			for (pnt.setY(0); pnt.getY()<extent.getY(); pnt.incrementY()) {
				for (pnt.setX(0); pnt.getX()<extent.getX(); pnt.incrementX()) {
					
					if( kernel.accptPos(ind, pnt) ) {
						outArr.put(ind,outBinary.getOnByte());
					} else {
						outArr.put(ind,outBinary.getOffByte());
					}
					
					ind++;
				}
			}
		}
		
		return out;
	}
	
	
	/**
	 * Applies the kernel to a voxelbox and sums the returned value
	 * 
	 * @param kernel the kernel to be applied
	 * @param vb the voxel-box to iterate over
	 * @return the sum of the count value returned by the kernel over all iterated voxels
	 * @throws OperationFailedException 
	 */
	public static int applyForCount( CountKernel kernel, VoxelBox<ByteBuffer> vb ) throws OperationFailedException {
		return applyForCount(kernel, vb, new BoundingBox(vb.extent()) );
	}
	
	
	/**
	 * Applies the kernel to a voxelbox and sums the returned value
	 * 
	 * @param kernel the kernel to be applied
	 * @param vb the voxel-box to iterate over
	 * @param bbox a bounding-box (co-ordinates relative to vb) that restricts where iteration occurs. Must be containted within vb.
	 * @return the sum of the count value returned by the kernel over all iterated voxels
	 * @throws OperationFailedException 
	 */
	public static int applyForCount( CountKernel kernel, VoxelBox<ByteBuffer> vb, BoundingBox bbox ) throws OperationFailedException {
		
		if( !vb.extent().contains(bbox)) {
			throw new OperationFailedException(
				String.format("BBox (%s) must be contained within extent (%s)", bbox, vb.extent() )
			);
		}
		
		int localSlicesSize = 3;
		 
		int cnt = 0;
		
		Extent extent = vb.extent();
		
		kernel.init(vb);
		
		ReadableTuple3i pntMax = bbox.calcCornerMax();
		
		Point3i pnt = new Point3i();
		for (pnt.setZ(bbox.cornerMin().getZ()); pnt.getZ()<=pntMax.getZ(); pnt.incrementZ()) {

			LocalSlices localSlices = new LocalSlices(pnt.getZ(),localSlicesSize, vb);
			kernel.notifyZChange(localSlices, pnt.getZ());
			
			for (pnt.setY(bbox.cornerMin().getY()); pnt.getY()<=pntMax.getY(); pnt.incrementY()) {
				for (pnt.setX(bbox.cornerMin().getX()); pnt.getX()<=pntMax.getX(); pnt.incrementX()) {
					
					int ind = extent.offset(pnt.getX(), pnt.getY());
					cnt += kernel.countAtPos(ind, pnt);
				}
			}
		}
		
		return cnt;
	}
	
	
	
	/**
	 * Applies the kernel to a voxelbox until a positive value is returned, then exits with TRUE
	 * 
	 * @param kernel the kernel to be applied
	 * @param vb the voxel-box to iterate over
	 * @param bbox a bounding-box (co-ordinates relative to vb) that restricts where iteration occurs. Must be containted within vb.
	 * @return TRUE if a positive-value is encountered, 0 if it never is encountered
	 * @throws OperationFailedException 
	 */
	public static boolean applyUntilPositive( CountKernel kernel, VoxelBox<ByteBuffer> vb, BoundingBox bbox ) throws OperationFailedException {
		
		if( !vb.extent().contains(bbox)) {
			throw new OperationFailedException(
				String.format("BBox (%s) must be contained within extent (%s)", bbox, vb.extent() )
			);
		}
		
		int localSlicesSize = 3;
		 
		Extent extent = vb.extent();
		
		kernel.init(vb);
		
		ReadableTuple3i pntMax = bbox.calcCornerMax();
		
		Point3i pnt = new Point3i();
		for (pnt.setZ(bbox.cornerMin().getZ()); pnt.getZ()<=pntMax.getZ(); pnt.incrementZ()) {

			LocalSlices localSlices = new LocalSlices(pnt.getZ(),localSlicesSize, vb);
			kernel.notifyZChange(localSlices, pnt.getZ());
			
			for (pnt.setY(bbox.cornerMin().getY()); pnt.getY()<=pntMax.getY(); pnt.incrementY()) {
				for (pnt.setX(bbox.cornerMin().getX()); pnt.getX()<=pntMax.getX(); pnt.incrementX()) {
					
					int ind = extent.offsetSlice(pnt);
					if (kernel.countAtPos(ind, pnt)>0) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	
	public static int applyForCount( BinaryKernel kernel, VoxelBox<ByteBuffer> in ) {
		
		int localSlicesSize = 3;
		 
		int cnt = 0;
		
		Extent extent = in.extent();
		
		kernel.init(in);
		
		Point3i pnt = new Point3i();
		for (pnt.setZ(0); pnt.getZ()<extent.getZ(); pnt.incrementZ()) {

			LocalSlices localSlices = new LocalSlices(pnt.getZ(),localSlicesSize, in);
			kernel.notifyZChange(localSlices, pnt.getZ());
			
			int ind = 0;
			
			for (pnt.setY(0); pnt.getY()<extent.getY(); pnt.incrementY()) {
				for (pnt.setX(0); pnt.getX()<extent.getX(); pnt.incrementX()) {
					
					if( kernel.accptPos(ind, pnt) ) {
						cnt++;
					}
					
					ind++;
				}
			}
		}
		
		return cnt;
	}
	
	
	public static int applyForCountOnMask( BinaryKernel kernel, VoxelBox<ByteBuffer> in, ObjectMask om ) {
		
		int localSlicesSize = 3;
		 
		int cnt = 0;
		
		BoundingBox bbox = om.getBoundingBox();
		ReadableTuple3i crnrMin = bbox.cornerMin();
		ReadableTuple3i crnrMax = bbox.calcCornerMax();
		
		Extent extent = in.extent();
		
		kernel.init(in);
		
		BinaryValuesByte bvb = om.getBinaryValues().createByte();
		
		Point3i pnt = new Point3i();
		for (pnt.setZ(crnrMin.getZ()); pnt.getZ()<=crnrMax.getZ(); pnt.incrementZ()) {

			LocalSlices localSlices = new LocalSlices(
				pnt.getZ(),
				localSlicesSize,
				in
			);
			kernel.notifyZChange(localSlices, pnt.getZ());
			
			int ind = 0;
			
			ByteBuffer bufMask = om.getVoxelBox().getPixelsForPlane(pnt.getZ() - crnrMin.getZ()).buffer();
			
			for (pnt.setY(crnrMin.getY()); pnt.getY()<=crnrMax.getY(); pnt.incrementY()) {
				for (pnt.setX(crnrMin.getX()); pnt.getX()<=crnrMax.getX(); pnt.incrementX()) {
					
					int indKernel = extent.offset(pnt.getX(), pnt.getY());

					if(bufMask.get(ind)==bvb.getOnByte() && kernel.accptPos(indKernel, pnt) ) {
						cnt++;
					}
					
					ind++;
				}
			}
		}
		
		return cnt;
	}
}
