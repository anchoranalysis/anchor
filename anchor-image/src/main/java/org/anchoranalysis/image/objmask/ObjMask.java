package org.anchoranalysis.image.objmask;

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

import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.objmask.factory.CreateFromConnectedComponentsFactory;
import org.anchoranalysis.image.objmask.intersecting.CountIntersectingPixelsBinary;
import org.anchoranalysis.image.objmask.intersecting.DetermineWhetherIntersectingPixelsBinary;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.box.thresholder.VoxelBoxThresholder;


/* 
 * An object expressed in voxels, bounded within overall space
 * 
 * A bounding-box defines a box within the overall space, and a raster-mask defines which voxels inside
 *  the bounding-box belong to the object.
 * 
 * Each voxel in the raster-mask must be one of two states, an ON value and an OFF value
 * 
 * These voxels are MUTABLE.
 */
public class ObjMask {
	
	private BoundedVoxelBox<ByteBuffer> delegate;
	private BinaryValues bv = new BinaryValues( 0, 255 );
	private BinaryValuesByte bvb = new BinaryValuesByte( (byte) 0, (byte) -1 );
	
	// Initialises a voxel box to match a BoundingBox size, with all values set to 0  
	public ObjMask(BoundingBox bbox) {
		super();
		delegate = new BoundedVoxelBox<>(bbox, VoxelBoxFactory.getByte() );
	}
	
	public ObjMask(BoundedVoxelBox<ByteBuffer> voxelBox) {
		super();
		delegate = voxelBox;
	}
	
	public ObjMask(BoundedVoxelBox<ByteBuffer> voxelBox, BinaryValues binaryValues) {
		super();
		delegate = voxelBox;
		this.bv = binaryValues.duplicate();
		this.bvb = binaryValues.createByte();
	}
	
	public ObjMask(VoxelBox<ByteBuffer> voxelBox ) {
		delegate = new BoundedVoxelBox<>(voxelBox);
	}
	
	public ObjMask(BinaryVoxelBox<ByteBuffer> voxelBox ) {
		delegate = new BoundedVoxelBox<>(voxelBox.getVoxelBox());
		this.bv = new BinaryValues( voxelBox.getBinaryValues() );
		this.bvb = new BinaryValuesByte( voxelBox.getBinaryValues() );
	}
	
	public ObjMask(BoundingBox BoundingBox, VoxelBox<ByteBuffer> voxelBox ) {
		delegate = new BoundedVoxelBox<>(BoundingBox, voxelBox);
	}
	
	public ObjMask(BoundingBox BoundingBox, VoxelBox<ByteBuffer> voxelBox, BinaryValuesByte binaryValues ) {
		delegate = new BoundedVoxelBox<>(BoundingBox, voxelBox);
		this.bv = binaryValues.createInt();
		this.bvb = new BinaryValuesByte( binaryValues );
	}
	
	public ObjMask(BoundingBox BoundingBox, VoxelBox<ByteBuffer> voxelBox, BinaryValues binaryValues ) throws CreateException {
		delegate = new BoundedVoxelBox<>(BoundingBox, voxelBox);
		this.bv = binaryValues.duplicate();
		this.bvb = binaryValues.createByte();
	}
	
	public ObjMask(BoundingBox BoundingBox, BinaryVoxelBox<ByteBuffer> voxelBox ) {
		delegate = new BoundedVoxelBox<>(BoundingBox,voxelBox.getVoxelBox());
		this.bv = new BinaryValues( voxelBox.getBinaryValues() );
		this.bvb = new BinaryValuesByte( voxelBox.getBinaryValues() );
	}
	
	// Copy constructor
	public ObjMask(ObjMask src) {
		super();
		delegate = new BoundedVoxelBox<>( src.delegate );
		bv = new BinaryValues( src.bv );
		bvb = new BinaryValuesByte( src.bvb );
	}

	public ObjMask duplicate() {
		ObjMask newMask = new ObjMask(this);
		return newMask;
	}

	public int numPixels() {
		return delegate.getVoxelBox().countEqual( bv.getOnInt() );
	}

