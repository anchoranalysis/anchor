package ch.ethz.biol.cell.mpp.mark;

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
import org.anchoranalysis.core.cache.IHasCacheableID;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.core.name.value.NameValue;
import org.anchoranalysis.core.unit.SpatialConversionUtilities;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;

import ch.ethz.biol.cell.core.IHasIdentifier;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

public abstract class Mark implements Serializable, IHasCacheableID, IHasIdentifier {

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

	
	// Does a quick test to see if we can reject the possibility
	// of overlap
	//   true -> no overlap
	//   false -> maybe overlap, maybe not
	public boolean quickTestNoOverlap( Mark m, int regionID ) {
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
	public ObjMaskWithProperties calcMask( ImageDim bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bv ) {
		
		BoundingBox bbox = this.bbox( bndScene, rm.getRegionID() );
		
		// We make a new mask and populate it from out iterator
		ObjMaskWithProperties mask = new ObjMaskWithProperties(bbox);

		assert( mask.getVoxelBox().extnt().getZ() > 0 );
		
		byte maskOn = bv.getOnByte();
		
		Point3i maxPos = bbox.calcCrnrMax();
		
		Point3i pnt = new Point3i();
		for (pnt.setZ(bbox.getCrnrMin().getZ()); pnt.getZ()<=maxPos.getZ(); pnt.incrZ()) {
			
			int z_local = pnt.getZ() - bbox.getCrnrMin().getZ();
			ByteBuffer mask_slice = mask.getVoxelBox().getPixelsForPlane(z_local).buffer();

			int cnt = 0;
			for (pnt.setY(bbox.getCrnrMin().getY()); pnt.getY()<=maxPos.getY(); pnt.incrY()) {
				for (pnt.setX(bbox.getCrnrMin().getX()); pnt.getX()<=maxPos.getX(); pnt.incrX()) {
					
					byte membership = evalPntInside(pnt);
					
					if (rm.isMemberFlag(membership)) {
						mask_slice.put(cnt, maskOn);
					}
					cnt++;
					
				}
			}
		}
		
		assert( mask.getVoxelBox().extnt().getZ() > 0 );
			
		return mask;
	}
	
	
	// Calculates the mask of an object
	public ObjMaskWithProperties calcMaskScaledXY( ImageDim bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut, double scaleFactor ) {
			
		BoundingBox bbox = this.bbox( bndScene, rm.getRegionID() );
		bbox.scaleXYPosAndExtnt(scaleFactor, scaleFactor);
		
		// We make a new mask and populate it from out iterator
		ObjMaskWithProperties mask = new ObjMaskWithProperties(bbox);

		assert( mask.getVoxelBox().extnt().getZ() > 0 );
		
		byte maskOn = bvOut.getOnByte();
		
		Point3i maxPos = bbox.calcCrnrMax();
		
		Point3i pnt = new Point3i();
		Point3d pntScaled = new Point3d();
		for (pnt.setZ(bbox.getCrnrMin().getZ()); pnt.getZ()<=maxPos.getZ(); pnt.incrZ()) {
			
			int z_local = pnt.getZ() - bbox.getCrnrMin().getZ();
			ByteBuffer mask_slice = mask.getVoxelBox().getPixelsForPlane(z_local).buffer();

			// Z co-ordinates are the same as we only scale in XY
			pntScaled.setZ( pnt.getZ() );
			
			int cnt = 0;
			for (pnt.setY(bbox.getCrnrMin().getY()); pnt.getY()<=maxPos.getY(); pnt.incrY()) {
				for (pnt.setX(bbox.getCrnrMin().getX()); pnt.getX()<=maxPos.getX(); pnt.incrX()) {
					
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
		assert( mask.getVoxelBox().extnt().getZ() > 0 );
			
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
	
	public NameValueSet<String> generateProperties(ImageRes sr) {
		
		NameValueSet<String> nvc = new NameValueSet<>();
		nvc.add( new NameValue<>("Type", getName() ));
		nvc.add( new NameValue<>("ID", Integer.toString( getId() ) ));
		nvc.add( new NameValue<>("CacheID", cacheID!=null ? Integer.toString( cacheID ) : "null" ));
		

		if (sr!=null) {
			for( int r=0; r<numRegions(); r++) {
				double vol = volume(r);
				if (numDims()==3) {
					SpatialConversionUtilities.UnitSuffix unitPrefix = SpatialConversionUtilities.UnitSuffix.CUBIC_MICRO;
					double volumeUnits = SpatialConversionUtilities.convertToUnits(sr.convertVolume(vol), unitPrefix);
					nvc.add( new NameValue<>(
						String.format("Volume [geom] %d",r),
						String.format("%2.2f (%.2f%s)", vol, volumeUnits, SpatialConversionUtilities.unitMeterStringDisplay(unitPrefix)) )
					);
				} else if (numDims()==2) {
					SpatialConversionUtilities.UnitSuffix unitPrefix = SpatialConversionUtilities.UnitSuffix.SQUARE_MICRO;
					double areaUnits = SpatialConversionUtilities.convertToUnits(sr.convertVolume(vol), unitPrefix);
					nvc.add( new NameValue<>(
						String.format("Area [geom] %d",r),
						String.format("%2.2f (%.2f%s)", vol, areaUnits, SpatialConversionUtilities.unitMeterStringDisplay(unitPrefix)) )
					);
				}
			}
		}
		return nvc;
	}
}
