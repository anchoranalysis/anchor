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


import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.interpolator.InterpolateUtilities;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.box.pixelsforplane.IPixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> buffer-type
 */
public abstract class VoxelBox<T extends Buffer> {

	private final IPixelsForPlane<T> planeAccess;
	private final VoxelBoxFactoryTypeBound<T> factory;
	
	public VoxelBox(IPixelsForPlane<T> pixelsForPlane, VoxelBoxFactoryTypeBound<T> factory) {
		super();
		this.planeAccess = pixelsForPlane;
		this.factory = factory;
	}

	public IPixelsForPlane<T> getPlaneAccess() {
		return planeAccess;
	}
	
	public VoxelDataType dataType() {
		return factory.dataType();
	}
	
	public VoxelBox<T> createBufferAvoidNew(BoundingBox bbox ) {
		
		if (   bbox.equals(new BoundingBox(extnt()))
			&& bbox.getCrnrMin().getX()==0
			&& bbox.getCrnrMin().getY()==0
			&& bbox.getCrnrMin().getZ()==0
		) {
			return this;
		}
		return createBufferAlwaysNew(bbox);
	}
	
	public VoxelBox<T> createBufferAlwaysNew(BoundingBox bbox) {
		
		// Otherwise we create a new buffer
		VoxelBox<T> vbOut = factory.create(bbox.extnt());
		copyPixelsTo(bbox, vbOut, new BoundingBox(bbox.extnt()));
		return vbOut;
	}
	
	
	public void replaceBy(VoxelBox<T> vb) throws IncorrectImageSizeException {
		
		if (!extnt().equals(vb.extnt())) {
			throw new IncorrectImageSizeException("Sizes do not match");
		}
		
		BoundingBox bbo = new BoundingBox(vb.extnt());
		vb.copyPixelsTo(bbo, this, bbo);
	}

