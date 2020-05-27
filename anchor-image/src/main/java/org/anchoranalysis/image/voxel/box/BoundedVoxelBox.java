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
import java.util.Optional;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.box.pixelsforplane.IPixelsForPlane;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

/**
 * A a voxel buffer representing an object
 *  i.e. a region bounded in space
 * 
 * @author FEEHANO
 *
 * @param <T> BufferType
 */
public class BoundedVoxelBox<T extends Buffer> {

	private static final Point3i allOnes2D = new Point3i(1,1,0);
	private static final Point3i allOnes3D = new Point3i(1,1,1);
	
	private BoundingBox boundingBox;
	private VoxelBox<T> voxelBox;
	
	// Initialises a voxel box to match a BoundingBox size, with all values set to 0  
	public BoundedVoxelBox(BoundingBox bbox, VoxelBoxFactoryTypeBound<T> factory) {
		super();
		this.boundingBox = bbox;
		assert(!bbox.extent().isEmpty());
		assert(bbox.extent().getZ()>0);
		this.voxelBox = factory.create( bbox.extent() );
		assert(sizesMatch());
	}
	
	public BoundedVoxelBox(VoxelBox<T> voxelBox ) {
		this.boundingBox = new BoundingBox( voxelBox.extent() );
		this.voxelBox = voxelBox;
	}
	
	public BoundedVoxelBox(BoundingBox boundingBox, VoxelBox<T> voxelBox ) {
		this.boundingBox = boundingBox;
		this.voxelBox = voxelBox;
	}
	
	// Copy constructor
	public BoundedVoxelBox(BoundedVoxelBox<T> src) {
		super();
		this.boundingBox = src.getBoundingBox();
		assert( src.voxelBox.extent().getZ() > 0 );
		this.voxelBox = src.voxelBox.duplicate();
		assert( this.voxelBox.extent().equals( src.voxelBox.extent() ));
	}
	
	public boolean equalsDeep( BoundedVoxelBox<?> other) {
		
		if(!boundingBox.equals(other.boundingBox)) {
			return false;
		}
		if(!voxelBox.equalsDeep(other.voxelBox)) {
			return false;
		}
		return true;
	}
	
	public void setVoxelBox(VoxelBox<T> voxelBox) {
		this.voxelBox = voxelBox;
	}
	
	// To enable compiling, as otherwise there as a type collision
	@SuppressWarnings("unchecked")
	public void setVoxelBoxSkipType(VoxelBox<? extends Buffer> voxelBox) {
		this.voxelBox = (VoxelBox<T>) voxelBox;
	}
	
	public BoundedVoxelBox<T> flattenZ() {
		BoundingBox bboxNew = this.boundingBox.flattenZ();
		return new BoundedVoxelBox<>( bboxNew, voxelBox.maxIntensityProj() );
	}
	
	public BoundedVoxelBox<T> growToZ( int sz, VoxelBoxFactoryTypeBound<T> factory ) {
		assert(this.boundingBox.extent().getZ()==1);
		assert(this.voxelBox.extent().getZ()==1);
		
		BoundingBox bboxNew = new BoundingBox(
			boundingBox.getCrnrMin(),
			boundingBox.extent().duplicateChangeZ(sz)
		);
		
		VoxelBox<T> buffer = factory.create(bboxNew.extent());
		
		Extent e = this.boundingBox.extent();
		BoundingBox bboxSrc = new BoundingBox(e);
		
		// we copy in one by one
		for (int z=0;z<buffer.extent().getZ();z++) {
			this.voxelBox.copyPixelsTo(bboxSrc, buffer, new BoundingBox(new Point3i(0,0,z),e) );
		}
		
		return new BoundedVoxelBox<>(bboxNew,buffer);
	}
	
	// Considers growing in the negative direction from crnr by neg increments
	//  returns the maximum number of increments that are allowed without leading
	//  to a bounding box that is <0
	private static int clipNeg( int crnr, int neg ) {
		int diff = crnr-neg;
		if (diff>0) {
			return neg;
		} else {
			return neg + diff;
		}
	}
	
