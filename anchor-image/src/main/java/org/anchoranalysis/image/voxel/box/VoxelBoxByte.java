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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.pixelsforplane.IPixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.max.MaxIntensityBufferByte;
import org.anchoranalysis.image.voxel.buffer.mean.MeanIntensityByteBuffer;

public final class VoxelBoxByte extends VoxelBox<ByteBuffer> {

	public VoxelBoxByte(IPixelsForPlane<ByteBuffer> pixelsForPlane) {
		super(
			pixelsForPlane,
			VoxelBoxFactory.getByte()
		);
	}
	
	
	public static int ceilOfMaxPixel( IPixelsForPlane<?> planeAccess ) {
		int max = 0;
		boolean first = true;
		
		int sizeXY = planeAccess.extent().getVolumeXY();
		for (int z=0; z<planeAccess.extent().getZ(); z++) {
			
			VoxelBuffer<?> pixels = planeAccess.getPixelsForPlane(z);
			
			for(int offset=0; offset<sizeXY; offset++) {
				
				int val = pixels.getInt(offset);
				if (first || val > max) {
					max = val;
					first = false;
				}
			}
			
		}
		return max;
	}
	
	
	@Override
	public int ceilOfMaxPixel() {
		return ceilOfMaxPixel(getPlaneAccess());
	}

	@Override
	public void copyItem(ByteBuffer srcBuffer, int srcIndex,
			ByteBuffer destBuffer, int destIndex) {
		destBuffer.put( destIndex, srcBuffer.get(srcIndex) );
	}

	@Override
	public boolean isGreaterThan(ByteBuffer buffer, int operand) {
		return ByteConverter.unsignedByteToInt(buffer.get()) > operand;
	}
	