	/**
	 * Copies pixels from this object to another object
	 * 
	 * @param sourceBox relative to the current object
	 * @param destVoxelBox 
	 * @param destBox relative to destVoxelBox
	 */
	public void copyPixelsTo(BoundingBox sourceBox, VoxelBox<T> destVoxelBox,
			BoundingBox destBox) {
		
		checkExtentMatch(sourceBox, destBox);
		
		Point3i srcStart = sourceBox.getCrnrMin();
		Point3i srcEnd = sourceBox.calcCrnrMax();
		
		Point3i relPos = destBox.relPosTo(sourceBox);
		
		for (int z=srcStart.getZ(); z<=srcEnd.getZ(); z++ ) {
			
			assert( z< extnt().getZ() );
			
			T srcArr = getPlaneAccess().getPixelsForPlane(z).buffer();
			T destArr = destVoxelBox.getPlaneAccess().getPixelsForPlane(z + relPos.getZ()).buffer();
			
			for (int y=srcStart.getY(); y<=srcEnd.getY(); y++) {
				for (int x=srcStart.getX(); x<=srcEnd.getX(); x++) {
				
					int srcIndex = getPlaneAccess().extnt().offset(x, y);
					int destIndex = destVoxelBox.extnt().offset(x + relPos.getX(), y+relPos.getY());
					
					copyItem( srcArr, srcIndex, destArr, destIndex );
					//destArr.put( destIndex, srcArr.get(srcIndex) );
				}
			}
		}
	}
	
	
	// Only copies pixels if part of an ObjMask, otherwise we set a null pixel
	public void copyPixelsToCheckMask(BoundingBox sourceBox, VoxelBox<T> destVoxelBox,
			BoundingBox destBox, VoxelBox<ByteBuffer> objMaskBuffer, BinaryValuesByte maskBV ) {
		
		checkExtentMatch(sourceBox, destBox);
		
		Point3i srcStart = sourceBox.getCrnrMin();
		Point3i srcEnd = sourceBox.calcCrnrMax();
		
		Point3i relPos = destBox.relPosTo(sourceBox);
		
		for (int z=srcStart.getZ(); z<=srcEnd.getZ(); z++ ) {
			
			T srcArr = getPlaneAccess().getPixelsForPlane(z).buffer();
			T destArr = destVoxelBox.getPlaneAccess().getPixelsForPlane(z + relPos.getZ()).buffer();

			ByteBuffer maskBuffer = objMaskBuffer.getPixelsForPlane(z-srcStart.getZ()).buffer();
			
			for (int y=srcStart.getY(); y<=srcEnd.getY(); y++) {
				for (int x=srcStart.getX(); x<=srcEnd.getX(); x++) {
				
					int srcIndex = getPlaneAccess().extnt().offset(x, y);
					int destIndex = destVoxelBox.extnt().offset(x + relPos.getX(), y+relPos.getY());
					
					if(maskBuffer.get()==maskBV.getOnByte()) {
						copyItem( srcArr, srcIndex, destArr, destIndex );
					}
					//destArr.put( destIndex, srcArr.get(srcIndex) );
				}
			}
		}
	}
	
	
	/**
	 * Sets pixels in a box to a particular value if they match an Object-Mask
	 * 
	 * See {@ #setPixelsCheckMask(BoundingBox, VoxelBox, BoundingBox, int, byte) for details
	 * 
	 * @param om the object-mask to restrict which values in the buffer are written to
	 * @param value value to be set in matched pixels
	 * @return the number of pixels successfully "set"
	 */
	public int setPixelsCheckMask( ObjMask om, int value ) {
		assert( om!= null );
		assert( om.getBoundingBox()!=null );
		assert( om.getVoxelBox()!=null );
		return setPixelsCheckMask(
			om.getBoundingBox(),
			om.getVoxelBox(),
			new BoundingBox(om.getBoundingBox().extnt()),
			value,
			om.getBinaryValuesByte().getOnByte()
		);
	}
	
	
	/**
	 * Sets pixels in a box to a particular value if they match an Object-Mask
	 * 
	 * See {@ #setPixelsCheckMask(BoundingBox, VoxelBox, BoundingBox, int, byte) for details
	 * 
	 * @param om the object-mask to restrict which values in the buffer are written to
	 * @param value value to be set in matched pixels
	 * @param maskMatchValue what's an "On" value for the mask to match against?
	 * @return the number of pixels successfully "set"
	 */
	public int setPixelsCheckMask( ObjMask om, int value, byte maskMatchValue ) {
		assert( om!= null );
		assert( om.getBoundingBox()!= null );
		assert( om.getVoxelBox()!= null );
		return setPixelsCheckMask(
			om.getBoundingBox(),
			om.getVoxelBox(),
			new BoundingBox(om.getBoundingBox().extnt()),
			value,
			maskMatchValue
		);
	}
	
	/**
	 * Sets pixels in a box to a particular value if they match an Object-Mask... with more customization
	 *
	 * <p>Pixels are unchanged if they do not match the mask</p>
	 * <p>Bounding boxes can be used to restrict regions in both the source and destination, but must be equal in volume.</p>
	 * 
	 * @param bboxToBeAssigned which part of the buffer to write to
	 * @param objMaskBuffer the byte-buffer for the mask
	 * @param bboxMask which part of the mask to consider as input
	 * @param value value to be set in matched pixels
	 * @param maskMatchValue what's an "On" value for the mask to match against?
	 * @return the number of pixels successfully "set"
	 */
	public int setPixelsCheckMask(
		BoundingBox bboxToBeAssigned,
		VoxelBox<ByteBuffer> objMaskBuffer,
		BoundingBox bboxMask,
		int value,
		byte maskMatchValue
	) {
		checkExtentMatch(bboxMask, bboxToBeAssigned);
		
		Extent eIntersectingBox = bboxMask.extnt();
		
		Extent eAssignBuffer = this.extnt();
		Extent eMaskBuffer = objMaskBuffer.extnt();
		
		int cnt = 0;
		
		for (int z=0; z<eIntersectingBox.getZ(); z++) {
			
			VoxelBuffer<?> pixels = getPlaneAccess().getPixelsForPlane(z + bboxToBeAssigned.getCrnrMin().getZ());
			ByteBuffer pixelsMask = objMaskBuffer.getPixelsForPlane(z + bboxMask.getCrnrMin().getZ()).buffer();
			
			for (int y=0; y<eIntersectingBox.getY(); y++) {
				for (int x=0; x<eIntersectingBox.getX(); x++) {

					int indexMask = eMaskBuffer.offset(x + bboxMask.getCrnrMin().getX(), y + bboxMask.getCrnrMin().getY());
					
					if (pixelsMask.get(indexMask)==maskMatchValue) {
						int indexAssgn = eAssignBuffer.offset(
							x + bboxToBeAssigned.getCrnrMin().getX(),
							y + bboxToBeAssigned.getCrnrMin().getY()
						);
						pixels.putInt(indexAssgn, value );
						cnt++;
					}
				}
			}
			
		}
		
		return cnt;
	}
	