	// Considers growing in the positive direction from crnr by neg increments
	//  returns the maximum number of increments that are allowed without leading
	//  to a bounding box that is >= max
	private static int clipPos( int crnr, int pos, int max ) {
		int sum = crnr+pos;
		if (sum<max) {
			return pos;
		} else {
			return pos - (sum-max+1);
		}
	}

	
	/**
	 * Grow bounding-box by 1 pixel in all directions
	 *  
	 * @param do3D 3-dimensions (true) or 2-dimensions (false)
	 * @param clipRegion a region to clip to, which we can't grow beyond
	 * @return a bounding box: the crnr is the relative-position to the current bounding box, the extnt is absolute
	 */
	public BoundingBox dilate( boolean do3D, Optional<Extent> clipRegion ) {
		Point3i allOnes = do3D ? allOnes3D : allOnes2D;
		return createGrownBoxAbsolute(allOnes,allOnes, clipRegion);
	}
	
	
	/**
	 * Creates a grown bounding-box relative to this current box (absolute coordinates)
	 * 
	 * @param neg how much to grow in the negative direction
	 * @param pos how much to grow in the negative direction
	 * @param clipRegion a region to clip to, which we can't grow beyond
	 * @return a bounding box: the crnr is the relative-position to the current bounding box, the extnt is absolute
	 */
	private BoundingBox createGrownBoxAbsolute( Point3i neg, Point3i pos, Optional<Extent> clipRegion ) {
		BoundingBox relBox = createGrownBoxRelative(neg, pos, clipRegion);
		return relBox.reflectThroughOrigin().shiftBy( boundingBox.getCrnrMin() );
	}

	
	
	/**
	 * Creates a grown bounding-box relative to this current box (relative coordinates)
	 * 
	 * @param neg how much to grow in the negative direction
	 * @param pos how much to grow in the negative direction
	 * @param a region to clip to, which we can't grow beyond
	 * @return a bounding box: the crnr is the relative-position to the current bounding box (multipled by -1), the extnt is absolute
	 */
	private BoundingBox createGrownBoxRelative( Point3i neg, Point3i pos, Optional<Extent> clipRegion ) {
		
		Point3i negClip = new Point3i(neg);
		negClip.setX( clipNeg(boundingBox.getCrnrMin().getX(), neg.getX()));
		negClip.setY( clipNeg(boundingBox.getCrnrMin().getY(), neg.getY()));
		negClip.setZ( clipNeg(boundingBox.getCrnrMin().getZ(), neg.getZ()));
		
		ReadableTuple3i bboxMax = boundingBox.calcCrnrMax();
		
		int maxPossibleX;
		int maxPossibleY;
		int maxPossibleZ;
		if (clipRegion.isPresent()) {
			maxPossibleX = clipRegion.get().getX();
			maxPossibleY = clipRegion.get().getY();
			maxPossibleZ = clipRegion.get().getZ();
		} else {
			maxPossibleX = Integer.MAX_VALUE;
			maxPossibleY = Integer.MAX_VALUE;
			maxPossibleZ = Integer.MAX_VALUE;
		}
		
		Point3i posClip = new Point3i(pos);
		posClip.setX( clipPos(bboxMax.getX(), pos.getX(), maxPossibleX));
		posClip.setY( clipPos(bboxMax.getY(), pos.getY(), maxPossibleY));
		posClip.setZ( clipPos(bboxMax.getZ(), pos.getZ(), maxPossibleZ));
		
		
		// We calculate new sizes
		Extent e = this.voxelBox.extent();
		
		Extent eNew = new Extent(
			e.getX() + negClip.getX() + posClip.getX(),
			e.getY() + negClip.getY() + posClip.getY(),
			e.getZ() + negClip.getZ() + posClip.getZ()
		);
		return new BoundingBox(negClip, eNew);
	}
	
	
	/**
	 * Creates a new copy of the object mask with the buffer grown in pos and neg directions by a certain amount
	 * 
	 * @param neg
	 * @param pos
	 * @param clipRegion if defined, clips the buffer to this region
	 * @param factory
	 * @return
	 */
	public BoundedVoxelBox<T> growBuffer( Point3i neg, Point3i pos, Optional<Extent> clipRegion, VoxelBoxFactoryTypeBound<T> factory ) throws OperationFailedException {
		
		if(clipRegion.isPresent() && !clipRegion.get().contains(this.boundingBox) ) {
			throw new OperationFailedException("Cannot grow the bounding-box of the object-mask, as it is already outside the clipping region.");
		}
		
		Extent e = this.voxelBox.extent();
				
		BoundingBox grownBox = createGrownBoxRelative( neg, pos, clipRegion );
				
		// We allocate a new buffer
		VoxelBox<T> bufferNew = factory.create( grownBox.extent() );
		this.voxelBox.copyPixelsTo(new BoundingBox(e), bufferNew, new BoundingBox(grownBox.getCrnrMin(),e)  );
		
		// We create a new bounding box
		Point3i crnrMinNew = new Point3i( this.boundingBox.getCrnrMin() );
		crnrMinNew.sub( grownBox.getCrnrMin() );
		
		BoundingBox bbo = new BoundingBox(
			crnrMinNew,
			grownBox.extent()
		);
		
		return new BoundedVoxelBox<>( bbo, bufferNew );
	}

