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
import java.util.Optional;
import java.util.function.Function;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;

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
	
	/**
	 * Replaces the voxels in the box.
	 * 
	 * <p>This is an IMMUTABLE operation, and a new voxel-box is created.</p>
	 * 
	 * @param voxelBoxToAssign voxels to be assigned.
	 * @return a new voxel-box with the replacement voxels but identical bounding-box
	 */
	public BoundedVoxelBox<T> replaceVoxels(VoxelBox<T> voxelBoxToAssign) {
		assert( voxelBoxToAssign.extent().equals( extent() ));
		return new BoundedVoxelBox<>(boundingBox, voxelBoxToAssign);
	}
	
	public BoundedVoxelBox<T> flattenZ() {
		BoundingBox bboxNew = this.boundingBox.flattenZ();
		return new BoundedVoxelBox<>( bboxNew, voxelBox.maxIntensityProj() );
	}
	
	public BoundedVoxelBox<T> growToZ( int sz, VoxelBoxFactoryTypeBound<T> factory ) {
		assert(this.boundingBox.extent().getZ()==1);
		assert(this.voxelBox.extent().getZ()==1);
		
		BoundingBox bboxNew = new BoundingBox(
			boundingBox.getCornerMin(),
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
	 * @return a bounding box: the crnr is the relative-position to the current bounding box, the extent is absolute
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
	 * @return a bounding box: the crnr is the relative-position to the current bounding box, the extent is absolute
	 */
	private BoundingBox createGrownBoxAbsolute( Point3i neg, Point3i pos, Optional<Extent> clipRegion ) {
		BoundingBox relBox = createGrownBoxRelative(neg, pos, clipRegion);
		return relBox.reflectThroughOrigin().shiftBy( boundingBox.getCornerMin() );
	}

	
	
	/**
	 * Creates a grown bounding-box relative to this current box (relative coordinates)
	 * 
	 * @param neg how much to grow in the negative direction
	 * @param pos how much to grow in the negative direction
	 * @param a region to clip to, which we can't grow beyond
	 * @return a bounding box: the crnr is the relative-position to the current bounding box (multipled by -1), the extent is absolute
	 */
	private BoundingBox createGrownBoxRelative( Point3i neg, Point3i pos, Optional<Extent> clipRegion ) {
		
		Point3i negClip = new Point3i(
			clipNeg(boundingBox.getCornerMin().getX(), neg.getX()),
			clipNeg(boundingBox.getCornerMin().getY(), neg.getY()),
			clipNeg(boundingBox.getCornerMin().getZ(), neg.getZ())
		);
		
		ReadableTuple3i bboxMax = boundingBox.calcCornerMax();
		
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
		
		Point3i growBy = new Point3i(
			clipPos(bboxMax.getX(), pos.getX(), maxPossibleX) + negClip.getX(),
			clipPos(bboxMax.getY(), pos.getY(), maxPossibleY) + negClip.getY(),
			clipPos(bboxMax.getZ(), pos.getZ(), maxPossibleZ) + negClip.getZ()
		);
		return new BoundingBox(
			negClip,
			this.voxelBox.extent().growBy(growBy)
		);
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
		this.voxelBox.copyPixelsTo(new BoundingBox(e), bufferNew, new BoundingBox(grownBox.getCornerMin(),e)  );
		
		// We create a new bounding box
		BoundingBox bbo = new BoundingBox(
			Point3i.immutableSubtract( this.boundingBox.getCornerMin(), grownBox.getCornerMin() ),
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
	
	private boolean sizesMatch() {
		boolean xCrct = ((this.getBoundingBox().extent().getX())==getVoxelBox().extent().getX());
		boolean yCrct = ((this.getBoundingBox().extent().getY())==getVoxelBox().extent().getY());
		boolean zCrct = ((this.getBoundingBox().extent().getZ())==getVoxelBox().extent().getZ());
		return xCrct && yCrct && zCrct;
	}
	
	public BoundedVoxelBox<T> createMaxIntensityProjection() {
		BoundingBox bboxNew = boundingBox.flattenZ();
		return new BoundedVoxelBox<>(bboxNew,voxelBox.maxIntensityProj());
	}
	
	/**
	 * A maximum-intensity projection (flattens in z dimension)
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @return a new bounded-voxel-box flattened in Z dimension.
	 */
	public BoundedVoxelBox<T> maxIntensityProjection() {
		return new BoundedVoxelBox<>(
			boundingBox.flattenZ(),
			voxelBox.maxIntensityProj()
		);
	}
	
	public BoundedVoxelBox<T> duplicate() {
		BoundedVoxelBox<T> newMask = new BoundedVoxelBox<>(this);
		assert(newMask.sizesMatch());
		return newMask;
	}
	
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public VoxelBox<T> getVoxelBox() {
		return voxelBox;
	}

	public T getPixelsForPlane(int z) {
		return voxelBox.getPixelsForPlane(z).buffer();
	}

	public Extent extent() {
		return voxelBox.extent();
	}
	
	
	/**
	 * Creates an box with a subrange of the slices.
	 * 
	 * <p>This will always reuse the existing voxel-buffers.</p.
	 * 
	 * @param zMin minimum z-slice index, inclusive.
	 * @param zMax maximum z-slice index, inclusive.
	 * @param factory factory to use to create new voxels.
	 * @return a newly created box for the slice-range requested.
	 * @throws CreateException
	 */
	public BoundedVoxelBox<T> regionZ(int zMin, int zMax, VoxelBoxFactoryTypeBound<T> factory) throws CreateException {
	
		if( !boundingBox.contains().z(zMin)) {
			throw new CreateException("zMin outside range");
		}
		if( !boundingBox.contains().z(zMax)) {
			throw new CreateException("zMax outside range");
		}
		
		int relZ = zMin - boundingBox.getCornerMin().getZ();
		
		BoundingBox target = new BoundingBox(
			boundingBox.getCornerMin().duplicateChangeZ(zMin),
			boundingBox.extent().duplicateChangeZ(zMax-zMin+1)
		);
		
		SubrangeVoxelAccess<T> voxelAccess = new SubrangeVoxelAccess<T>(relZ,target.extent(),this);
		return new BoundedVoxelBox<T>( target, factory.create(voxelAccess) );
	}
	
	/**
	 * A (sub-)region of the voxels.
	 * 
	 * <p>The region may some smaller portion of the voxel-box, or the voxel-box as a whole.</p>
	 * 
	 * <p>It should <b>never</b> be larger than the voxel-box.</p>
	 * 
	 * <p>See {@link org.anchoranalysis.image.voxel.box.VoxelBox::region) for more details.</p>
	 *   
	 * @param bounding-box in absolute coordinates.
	 * @param reuseIfPossible if TRUE the existing box will be reused if possible, otherwise a new box is always created.
	 * @return a bounded voxel-box corresponding to the requested region, either newly-created or reused
	 * @throws CreateException
	 */
	public BoundedVoxelBox<T> region(BoundingBox bbox, boolean reuseIfPossible) throws CreateException {

		if (!boundingBox.contains().box(bbox)) {
			throw new CreateException("Source box does not contain target box");
		}
		
		BoundingBox target = bbox.relPosToBox(boundingBox);
		return new BoundedVoxelBox<>(
			bbox,
			voxelBox.region(target,reuseIfPossible)
		);
	}
	

	/**
	 * Like {@link region} but only expects a bounding-box that intersects at least partially.
	 * 
	 * <p>This is a weakened condition compared to {@link region}</p>.
	 * 
	 * <p>The region outputted will have the same size and coordinates as the bounding-box, but
	 * with the correct voxel-values for the part within the voxel-box. Any other voxels are set to {@code voxelValueForRest}.</p>
	 * 
	 * <p>A new voxel-buffer is always created for this operation i.e. the existing box is never reused like sometimes in {@link region}.</p.
	 *   
	 * @param bbox bounding-box in absolute co-ordinates, that must at least partially intersect with the current bounds.
	 * @param voxelValueForRest a voxel-value for the parts of the buffer not covered by the intersection.
	 * @return a newly created voxel-box containing partially some parts of the existing voxels and other regions.
	 * @throws CreateException if the boxes do not intersect
	 */
	public BoundedVoxelBox<T> regionIntersecting(BoundingBox bbox, int voxelValueForRest) throws CreateException {
		
		Optional<BoundingBox> bboxIntersect = boundingBox.intersection().with(bbox);
		if (!bboxIntersect.isPresent()) {
			throw new CreateException("Requested bounding-box does not intersect with current bounds");
		}
		
		VoxelBox<T> bufNew = voxelBox.getFactory().create( bbox.extent() );
		
		// We can rely on the newly created voxels being 0 by default, otherwise we must update.
		if (voxelValueForRest!=0) {
			voxelBox.setAllPixelsTo(voxelValueForRest);
		}
		
		voxelBox.copyPixelsTo(
			bboxIntersect.get().relPosToBox(this.boundingBox),
			bufNew,
			bboxIntersect.get().relPosToBox(bbox)
		);

		return new BoundedVoxelBox<>(bbox, bufNew);
	}
	
	/**
	 * Applies a function to map the bounding-box to a new-value
	 * 
	 * <p>This is an IMMUTABLE operation, but the existing voxel-buffers are reused in the new object.</p>
	 * 
	 * @return a new object-mask with the updated bounding box
	 */
	public BoundedVoxelBox<T> mapBoundingBox( Function<BoundingBox,BoundingBox> mapFunc ) {
		return new BoundedVoxelBox<>(
			mapFunc.apply(boundingBox),
			voxelBox
		);
	}
		
	/**
	 * Extracts a particular slice.
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param z which slice to extract
	 * @param keepZ if true the slice keeps its z coordinate, otherwise its set to 0
	 * @return the extracted-slice (bounded)
	 */
	public BoundedVoxelBox<T> extractSlice(int z, boolean keepZ) {
		
		BoundingBox bboxFlattened = boundingBox.flattenZ();
				
		return new BoundedVoxelBox<>(
			keepZ ? bboxFlattened.shiftToZ(z) : bboxFlattened,
			voxelBox.extractSlice(z)
		);
	}
}