	public void setVoxelBox(VoxelBox<ByteBuffer> voxelBox) {
		delegate.setVoxelBox(voxelBox);
	}

	public ObjMask flattenZ() {
		return new ObjMask( delegate.flattenZ(), bv.duplicate() );
	}

	public ObjMask growToZ(int sz) {
		return new ObjMask(
			delegate.growToZ(
				sz,
				VoxelBoxFactory.getByte()
			)
		);
	}

	public ObjMask growBuffer(Point3i neg, Point3i pos,
			Extent clipRegion) {
		return new ObjMask(
			delegate.growBuffer(
				neg,
				pos,
				clipRegion,
				VoxelBoxFactory.getByte()
			)
		);
	}
	
	
	public boolean equalsDeep( ObjMask other) {
		if(!delegate.equalsDeep(other.delegate)) {
			return false;
		}
		if(!bv.equals(other.bv)) {
			return false;
		}
		if(!bvb.equals(other.bvb)) {
			return false;
		}
		// DOES NOT CHECK factory
		return true;
	}

	public int countIntersectingPixels(ObjMask other) {
		return new CountIntersectingPixelsBinary(
			getBinaryValuesByte(),
			other.getBinaryValuesByte()
		).countIntersectingPixels(
			delegate,
			other.delegate
		);
	}
	
	public boolean hasIntersectingPixels(ObjMask other) {
		return new DetermineWhetherIntersectingPixelsBinary(
			getBinaryValuesByte(),
			other.getBinaryValuesByte()		
		).hasIntersectingPixels(delegate, other.delegate);
	}

	// Scales an objMask making sure to create a duplicate first
	public ObjMask scaleNew(double ratioX, double ratioY, Interpolator interpolator) throws OperationFailedException {
		
		if ((bv.getOnInt()==255 && bv.getOffInt()==0) || (bv.getOnInt()==0 && bv.getOffInt()==255)) {
			
			BoundedVoxelBox<ByteBuffer> boxNew = delegate.scaleNew(ratioX, ratioY, interpolator);
			
			// We should do a thresholding afterwards to make sure our values correspond to the two binary values
			if (interpolator!=null && interpolator.isNewValuesPossible()) {
				
				// We threshold to make sure it's still binary
				int thresholdVal = (bv.getOnInt() + bv.getOffInt()) /2;
				
				try {
					VoxelBoxThresholder.thresholdForLevel(boxNew.getVoxelBox(), thresholdVal, bv.createByte());
				} catch (CreateException e) {
					throw new OperationFailedException("Cannot convert binary values into bytes");
				}
			}
			
			return new ObjMask( boxNew, bv.duplicate() );
			
		} else {
			throw new OperationFailedException("Operation not supported for these binary values");
		}
	}
	
	// Scales an objMask replacing the existing mask
	public void scale(double ratioX, double ratioY, Interpolator interpolator ) throws OperationFailedException {
		
		if ((bv.getOnInt()==255 && bv.getOffInt()==0) || (bv.getOnInt()==0 && bv.getOffInt()==255)) {
			
			delegate.scale(ratioX, ratioY, interpolator );
			
			// We should do a thresholding afterwards to make sure our values correspond to the two binry values
			if (interpolator.isNewValuesPossible()) {
				
				// We threshold to make sure it's still binary
				int thresholdVal = (bv.getOnInt() + bv.getOffInt()) /2;
				
				try {
					VoxelBoxThresholder.thresholdForLevel(delegate.getVoxelBox(), thresholdVal, bv.createByte());
				} catch (CreateException e) {
					throw new OperationFailedException("Cannot convert binary values into bytes");
				}

				
			}
			
		} else {
			throw new OperationFailedException("Operation not supported for these binary values");
		}

	}
	
	/** Calculates center-of-gravity across all axes */
	public Point3d centerOfGravity() {
		return CenterOfGravityCalculator.calcCenterOfGravity(this);
	}