	/**
	 * Creates a scaled-version (in XY dimensions only) of the current bounding-box
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param scaleFactor what to scale X and Y dimensions by?
	 * @param interpolator means of interpolating between pixels
	 * @return a new bounded-voxels box of specified size containing scaled contents of the existing
	 */
	public BoundedVoxelBox<T> scale( ScaleFactor scaleFactor, Interpolator interpolator ) {
		
		VoxelBox<T> voxelBoxOut = voxelBox.resizeXY(
			ScaleFactorUtilities.scaleQuantity(scaleFactor.getX(), boundingBox.extent().getX()),
			ScaleFactorUtilities.scaleQuantity(scaleFactor.getY(), boundingBox.extent().getY()),
			interpolator
		);
		
		return new BoundedVoxelBox<T>(
			boundingBox.scale(scaleFactor, voxelBoxOut.extent()),
			voxelBoxOut
		);
	}
	
	public boolean sizesMatch() {
		boolean xCrct = ((this.getBoundingBox().extent().getX())==getVoxelBox().extent().getX());
		boolean yCrct = ((this.getBoundingBox().extent().getY())==getVoxelBox().extent().getY());
		boolean zCrct = ((this.getBoundingBox().extent().getZ())==getVoxelBox().extent().getZ());
		return xCrct && yCrct && zCrct;
	}
	
	public BoundedVoxelBox<T> createMaxIntensityProjection() {
		BoundingBox bboxNew = boundingBox.flattenZ();
		return new BoundedVoxelBox<>(bboxNew,voxelBox.maxIntensityProj());
	}
	
	
	public void convertToMaxIntensityProjection() {
		boundingBox = boundingBox.flattenZ();
		voxelBox = voxelBox.maxIntensityProj();
	}
	
	public BoundedVoxelBox<T> duplicate() {
		BoundedVoxelBox<T> newMask = new BoundedVoxelBox<>(this);
		assert(newMask.sizesMatch());
		return newMask;
	}
	
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	public VoxelBox<T> getVoxelBox() {
		return voxelBox;
	}

	public T getPixelsForPlane(int z) {
		return voxelBox.getPixelsForPlane(z).buffer();
	}

	public Extent extnt() {
		return voxelBox.extent();
	}
	
	public static class SubrangePixelAccess<BufferType extends Buffer> implements IPixelsForPlane<BufferType> {

		private int zRel;
		private Extent extnt;
		private BoundedVoxelBox<BufferType> src;
		
		public SubrangePixelAccess(int zRel, Extent extnt,
				BoundedVoxelBox<BufferType> src) {
			super();
			this.zRel = zRel;
			this.extnt = extnt;
			this.src = src;
		}

		@Override
		public void setPixelsForPlane(int z, VoxelBuffer<BufferType> pixels) {
			src.getVoxelBox().setPixelsForPlane(z+zRel, pixels);
		}

		@Override
		public VoxelBuffer<BufferType> getPixelsForPlane(int z) {
			return src.getVoxelBox().getPixelsForPlane(z+zRel);
		}

		@Override
		public Extent extent() {
			return extnt;
		}
	};
	
	
	// Creates an ObjMask with a subrange of the slices. zMin inclusive, zMax inclusive
	// Note, no new voxels are created
	public BoundedVoxelBox<T> createVirtualSubrange(int zMin, int zMax, VoxelBoxFactoryTypeBound<T> factory) throws CreateException {
	
		if( !boundingBox.contains().z(zMin)) {
			throw new CreateException("zMin outside range");
		}
		if( !boundingBox.contains().z(zMax)) {
			throw new CreateException("zMax outside range");
		}
		
		int relZ = zMin - boundingBox.getCrnrMin().getZ();
		
		BoundingBox target = new BoundingBox(
			boundingBox.getCrnrMin().duplicateChangeZ(zMin),
			boundingBox.extent().duplicateChangeZ(zMax-zMin+1)
		);
		
		SubrangePixelAccess<T> pixelAccess = new SubrangePixelAccess<T>(relZ,target.extent(),this);
		return new BoundedVoxelBox<T>( target, factory.create(pixelAccess) );
	}

