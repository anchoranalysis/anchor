package org.anchoranalysis.anchor.mpp.mark.conic;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import static org.anchoranalysis.anchor.mpp.mark.conic.TensorUtilities.squared;

import java.io.Serializable;

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.bean.bound.BoundCalculator;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkAbstractPosition;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;

/** Base-class for a conic that has a single radius (circle, sphere etc.) */
public abstract class MarkSingleRadius extends MarkAbstractPosition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
	private static byte FLAG_SUBMARK_INSIDE = RegionMembershipUtilities.flagForRegion( GlobalRegionIdentifiers.SUBMARK_INSIDE );
	private static byte FLAG_SUBMARK_SHELL = RegionMembershipUtilities.flagForRegion( GlobalRegionIdentifiers.SUBMARK_SHELL );
		
	// START mark state
	private double radius;
	// END mark state
	
    private double radiusSq;
    private double radiusExtraSq;
    
    private static double SphereExtraRad = 2;
    
	private Bound boundRadius;
	
	/**
	 * Default constructor
	 */
    protected MarkSingleRadius() {
    	// NOTHING TO DO    	
    }
    

    /**
     * Constructor with a bound on the radius
     * @param bonudRadius
     */
    protected MarkSingleRadius( Bound boundRadius ) {
    	super();
    	this.boundRadius = boundRadius;
    }
    
    /**
     * Copy constructor
     * 
     * @param src
     */
    protected MarkSingleRadius( MarkSingleRadius src ) {
    	super( src );
    	this.boundRadius = src.boundRadius;
    	this.radius = src.radius;
    	this.radiusSq = src.radiusSq;
    	this.radiusExtraSq = src.radiusExtraSq;
    }

	/**
	 * Objects are scaled in pre-rotated position.
	 * 
	 * <p>So when aligned to axes, we actually scale in all 3 dimensions, and ignore scene-resolution</p>
	 * 
	 */
	@Override
	public void scale( double mult_factor )  {
		super.scale(mult_factor);
		
		if (this.boundRadius!=null) {
			this.boundRadius = this.boundRadius.duplicate();
			this.boundRadius.scale( mult_factor );
		}
		
		setRadius( this.radius * mult_factor );
	}
	
    public boolean randomizeMarks( RandomNumberGenerator re, ImageResolution sr, BoundCalculator boundGenerator ) {
    	setRadius( this.boundRadius.rslv(sr, false).randOpen( re ) );
    	return true;
    }
    
	@Override
	public BoundingBox bbox( ImageDimensions bndScene, int regionID ) {

		// TODO should we have the extra 0.5 here?
		return BoundingBoxCalculator.bboxFromBounds(
			getPos(),
			radiusForRegion(regionID) + 0.5,
			numDims()==3,
			bndScene
		);
	}

	@Override
	public BoundingBox bboxAllRegions(ImageDimensions bndScene) {
		return bbox(bndScene, GlobalRegionIdentifiers.SUBMARK_SHELL);
	}
		
	// Where is a point in relation to the current object
	@Override
	public final byte evalPntInside( Point3d pt ) {
		
		double dist = getPos().distanceSquared(pt);
		
		if (dist<=radiusSq) {
			return FLAG_SUBMARK_INSIDE;
		} else if (dist<=(radiusExtraSq)) {
			return FLAG_SUBMARK_SHELL;
		}
		
		return FLAG_SUBMARK_NONE;
	}
	
	@Override
	public  boolean hasOverlapWithQuick( ) {
		return true;
	}

	@Override
	public boolean quickTestNoOverlap( Mark mark, int regionID ) {
		return overlapWithQuick(mark, regionID)==0;
	}

	/**  Note that this does not return the exact number of voxels of overlap, but some sort of approximation based upon the volume ratios */
	@Override
	public double overlapWithQuick( Mark mark, int regionID ) {

		if (getClass().equals(mark.getClass())) {
			MarkSingleRadius target = (MarkSingleRadius) mark;
			return this.radius + target.radius - this.getPos().distance(target.getPos());
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public int numRegions() {
		return 2;
	}

	@Override
	public boolean equalsDeep(Mark mark) {
		
		if (!super.equalsDeep(mark)) {
			return false;
		}
		
		if (!mark.getClass().equals(getClass())) {
			return false;
		}
		
		MarkSingleRadius trgt = (MarkSingleRadius) mark;
		
		return radius==trgt.radius;
	}
	    
    public String strMarks() {
    	return String.format("rad=%8.3f", this.radius );
    }
    
	public void setRadius(double radius) {
		this.radius = radius;
		
    	this.radiusSq = squared(radius);
    	this.radiusExtraSq = squared(radius + SphereExtraRad);
	}
	
	public double getRadius() {
		return radius;
	}
	
	protected double radiusForRegion( int regionID ) {
		return regionID==GlobalRegionIdentifiers.SUBMARK_INSIDE ? radius : radius + SphereExtraRad; 
	}
	
	protected double radiusForRegionSquared( int regionID ) {
		return regionID==GlobalRegionIdentifiers.SUBMARK_INSIDE ? radiusSq : radiusExtraSq; 
	}
	
	public Bound getBoundRadius() {
		return boundRadius;
	}
	
	public void setBoundRadius(Bound boundRadius) {
		this.boundRadius = boundRadius;
	}

}