	/**
	 * Calculates center-of-gravity for one specific axis only
	 * @param axis the axis
	 * @return the center of gravity
	 */
	public double centerOfGravity(AxisType axis) {
		return CenterOfGravityCalculator.calcCenterOfGravityForAxis(this, axis);
	}

	public boolean sizesMatch() {
		return delegate.sizesMatch();
	}

	/**
	 * Tests if an object-mask is connected
	 * 
	 * TODO: this is not particular efficient. We can avoid making the ObjMaskCollection
	 * 
	 * @return
	 * @throws OperationFailedException 
	 */
	public boolean checkIfConnected() throws OperationFailedException {
		
		CreateFromConnectedComponentsFactory creator = new CreateFromConnectedComponentsFactory();
		creator.setBigNghb(true);
		ObjMaskCollection objs;
		try {
			objs = creator.createConnectedComponents(this.binaryVoxelBox().duplicate());
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
		return objs.size()<=1;
	}
	
	public boolean numPixelsLessThan( int num ) {
		
		Extent e = delegate.getVoxelBox().extnt();
		
		int cnt = 0;
		
		for (int z=0; z<e.getZ(); z++) {
			ByteBuffer bb = delegate.getVoxelBox().getPixelsForPlane(z).buffer();
			
			while( bb.hasRemaining() ) {
				byte b = bb.get();
				
				if (b==bvb.getOnByte()) {
					cnt++;
					
					if (cnt>=num) {
						//System.out.println( String.format("Reached count %d",cnt) );
						return false;
					}
				}
			}
			
		}
		//System.out.println( String.format("Did NOT reach count %d",cnt) );
		return true;
	}
	
	public boolean hasPixelsGreaterThan( int num ) {
		
		Extent e = delegate.getVoxelBox().extnt();
		
		int cnt = 0;
		
		for (int z=0; z<e.getZ(); z++) {
			ByteBuffer bb = delegate.getVoxelBox().getPixelsForPlane(z).buffer();
			
			while( bb.hasRemaining() ) {
				byte b = bb.get();
				
				if (b==bvb.getOnByte()) {
					cnt++;
					
					if (cnt>num) {
						//System.out.println( String.format("Reached count %d",cnt) );
						return true;
					}
				}
			}
			
		}
		//System.out.println( String.format("Did NOT reach count %d",cnt) );
		return false;
	}
	
	public ObjMask intersect( ObjMask othr, ImageDim dim ) {
		
		// we combine the two masks
		BoundingBox bboxIntersect = getBoundingBox().intersectCreateNew( othr.getBoundingBox(), dim.getExtnt() );
		
		if (bboxIntersect==null) {
			return null;
		}
		
		// We calculate a bounding box, which we write into in the omDest
		Point3i pntIntersectRelToSrc = bboxIntersect.relPosTo( getBoundingBox() );
		Point3i pntIntersectRelToOthr = bboxIntersect.relPosTo( othr.getBoundingBox() );
		
		
		
		BoundingBox bboxSrcMask = new BoundingBox(pntIntersectRelToSrc, bboxIntersect.extnt() );
		BoundingBox bboxOthrMask = new BoundingBox(pntIntersectRelToOthr, bboxIntersect.extnt() );
		
		BinaryValues bvOut = BinaryValues.getDefault();
		
		VoxelBox<ByteBuffer> vbMaskOut = VoxelBoxFactory.getByte().create( bboxIntersect.extnt() );
		vbMaskOut.setAllPixelsTo( bvOut.getOnInt() );
		
		BoundingBox allOut = new BoundingBox( vbMaskOut.extnt() );
		vbMaskOut.setPixelsCheckMask(allOut, getVoxelBox(), bboxSrcMask, bvOut.getOffInt(), this.getBinaryValuesByte().getOffByte() );
		vbMaskOut.setPixelsCheckMask(allOut, othr.getVoxelBox(), bboxOthrMask, bvOut.getOffInt(), othr.getBinaryValuesByte().getOffByte() );
		
		return new ObjMask(bboxIntersect, new BinaryVoxelBoxByte(vbMaskOut, bvOut));
	}
	
	public boolean contains( Point3i pnt ) {
		
		if (!delegate.getBoundingBox().contains(pnt)) {
			return false;
		}
		
		int xRel = pnt.getX() - delegate.getBoundingBox().getCrnrMin().getX();
		int yRel = pnt.getY() - delegate.getBoundingBox().getCrnrMin().getY();
		int zRel = pnt.getZ() - delegate.getBoundingBox().getCrnrMin().getZ();
		
		return delegate.getVoxelBox().getVoxel(xRel, yRel, zRel)==bv.getOnInt();
	}
	
	
	public boolean containsIgnoreZ( Point3i pnt ) {
		
		if (!delegate.getBoundingBox().containsIgnoreZ(pnt)) {
			return false;
		}
		
		int xRel = pnt.getX() - delegate.getBoundingBox().getCrnrMin().getX();
		int yRel = pnt.getY() - delegate.getBoundingBox().getCrnrMin().getY();
		
		Extent e = delegate.getBoundingBox().extnt();
		for( int z=0; z<e.getZ(); z++) {
			if (delegate.getVoxelBox().getVoxel(xRel, yRel, z)==bv.getOnInt()) {
				return true;
			}
		}
		return false;
	}

	public void convertToMaxIntensityProjection() {
		delegate.convertToMaxIntensityProjection();
	}

	public BoundingBox getBoundingBox() {
		return delegate.getBoundingBox();
	}

	public void setBoundingBox(BoundingBox BoundingBox) {
		delegate.setBoundingBox(BoundingBox);
	}

	public BinaryVoxelBox<ByteBuffer> binaryVoxelBox() {
		return new BinaryVoxelBoxByte( delegate.getVoxelBox(), bv );
	}
	
	public VoxelBox<ByteBuffer> getVoxelBox() {
		return delegate.getVoxelBox();
	}

	public BinaryValues getBinaryValues() {
		return bv;
	}
	
	public BinaryValuesByte getBinaryValuesByte() {
		return bvb;
	}
	
	public void moveToOrigin() {
		delegate.getBoundingBox().setCrnrMin( new Point3i() );
	}

	public void setBinaryValues(BinaryValues binaryValues) throws CreateException {
		this.bv = binaryValues;
		this.bvb = binaryValues.createByte();
	}
	
	public void setBinaryValues(BinaryValuesByte binaryValues) {
		this.bv = binaryValues.createInt();
		this.bvb = binaryValues;
	}

	public BoundedVoxelBox<ByteBuffer> getVoxelBoxBounded() {
		return delegate;
	}
	
	// omContained MUST be contained within the overall vox
	public void invertContainedMask( ObjMask omContained ) throws OperationFailedException {
		
		Point3i pntRel = omContained.getBoundingBox().relPosTo(getBoundingBox());
		BoundingBox bboxRel = new BoundingBox(pntRel, omContained.getBoundingBox().extnt());
		
		try {
			ObjMask omContainedRel = new ObjMask( bboxRel, omContained.getVoxelBox(), omContained.getBinaryValues() );
			
			getVoxelBox().setPixelsCheckMask(omContainedRel, getBinaryValuesByte().getOffByte());
			
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}

	// If keepZ is true the slice keeps its z coordinate, otherwise its set to 0
	public ObjMask extractSlice(int z, boolean keepZ) throws OperationFailedException {
		return new ObjMask(
			delegate.extractSlice(z, keepZ),
			this.bv.duplicate()
		);
	}
	
	public ObjMask clipToContainer(BoundingBox bboxContainer) throws OperationFailedException {
		assert( bboxContainer.hasIntersection(this.getBoundingBox()));
		if (bboxContainer.contains(getBoundingBox())) {
			// Nothing to do
			return this;
		} else {
			
			try {
				BoundingBox bboxIntersection = BoundingBox.intersect(getBoundingBox(), bboxContainer);
				
				// First we try to chop of the Zs, and see if it fits.
				// This is much less work than always processing all pixels, just for the sake of Z-slices
				ObjMask omSubslices = createVirtualSubrange(bboxIntersection.getCrnrMin().getZ(), bboxIntersection.calcCrnrMax().getZ() );
				
				if (bboxContainer.contains(omSubslices.getBoundingBox())) {
					return omSubslices;
				}
				
				ObjMask om = createSubmaskAvoidNew(bboxIntersection);
				assert( bboxContainer.contains(om.getBoundingBox()) );
				return om;
			} catch (CreateException e) {
				throw new OperationFailedException(e);
			}
		}
	}
	
	// Creates an ObjMask with a subrange of the slices. zMin inclusive, zMax inclusive
	public ObjMask createVirtualSubrange(int zMin, int zMax) throws CreateException {
		return new ObjMask(
			delegate.createVirtualSubrange(
				zMin,
				zMax,
				VoxelBoxFactory.getByte()
			),
			this.bv
		);
	}
	
	
	public ObjMask createSubmaskAvoidNew(BoundingBox bbox)
			throws CreateException {
		return new ObjMask(
			delegate.createBufferAvoidNew(bbox),
			this.bv.duplicate()
		);
	}

	public ObjMask createSubmaskAlwaysNew(BoundingBox bbox)
			throws CreateException {
		return new ObjMask(
			delegate.createBufferAlwaysNew(bbox),
			this.bv.duplicate()
		);
	}
	
	/**
	 * More relaxed-condition than a submask. It will create a mask
	 *   within a new bounding-box, so long as part of the bounding-box
	 *   intersects with the current mask
	 *   
	 * @param bbox
	 * @return
	 * @throws CreateException
	 */
	public ObjMask createIntersectingMaskAlwaysNew(BoundingBox bbox)
			throws CreateException {
		return new ObjMask(
			delegate.createIntersectingBufferAlwaysNew(bbox),
			this.bv.duplicate()
		);
	}
	
	public Point3i findAnyPntOnMask() {
		
		// First we try the mid-point
		Point3d pntD = getBoundingBox().midpoint();
		Point3i pnt = PointConverter.intFromDouble(pntD);
		if (contains(pnt)) {
			return new Point3i(pnt);
		}
		
		BinaryValuesByte bvb = getBinaryValuesByte();
		
		// Otherwise we iterate until we find ap oint
		for( int z=0; z<getBoundingBox().extnt().getZ(); z++) {
			
			ByteBuffer bbMask = getVoxelBox().getPixelsForPlane(z).buffer();
			
			for( int y=0; y<getBoundingBox().extnt().getY(); y++) {
				for( int x=0; x<getBoundingBox().extnt().getX(); x++) {
					if (bbMask.get()==bvb.getOnByte()) {
						
						return new Point3i(
							x+getBoundingBox().getCrnrMin().getX(),
							y+getBoundingBox().getCrnrMin().getY(),
							z+getBoundingBox().getCrnrMin().getZ()
						);
					}
				}
			}
		}
		
		return null;
	}

	public void setIntersectingPixels(ObjMask omCompare1, ObjMask omCompare2,
			ImageDim dim, int setVal) {
		// Let's make new ObjMasks that are relative to delegate
		
		ObjMask rel1 = omCompare1.relMaskTo(delegate.getBoundingBox());
		ObjMask rel2 = omCompare2.relMaskTo(delegate.getBoundingBox());
		
		delegate.setIntersectingPixels(
			rel1,
			rel2,
			dim,
			setVal,
			VoxelBoxFactory.getByte()
		);
	}
	
	// Creates a new objMask that is relative to another bbox
	public ObjMask relMaskTo( BoundingBox bbox ) {
		Point3i pnt = delegate.getBoundingBox().relPosTo(bbox);
		
		BoundingBox bboxNew = new BoundingBox(pnt, delegate.extnt());
		
		return new ObjMask( bboxNew, delegate.getVoxelBox() );
	}
	
	@Override
	public String toString() {
		return String.format("%s (cog=%s,numPixels=%d)", super.toString(), centerOfGravity().toString(), numPixels() );
	}


}