	public BoundedVoxelBox<T> createBufferAvoidNew(BoundingBox bbox) throws CreateException {
		
		if (!boundingBox.contains().box(bbox)) {
			throw new CreateException("Source box does not contain target box");
		}
		
		BoundingBox target = bbox.relPosToBox(boundingBox);
		return new BoundedVoxelBox<>(
			bbox,
			voxelBox.createBufferAvoidNew(target)
		);
	}

	
	/**
	 * It will create a buffer that is contained within the current object
	 *   
	 * @param bounding-box in absolute co-ordinates
	 * @return
	 * @throws CreateException
	 */
	public BoundedVoxelBox<T> createBufferAlwaysNew(BoundingBox bbox) throws CreateException {

		if (!boundingBox.contains().box(bbox)) {
			throw new CreateException("Source box does not contain target box");
		}
		
		BoundingBox target = bbox.relPosToBox(boundingBox);
		return new BoundedVoxelBox<>(
			bbox,
			voxelBox.createBufferAlwaysNew(target)
		);
	}
	

	/**
	 * More relaxed prior-condition than createBufferAlwaysNew. It will create a buffer
	 *   within a new bounding-box, so long as part of the bounding-box
	 *   intersects with the current mask
	 *   
	 * @param bbox bounding-box in absolute co-ordinates
	 * @return
	 * @throws CreateException
	 */
	public BoundedVoxelBox<T> createIntersectingBufferAlwaysNew(BoundingBox bbox)
			throws CreateException {
		
		Optional<BoundingBox> bboxIntersect = boundingBox.intersection().with(bbox);
		if (!bboxIntersect.isPresent()) {
			throw new CreateException("Source box does not intersect with target box");
		}
		
		// Find the difference between the current bounding box and our new one
		Point3i relPosToSrc = bboxIntersect.get().relPosTo( this.boundingBox );
		Point3i relPosToDest = bboxIntersect.get().relPosTo( bbox );
		
		BoundingBox bboxRelSrc = new BoundingBox( relPosToSrc, bboxIntersect.get().extent() );
		BoundingBox bboxRelDest = new BoundingBox( relPosToDest, bboxIntersect.get().extent() );
		
		VoxelBox<T> bufNew = voxelBox.getFactory().create( bbox.extent() );
		voxelBox.copyPixelsTo( bboxRelSrc, bufNew, bboxRelDest);

		return new BoundedVoxelBox<>(
			bbox,
			bufNew
		);
	}
	
	public void shiftBy(ReadableTuple3i shiftBy) {
		this.boundingBox = boundingBox.shiftBy(shiftBy);
	}
	
	public void shiftBackBy(ReadableTuple3i shiftBackwardsBy) {
		this.boundingBox = boundingBox.shiftBackBy(shiftBackwardsBy);
	}
	
	public void shiftTo(Point3i crnrMinNew) {
		this.boundingBox = boundingBox.shiftTo(crnrMinNew);
	}

	public void shiftToZ(int crnrZNew) {
		this.boundingBox = boundingBox.shiftToZ(crnrZNew);
	}
	
	public void reflectThroughOrigin() {
		this.boundingBox = boundingBox.reflectThroughOrigin();
	}

	// If keepZ is true the slice keeps its z coordinate, otherwise its set to 0
	public BoundedVoxelBox<T> extractSlice(int z, boolean keepZ) {
		
		BoundingBox bboxNew = boundingBox.flattenZ();
		
		if (keepZ) {
			bboxNew = bboxNew.shiftToZ(z);
		}
		
		return new BoundedVoxelBox<>(
			bboxNew,
			voxelBox.extractSlice(z)
		);
	}

	public void setPixelsCheckMask(ObjMask om, int value) {
		voxelBox.setPixelsCheckMask(om, value);
	}

	public void setPixelsCheckMask(BoundingBox bboxToBeAssigned,
			VoxelBox<ByteBuffer> objMaskBuffer, BoundingBox bboxMask, int value,
			byte maskMatchValue) {
		voxelBox.setPixelsCheckMask(bboxToBeAssigned, objMaskBuffer,
				bboxMask, value, maskMatchValue);
	}
}