	public abstract void addPixelsCheckMask( ObjMask mask, int value );
	
	public abstract void scalePixelsCheckMask( ObjMask mask, double value );
	
	public abstract void subtractFrom( int val );
	
	
	public ObjMask equalMask( BoundingBox bbox, int equalVal ) {
		
		ObjMask om = new ObjMask( new BoundingBox(bbox) );
		
		Point3i pntMax = bbox.calcCrnrMax();
		
		byte maskOut = om.getBinaryValuesByte().getOnByte();
		
		for (int z=bbox.getCrnrMin().getZ(); z<=pntMax.getZ(); z++) {
			
			T pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelOut = om.getVoxelBox().getPixelsForPlane(z - bbox.getCrnrMin().getZ()).buffer();
			
			int ind = 0;
			for (int y=bbox.getCrnrMin().getY(); y<=pntMax.getY(); y++) {
				for (int x=bbox.getCrnrMin().getX(); x<=pntMax.getX(); x++) {
					
					int index = getPlaneAccess().extnt().offset(x, y);
					
					pixelIn.position(index);
					
					if (isEqualTo(pixelIn, equalVal)) {
						pixelOut.put(ind, maskOut );
					}
					
					ind++;
				}
			}
		}
		
		return om;
	}
	
	
	public ObjMask greaterThanMask( BoundingBox bbox, int equalVal ) {
		
		ObjMask om = new ObjMask( new BoundingBox(bbox) );
		
		Point3i pntMax = bbox.calcCrnrMax();
		
		byte maskOut = om.getBinaryValuesByte().getOnByte();
		
		for (int z=bbox.getCrnrMin().getZ(); z<=pntMax.getZ(); z++) {
			
			T pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelOut = om.getVoxelBox().getPixelsForPlane(z - bbox.getCrnrMin().getZ()).buffer();
			
			int ind = 0;
			for (int y=bbox.getCrnrMin().getY(); y<=pntMax.getY(); y++) {
				for (int x=bbox.getCrnrMin().getX(); x<=pntMax.getX(); x++) {
					
					int index = getPlaneAccess().extnt().offset(x, y);
					
					pixelIn.position(index);
					
					if (isGreaterThan(pixelIn, equalVal)) {
						pixelOut.put(ind, maskOut );
					}
					
					ind++;
				}
			}
		}
		
		return om;
	}
	
	
	public ObjMask greaterThanMask( ObjMask maskIn, int equalVal ) {
		
		ObjMask maskOut = new ObjMask(
			new BoundingBox(
				maskIn.getBoundingBox()
			)
		);
		
		BoundingBox bbox = maskIn.getBoundingBox();
		Point3i pntMax = bbox.calcCrnrMax();
		
		byte maskInVal = maskIn.getBinaryValuesByte().getOnByte();
		byte maskOutVal = maskOut.getBinaryValuesByte().getOnByte();
		
		for (int z=bbox.getCrnrMin().getZ(); z<=pntMax.getZ(); z++) {
			
			T pixelIn = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer pixelMaskIn = maskIn.getVoxelBox().getPixelsForPlane(z).buffer();
			ByteBuffer pixelOut = maskOut.getVoxelBox().getPixelsForPlane(z - bbox.getCrnrMin().getZ()).buffer();
			
			int ind = 0;
			for (int y=bbox.getCrnrMin().getY(); y<=pntMax.getY(); y++) {
				for (int x=bbox.getCrnrMin().getX(); x<=pntMax.getX(); x++) {
					
					int index = getPlaneAccess().extnt().offset(x, y);
					
					pixelIn.position(index);
					
					byte maskVal = pixelMaskIn.get(ind);
					
					if (maskVal==maskInVal && isGreaterThan(pixelIn, equalVal)) {
						pixelOut.put(ind, maskOutVal);
					}
										
					ind++;
				}
			}
		}
		
		return maskOut;
	}
	

