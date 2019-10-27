package org.anchoranalysis.image.voxel.box;

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
import java.nio.ShortBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.pixelsforplane.IPixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferShort;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityShortBuffer;


public class VoxelBoxShort extends VoxelBox<ShortBuffer> {
	
	public VoxelBoxShort(IPixelsForPlane<ShortBuffer> pixelsForPlane) {
		super(
			pixelsForPlane,
			VoxelBoxFactory.getShort()
		);
	}
	
	@Override
	public int ceilOfMaxPixel() {
		return VoxelBoxByte.ceilOfMaxPixel(getPlaneAccess());
	}

	@Override
	public void copyItem(ShortBuffer srcBuffer, int srcIndex,
			ShortBuffer destBuffer, int destIndex) {
		destBuffer.put( destIndex, srcBuffer.get(srcIndex) );
	}

	@Override
	public boolean isGreaterThan(ShortBuffer buffer, int operand) {
		return buffer.get() > operand;
	}
	
	@Override
	public ObjMask equalMask( BoundingBox bbox, int equalVal ) {
		
		ObjMask om = new ObjMask(bbox );
		
		Point3i pntMax = bbox.calcCrnrMax();
		
		byte maskOnVal = om.getBinaryValuesByte().getOnByte();
		
		for (int z=bbox.getCrnrMin().getZ(); z<=pntMax.getZ(); z++) {
			
			ShortBuffer pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelOut = om.getVoxelBox().getPixelsForPlane(z - bbox.getCrnrMin().getZ()).buffer();
			
			int ind = 0;
			for (int y=bbox.getCrnrMin().getY(); y<=pntMax.getY(); y++) {
				for (int x=bbox.getCrnrMin().getX(); x<=pntMax.getX(); x++) {
					
					int index = getPlaneAccess().extnt().offset(x, y);
					short chnlVal = pixelIn.get(index);
					
					if ( chnlVal==equalVal ) {
						pixelOut.put(ind, maskOnVal);
					}
					
					ind++;
				}
			}
		}
		
		return om;
	}
	
	@Override
	public void setAllPixelsTo( int val ) {
		
		for (int z=0; z<extnt().getZ(); z++) {
			
			ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			while( buffer.hasRemaining() ) {
				buffer.put( (short) val );
			}
		}
	}
	
	
	@Override
	public void setPixelsTo( BoundingBox bbox, int val ) {

		short valShort = (short) val;
		
		Point3i crnrMin = bbox.getCrnrMin();
		Point3i crnrMax = bbox.calcCrnrMax();
		Extent e = extnt();
		
		for (int z=crnrMin.getZ(); z<=crnrMax.getZ(); z++) {
			
			ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			for (int y=crnrMin.getY(); y<=crnrMax.getY(); y++) {
				for (int x=crnrMin.getX(); x<=crnrMax.getX(); x++) {
					int offset = e.offset(x, y);
					buffer.put( offset, valShort );
				}
			}
		}
	}

	@Override
	public boolean isEqualTo(ShortBuffer buffer, int operand) {
		return buffer.get()==operand;
	}


	@Override
	public void multiplyBy(double val) {
		
		if (val==1) {
			return;
		}
		
		for (int z=0; z<extnt().getZ(); z++) {
			
			ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			while( buffer.hasRemaining() ) {
				
				int mult = (int) (ByteConverter.unsignedShortToInt( buffer.get() ) * val);
				buffer.put( buffer.position()-1, (short) mult );
			}
		}
		
	}
	
	@Override
	public void setVoxel(int x, int y, int z, int val) {
		ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        buffer.put( getPlaneAccess().extnt().offset(x, y), (short) val );
	}

	@Override
	public int getVoxel(int x, int y, int z) {
		ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        return ByteConverter.unsignedShortToInt( buffer.get( getPlaneAccess().extnt().offset(x, y) ) );
	}
	
	// TODO when values are too small or too large
	@Override
	public void addPixelsCheckMask(ObjMask mask, int value) {
		
		BoundingBox bbox = mask.getBoundingBox();
		VoxelBox<ByteBuffer> objMaskBuffer = mask.getVoxelBox();
		
		byte maskOnByte = mask.getBinaryValuesByte().getOnByte();
				
		Point3i pntMax = bbox.calcCrnrMax();
		for (int z=bbox.getCrnrMin().getZ(); z<=pntMax.getZ(); z++) {
			
			ShortBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelsMask = objMaskBuffer.getPixelsForPlane(z-bbox.getCrnrMin().getZ()).buffer();
			
			for (int y=bbox.getCrnrMin().getY(); y<=pntMax.getY(); y++) {
				for (int x=bbox.getCrnrMin().getX(); x<=pntMax.getX(); x++) {
					
					int indexMask = getPlaneAccess().extnt().offset(x, y);
					if (pixelsMask.get(indexMask)==maskOnByte) {
						int index = getPlaneAccess().extnt().offset(x, y);
						
						short shortVal = (short) (pixels.get(index) + value);
						pixels.put(index, shortVal );
					}

				}
			}
		}
		
	}
	
	
	@Override
	public void scalePixelsCheckMask(ObjMask mask, double value) {
		throw new IllegalArgumentException("Currently unsupported method");
	}
	

	@Override
	public boolean isEqualTo(ShortBuffer buffer1, ShortBuffer buffer2) {
		return buffer1.get()==buffer2.get();
	}

	
	@Override
	public void subtractFrom(int val) {

		for (int z=0; z<extnt().getZ(); z++) {
			
			ShortBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			while( buffer.hasRemaining() ) {
				int newVal = val - buffer.get();
				buffer.put( buffer.position()-1, (short) newVal );
			}
		}
	}

	@Override
	public void max(VoxelBox<ShortBuffer> other)
			throws OperationFailedException {
		throw new OperationFailedException("unsupported operation");
	}
	


	@Override
	public VoxelBox<ShortBuffer> maxIntensityProj() {
		
		MaxIntensityBufferShort mi = new MaxIntensityBufferShort( extnt() ); 

		for (int z=0; z<extnt().getZ(); z++) {
			mi.projectSlice( getPlaneAccess().getPixelsForPlane(z).buffer() );
		}
	
		return mi.getProjection();
	}
	
	@Override
	public VoxelBox<ShortBuffer> meanIntensityProj() {
		MeanIntensityShortBuffer mi = new MeanIntensityShortBuffer( extnt() ); 

		for (int z=0; z<extnt().getZ(); z++) {
			mi.projectSlice( getPlaneAccess().getPixelsForPlane(z).buffer() );
		}
		
		return mi.getFlatBuffer();

	}
}
