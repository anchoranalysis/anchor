package org.anchoranalysis.anchor.mpp.mark;

/*
 * #%L
 * anchor-mpp
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


import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.function.Function;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.anchor.overlay.id.Identifiable;
import org.anchoranalysis.core.cache.IHasCacheableID;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.objectmask.properties.ObjectWithProperties;
import org.anchoranalysis.image.scale.ScaleFactor;

public abstract class Mark implements Serializable, IHasCacheableID, Identifiable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3272456193681334471L;

	// null == not cacheable
	// If set, it means that any other mark with the same cacheID has
	//  the exact same state, as far as feature calculation is concerned
	private Integer cacheID = null;
	
	// START mark state
	private int id = -1;
	// END mark state
	
	// It is permissible to mutate the point during calculation
	public abstract byte evalPntInside( Point3d pt );
	
	public abstract Mark duplicate();
	
	public abstract int numRegions();
	
	public abstract String getName();
	
	@Override
	public void assignCacheID( int id ) {
		this.cacheID = Integer.valueOf(id);
	}
	
	@Override
	public boolean hasCacheID() {
		return cacheID!=null;
	}
	
	public String getCacheIDAsString() {
		return cacheID!=null ? Integer.toString(cacheID) : "null";
	}
	
	@Override
	public void clearCacheID() {
		this.cacheID = null;
	}
	
	@Override
	public int getCacheID() {
		return cacheID;
	}
	
	
	public void assignFrom( Mark srcMark ) throws OptionalOperationUnsupportedException {
		// We deliberately don't copy the ID		
		if (srcMark.cacheID!=null) {
			this.cacheID = srcMark.cacheID;
		} else {
			this.cacheID = null;
		}
	}
		
	
	// We can define an alternative "quick" metric for overlap
	//  for a mark, which takes the place of the voxel by
	//  voxel bounding box comparison
	public boolean hasOverlapWithQuick( ) {
		return false;
	}
	public double overlapWithQuick( Mark m, int regionID ) {
		return 0.0;
	}
	
	/**
	 * A quick (computationally-efficient) test to see if we can reject the possibility of overlap
	 * 
	 * @param mark the other mark to assess overlap with
	 * @param regionID the region to check for overlap
	 * @return TRUE if there's definitely no overlap, FLASE if there is maybe overlap or not
	 */
	public boolean quickTestNoOverlap( Mark mark, int regionID ) {
		return false;
	}
	
	public abstract double volume( int regionID );
	
	// Constructor
	public Mark() {
		super();
	}

	public Mark( Mark src ) {
		// We do not deep copy
		this.id = src.id;
		if (src.cacheID!=null) {
			this.cacheID = src.cacheID;
		} else {
			src.cacheID=null;
		}
	}

	// String representation of mark
	@Override
	public abstract String toString();

	public abstract void scale( double mult_factor ) throws OptionalOperationUnsupportedException;

	public abstract int numDims();
	
	// center point
	public abstract Point3d centerPoint();
	
	public abstract BoundingBox bbox( ImageDim bndScene, int regionID );
	
	public abstract BoundingBox bboxAllRegions( ImageDim bndScene );

	protected byte evalPntInside( Point3i pt ) {
		return this.evalPntInside(
			PointConverter.doubleFromInt(pt)
		); 
	}
	
	public boolean equalsID( Object obj ) {
		
		if (obj instanceof Mark) {
			Mark mark = (Mark) obj;
			return this.id==mark.id;	
		}
		
		return false;
	}
	
	// Checks if two marks are equal by comparing all attributes
	public boolean equalsDeep(Mark m) {
		
		// ID check
		if (!equalsID(m)) {
			return false;
		}
		return true;
	}
	
	// Calculates the mask of an object
	public ObjectWithProperties calcMask( ImageDim bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bv ) {
		
		BoundingBox bbox = this.bbox( bndScene, rm.getRegionID() );
		
		// We make a new mask and populate it from out iterator
		ObjectWithProperties mask = new ObjectWithProperties(bbox);

		assert( mask.getVoxelBox().extent().getZ() > 0 );
		
		byte maskOn = bv.getOnByte();
		
		ReadableTuple3i maxPos = bbox.calcCrnrMax();
		
		Point3i pnt = new Point3i();
		for (pnt.setZ(bbox.getCrnrMin().getZ()); pnt.getZ()<=maxPos.getZ(); pnt.incrementZ()) {
			
			int z_local = pnt.getZ() - bbox.getCrnrMin().getZ();
			ByteBuffer mask_slice = mask.getVoxelBox().getPixelsForPlane(z_local).buffer();

			int cnt = 0;
			for (pnt.setY(bbox.getCrnrMin().getY()); pnt.getY()<=maxPos.getY(); pnt.incrementY()) {
				for (pnt.setX(bbox.getCrnrMin().getX()); pnt.getX()<=maxPos.getX(); pnt.incrementX()) {
					
					byte membership = evalPntInside(pnt);
					
					if (rm.isMemberFlag(membership)) {
						mask_slice.put(cnt, maskOn);
					}
					cnt++;
					
				}
			}
		}
		
		assert( mask.getVoxelBox().extent().getZ() > 0 );
			
		return mask;
	}
	
	
	// Calculates the mask of an object
	public ObjectWithProperties calcMaskScaledXY( ImageDim bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut, double scaleFactor ) {
			
		 BoundingBox bbox = bbox( bndScene, rm.getRegionID() )
				 .scale( new ScaleFactor(scaleFactor) );
		
		// We make a new mask and populate it from out iterator
		ObjectWithProperties mask = new ObjectWithProperties(bbox);

		assert( mask.getVoxelBox().extent().getZ() > 0 );
		
		byte maskOn = bvOut.getOnByte();
		
		ReadableTuple3i maxPos = bbox.calcCrnrMax();
		
		Point3i pnt = new Point3i();
		Point3d pntScaled = new Point3d();
		for (pnt.setZ(bbox.getCrnrMin().getZ()); pnt.getZ()<=maxPos.getZ(); pnt.incrementZ()) {
			
			int z_local = pnt.getZ() - bbox.getCrnrMin().getZ();
			ByteBuffer mask_slice = mask.getVoxelBox().getPixelsForPlane(z_local).buffer();

			// Z co-ordinates are the same as we only scale in XY
			pntScaled.setZ( pnt.getZ() );
			
			int cnt = 0;
			for (pnt.setY(bbox.getCrnrMin().getY()); pnt.getY()<=maxPos.getY(); pnt.incrementY()) {
				for (pnt.setX(bbox.getCrnrMin().getX()); pnt.getX()<=maxPos.getX(); pnt.incrementX()) {
					
					pntScaled.setX( ((double) pnt.getX()) / scaleFactor );
					pntScaled.setY( ((double) pnt.getY()) / scaleFactor );
					
					byte membership = evalPntInside(pntScaled);
					
					if (rm.isMemberFlag(membership)) {
						mask_slice.put(cnt, maskOn);
					}
					cnt++;
					
				}
			}
		}
		
		//assert( mask.getMask().hasPixelsGreaterThan(0) );
		assert( mask.getVoxelBox().extent().getZ() > 0 );
			
		return mask;
	}
	
	public String strId() {
		return String.format("id=%10d", id);
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public OverlayProperties generateProperties(ImageRes res) {
		
		OverlayProperties nvc = new OverlayProperties();
		nvc.add("Type", getName() );
		nvc.add(
			"ID",
			Integer.toString( getId() )
		);
		nvc.add(
			"CacheID",
			cacheID!=null ? Integer.toString( cacheID ) : "null"
		);
		
		if (res==null) {
			return nvc;
		}

		addPropertiesForRegions( nvc, res );
		return nvc;
	}

	private void addPropertiesForRegions( OverlayProperties nvc, ImageRes res ) {
		for( int r=0; r<numRegions(); r++) {
			double vol = volume(r);
			
			String name = numDims()==3 ? "Volume" : "Area";
			
			UnitSuffix unit = numDims()==3 ? 
				UnitSuffix.CUBIC_MICRO : UnitSuffix.SQUARE_MICRO;
			
			Function<Double,Double> conversionFunc = numDims()==3 ?
				res::convertVolume : res::convertArea;
				
			nvc.addWithUnits(
				String.format("%s [geom] %d",name, r),
				vol,
				conversionFunc,
				unit
			);
		}
	}
}