	public int countGreaterThan( int operand ) {
		
		int cnt = 0;
		
		for (int z=0; z<getPlaneAccess().extnt().getZ(); z++) {
			
			T buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			while( buffer.hasRemaining() ) {
				
				if ( isGreaterThan(buffer,operand) ) {
					cnt++;
				}
			}
			
		}
		return cnt;
	}
	
	public boolean hasGreaterThan( int operand ) {
		
		for (int z=0; z<getPlaneAccess().extnt().getZ(); z++) {
			
			T buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			while( buffer.hasRemaining() ) {
				
				if ( isGreaterThan(buffer,operand) ) {
					return true;
				}
			}
			
		}
		return false;
	}
	
	
	public boolean hasEqualTo( int operand ) {
		
		for (int z=0; z<getPlaneAccess().extnt().getZ(); z++) {
			
			T buffer = getPlaneAccess().getPixelsForPlane(z).buffer();
			while( buffer.hasRemaining() ) {
				
				if ( isEqualTo(buffer,operand) ) {
					return true;
				}
			}
			
		}
		return false;
	}
	
	
	
	public int countEqual( int equalVal ) {
		
		int count = 0;
		
		for (int z=0; z<getPlaneAccess().extnt().getZ(); z++) {
			T pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
			
			while (pixels.hasRemaining()) {
				if (isEqualTo(pixels, equalVal )) {
					count++;
				}
			}
		}
		return count;
	}
	
	
	public int countEqualMask( int equalVal, ObjMask om ) {
		
		Point3i srcStart = om.getBoundingBox().getCrnrMin();
		Point3i srcEnd = om.getBoundingBox().calcCrnrMax();

		int count = 0;
		
		byte maskOnVal = om.getBinaryValuesByte().getOnByte();
		
		for (int z=srcStart.getZ(); z<=srcEnd.getZ(); z++ ) {
			
			T srcArr = getPlaneAccess().getPixelsForPlane(z).buffer();
			ByteBuffer maskBuffer = om.getVoxelBox().getPixelsForPlane(z-srcStart.getZ()).buffer();
			
			for (int y=srcStart.getY(); y<=srcEnd.getY(); y++) {
				for (int x=srcStart.getX(); x<=srcEnd.getX(); x++) {

					int maskIndex = om.getVoxelBox().extnt().offset(x-srcStart.getX(), y-srcStart.getY());
					
					if (maskBuffer.get(maskIndex)==maskOnVal) {
					
						int srcIndex = getPlaneAccess().extnt().offset(x, y);
						srcArr.position(srcIndex);
						
						if (isEqualTo(srcArr, equalVal )) {
							count++;
						}
					}
					//destArr.put( destIndex, srcArr.get(srcIndex) );
				}
			}
		}
		return count;
	}
	
	public abstract int ceilOfMaxPixel();
	
	public abstract void copyItem( T srcBuffer, int srcIndex, T destBuffer, int destIndex );
	
	public abstract boolean isGreaterThan( T buffer, int operand );
	
	public abstract boolean isEqualTo( T buffer, int operand );
	
	public abstract boolean isEqualTo( T buffer1, T buffer2 );
	
	public abstract void setAllPixelsTo( int val );
	
	public abstract void setPixelsTo( BoundingBox bbox, int val );
	
	public abstract void multiplyBy( double val );

	public abstract VoxelBox<T> maxIntensityProj();
	
	public abstract VoxelBox<T> meanIntensityProj();
	
	public void transferPixelsForPlane(int z, VoxelBox<T> src, int zSrc, boolean duplicate ) {
		if (duplicate) {
			setPixelsForPlane(z, src.getPixelsForPlane(zSrc));
		} else {
			setPixelsForPlane(z, src.getPixelsForPlane(zSrc).duplicate() );
		}
	}
	
	public void setPixelsForPlane(int z, VoxelBuffer<T> pixels) {
		planeAccess.setPixelsForPlane(z, pixels);
	}

	public VoxelBuffer<T> getPixelsForPlane(int z) {
		return planeAccess.getPixelsForPlane(z);
	}