	@Override
	public ObjectMask equalMask( BoundingBox bbox, int equalVal ) {
		
		ObjectMask om = new ObjectMask(bbox );
		
		ReadableTuple3i pntMax = bbox.calcCornerMax();
		
		byte equalValByte = (byte) equalVal;
		byte maskOnVal = om.getBinaryValuesByte().getOnByte();
		
		for (int z=bbox.getCornerMin().getZ(); z<=pntMax.getZ(); z++) {
			
			ByteBuffer pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelOut = om.getVoxelBox().getPixelsForPlane(z - bbox.getCornerMin().getZ()).buffer();
			
			int ind = 0;
			for (int y=bbox.getCornerMin().getY(); y<=pntMax.getY(); y++) {
				for (int x=bbox.getCornerMin().getX(); x<=pntMax.getX(); x++) {
					
					int index = getPlaneAccess().extent().offset(x, y);
					byte chnlVal = pixelIn.get(index);
					
					if ( chnlVal==equalValByte ) {
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

		byte valByte = (byte) val;
		
		for (int z=0; z<extent().getZ(); z++) {
			
			ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			while( buffer.hasRemaining() ) {
				buffer.put( valByte );
			}
		}
	}
	
	@Override
	public void setPixelsTo( BoundingBox bbox, int val ) {

		byte valByte = (byte) val;
		
		ReadableTuple3i crnrMin = bbox.getCornerMin();
		ReadableTuple3i crnrMax = bbox.calcCornerMax();
		Extent e = extent();
		
		for (int z=crnrMin.getZ(); z<=crnrMax.getZ(); z++) {
			
			ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			for (int y=crnrMin.getY(); y<=crnrMax.getY(); y++) {
				for (int x=crnrMin.getX(); x<=crnrMax.getX(); x++) {
					int offset = e.offset(x, y);
					buffer.put( offset, valByte );
				}
			}
		}
	}


	@Override
	public boolean isEqualTo(ByteBuffer buffer, int operand) {
		return ByteConverter.unsignedByteToInt(buffer.get())==operand;
	}


	@Override
	public VoxelBox<ByteBuffer> maxIntensityProj() {
		
		MaxIntensityBufferByte mi = new MaxIntensityBufferByte( extent() ); 

		for (int z=0; z<extent().getZ(); z++) {
			mi.projectSlice( getPlaneAccess().getPixelsForPlane(z).buffer() );
		}
	
		return mi.getProjection();
	}
	
	@Override
	public VoxelBox<ByteBuffer> meanIntensityProj() {
		MeanIntensityByteBuffer mi = new MeanIntensityByteBuffer( extent() ); 

		for (int z=0; z<extent().getZ(); z++) {
			mi.projectSlice( getPlaneAccess().getPixelsForPlane(z).buffer() );
		}
		
		return mi.getFlatBuffer();

	}


	@Override
	public void multiplyBy(double val) {
		
		if (val==1) {
			return;
		}
		
		for (int z=0; z<extent().getZ(); z++) {
			
			ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			while( buffer.hasRemaining() ) {
				int mult = (int) (ByteConverter.unsignedByteToInt( buffer.get() ) * val);
				buffer.put( buffer.position()-1, (byte) mult );
			}
		}
		
	}
	
	@Override
	public void setVoxel(int x, int y, int z, int val) {
		ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        buffer.put( getPlaneAccess().extent().offset(x, y), (byte) val );
	}

	@Override
	public int getVoxel(int x, int y, int z) {
		ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
        return ByteConverter.unsignedByteToInt( buffer.get( getPlaneAccess().extent().offset(x, y) ) );
	}
	

	
	


	
	@Override
	public void scalePixelsCheckMask(ObjectMask mask, double value) {
		
		BoundingBox bbox = mask.getBoundingBox();
		VoxelBox<ByteBuffer> objMaskBuffer = mask.getVoxelBox();

		byte maskOnByte = mask.getBinaryValuesByte().getOnByte();
		
		ReadableTuple3i pntMax = bbox.calcCornerMax();
		for (int z=bbox.getCornerMin().getZ(); z<=pntMax.getZ(); z++) {
			
			ByteBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelsMask = objMaskBuffer.getPixelsForPlane(z-bbox.getCornerMin().getZ()).buffer();
			
			for (int y=bbox.getCornerMin().getY(); y<=pntMax.getY(); y++) {
				for (int x=bbox.getCornerMin().getX(); x<=pntMax.getX(); x++) {
										
					if (pixelsMask.get()==maskOnByte) {
						int index = getPlaneAccess().extent().offset(x, y);
						
						int intVal = scaleClipped(
							value,
							ByteConverter.unsignedByteToInt(
								pixels.get(index)
							)
						);
						pixels.put(index, (byte) intVal );
					}
				}
			}
		}
		
	}
	
	@Override
	public void addPixelsCheckMask(ObjectMask mask, int value) {
		
		BoundingBox bbox = mask.getBoundingBox();
		VoxelBox<ByteBuffer> objMaskBuffer = mask.getVoxelBox();

		byte maskOnByte = mask.getBinaryValuesByte().getOnByte();
		
		ReadableTuple3i pntMax = bbox.calcCornerMax();
		for (int z=bbox.getCornerMin().getZ(); z<=pntMax.getZ(); z++) {
			
			ByteBuffer pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelsMask = objMaskBuffer.getPixelsForPlane(z-bbox.getCornerMin().getZ()).buffer();
			
			for (int y=bbox.getCornerMin().getY(); y<=pntMax.getY(); y++) {
				for (int x=bbox.getCornerMin().getX(); x<=pntMax.getX(); x++) {
										
					if (pixelsMask.get()==maskOnByte) {
						int index = getPlaneAccess().extent().offset(x, y);
						
						int intVal = addClipped(
							value,
							ByteConverter.unsignedByteToInt(
								pixels.get(index)
							)
						);
						pixels.put(index, (byte) intVal );
					}
				}
			}
		}
		
	}
	
	@Override
	public boolean isEqualTo(ByteBuffer buffer1, ByteBuffer buffer2) {
		return buffer1.get()==buffer2.get();
	}

	@Override
	public void subtractFrom(int val) {

		for (int z=0; z<extent().getZ(); z++) {
			
			ByteBuffer buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			while( buffer.hasRemaining() ) {
				int newVal = val - ByteConverter.unsignedByteToInt( buffer.get() );
				buffer.put( buffer.position()-1, (byte) newVal );
			}
		}
	}

	@Override
	public void max( VoxelBox<ByteBuffer> other ) throws OperationFailedException {
		
		if (!extent().equals(other.extent())) {
			throw new OperationFailedException("other must have same extent");
		}

		int vol = getPlaneAccess().extent().getVolumeXY();
		
		for (int z=0; z<getPlaneAccess().extent().getZ(); z++) {
			
			ByteBuffer buffer1 = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer buffer2 = other.getPlaneAccess().getPixelsForPlane(z).buffer();
			
			int indx = 0;
			while(indx<vol) {
				
				int elem1 = ByteConverter.unsignedByteToInt( buffer1.get(indx) );
				int elem2 = ByteConverter.unsignedByteToInt( buffer2.get(indx) );
				
				if( elem2 > elem1) {
					buffer1.put(indx, (byte) elem2);
				}

				indx++;
			}
		}
	}
		
	private static int scaleClipped(double value, int pixelValue) {
		int intVal = (int) Math.round(value * pixelValue);
		if (intVal<0) {
			return 0;
		}
		if (intVal>255) {
			return 255;
		}
		return intVal;
	}
	
	private static int addClipped(int value, int pixelValue) {
		int intVal = pixelValue + value;
		if (intVal<0) {
			return 0;
		}
		if (intVal>255) {
			return 255;
		}
		return intVal;
	}
}