	public Extent extnt() {
		return planeAccess.extnt();
	}

	public int minSliceNonZero() {
		
		for (int z=0; z<extnt().getZ(); z++) {
			if(isSliceNonZero(z)) {
				return z;
			}
		}
		
		return -1;
	}
	
	public boolean isSliceNonZero( int z ) {
		T pixels = getPlaneAccess().getPixelsForPlane(z).buffer();
		
		while (pixels.hasRemaining()) {
			if (!isEqualTo(pixels, 0 )) {
				return true;
			}
		}
		return false;
	}
	
	public abstract void setVoxel(int x, int y, int z, int val);

	// Very slow access, use sparingly.  Instead process slice by slice.
	public abstract int getVoxel(int x, int y, int z);
	
	// Creates a new channel contain a duplication only of a particular slice
	public VoxelBox<T> extractSlice( int z ) {
		
		Extent e = new Extent( extnt());
		e.setZ(1);
		
		VoxelBox<T> bufferAccess = factory.create(e);
		bufferAccess.getPlaneAccess().setPixelsForPlane(0, getPlaneAccess().getPixelsForPlane(z) );
		return bufferAccess;
	}
	
	/**
	 * Resizes the current voxelbox.  
	 * 
	 * @param x
	 * @param y
	 * @param interpolator	If non-null, this is used to interpolate pixels as they are resized.
	 * @param averageWhenDownsizing
	 * @return
	 */
	public VoxelBox<T> resizeXY( int x, int y, Interpolator interpolator ) {
		
		Extent e = new Extent( extnt());
		e.setXY(x, y);
		
		VoxelBox<T> bufferTarget = factory.create(e);
		
		assert(bufferTarget.getPixelsForPlane(0).buffer().capacity()==e.getVolumeXY());
		
		VoxelBoxWrapper srcWrapped = new VoxelBoxWrapper( this );
		VoxelBoxWrapper trgtWrapped = new VoxelBoxWrapper( bufferTarget);
		
		InterpolateUtilities.transferSlicesResizeXY( srcWrapped, trgtWrapped, interpolator );
			
		assert(bufferTarget.getPixelsForPlane(0).buffer().capacity()==e.getVolumeXY());
		return bufferTarget;
	}
	
	public VoxelBox<T> duplicate() {
		
		assert( getPlaneAccess().extnt().getZ() > 0 );
		
		VoxelBox<T> bufferAccess = factory.create( getPlaneAccess().extnt() );
		
		for( int z=0; z<extnt().getZ(); z++ ) {
			VoxelBuffer<T> buffer = getPixelsForPlane(z);
			bufferAccess.setPixelsForPlane( z, buffer.duplicate() );
		}
		
		return bufferAccess;
	}
	
	/**
	 * Is the buffer identical to another beautiful (deep equals)
	 * 
	 * @param other
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean equalsDeep( VoxelBox<?> other) {
		
		if (!factory.dataType().equals(other.getFactory().dataType())) {
			return false;
		}
		
		if (!extnt().equals(other.extnt())) {
			return false;
		}
		
		for (int z=0; z<getPlaneAccess().extnt().getZ(); z++) {
			
			VoxelBuffer<T> buffer1 = getPlaneAccess().getPixelsForPlane(z);
			VoxelBuffer<T> buffer2 = (VoxelBuffer<T>) other.getPlaneAccess().getPixelsForPlane(z);
			
			while( buffer1.buffer().hasRemaining() ) {
				
				if ( !isEqualTo(buffer1.buffer(), buffer2.buffer()) ) {
					return false;
				}
			}
			
			assert( !buffer2.buffer().hasRemaining() );
			
		}
		
		return true;
	}
	
	public abstract void max( VoxelBox<T> other ) throws OperationFailedException;

	public VoxelBoxFactoryTypeBound<T> getFactory() {
		return factory;
	}
	
	private static void checkExtentMatch(BoundingBox bbox1, BoundingBox bbox2) {
		Extent extent1 = bbox1.extnt();
		Extent extent2 = bbox2.extnt();
		if (!extent1.equals(extent2)) {
			throw new IllegalArgumentException(
				String.format(
					"The extents of the two bounding-boxes are not identical: %s vs %s",
					extent1,
					extent2
				)
			);
		}
	}
}
