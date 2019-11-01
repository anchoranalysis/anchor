package org.anchoranalysis.anchor.mpp.mark.conic;

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

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkAbstractPosition;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;

import ch.ethz.biol.cell.imageprocessing.bound.Bound;
import ch.ethz.biol.cell.mpp.mark.GlobalRegionIdentifiers;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipUtilities;



// A spherical mark
public class MarkSphere extends MarkAbstractPosition implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3526056946146656810L;
	
	private static byte FLAG_SUBMARK_NONE = RegionMembershipUtilities.flagForNoRegion();
	private static byte FLAG_SUBMARK_INSIDE = RegionMembershipUtilities.flagForRegion( GlobalRegionIdentifiers.SUBMARK_INSIDE );
	private static byte FLAG_SUBMARK_SHELL = RegionMembershipUtilities.flagForRegion( GlobalRegionIdentifiers.SUBMARK_SHELL );
	
	// START mark state
	private double radius;
	// END mark state
	
    private double radiusSq;
    private double radiusExtraSq;
    
    private Bound bndRadius = null;
    
    private static double SphereExtraRad = 2;
    
    // Sphere with default properties
    public MarkSphere() {
    }
    
    // Constructor
    public MarkSphere( Bound bnd_radius ) {
    	super();
    	this.bndRadius = bnd_radius;
    }
    
    // Copy Constructor - we do not copy scene
    public MarkSphere( MarkSphere src ) {
    	super( src );
    	this.bndRadius = src.bndRadius;
    	this.radius = src.radius;
    	this.radiusSq = src.radiusSq;
    	this.radiusExtraSq = src.radiusExtraSq;
    }
	
	@Override
	public String getName() {
		return new String("sphere");
	}
    
    @Override
	public double volume( int regionID ) {
    	return (4 * Math.PI * Math.pow( this.radius, 3.0) ) /3;
    }
    
    @Override
	public String toString() {
    	return String.format("%s %s pos=%s %s", "Sphr", strId(), strPos(), strMarks() );
    }
    
    public String strMarks() {
    	return String.format("rad=%8.3f", this.radius );
    }
 
	@Override
	public BoundingBox bbox( ImageDim bndScene, int regionID ) {
		double exRad = regionID==GlobalRegionIdentifiers.SUBMARK_SHELL ? SphereExtraRad : 0;
		return BoundsCalculator.bboxFromBounds( getPos(), radius + exRad + 0.5, true, bndScene);
	}
	
	
	// The duplicate operation for the marks (we avoid clone() in case its confusing, we might not always shallow copy)
	@Override
	public Mark duplicate() {
		return new MarkSphere( this );
	}
	

	// Does a quick test to see if we can reject the possibility
	// of overlap
	//   true -> no overlap
	//   false -> maybe overlap, maybe not
	@Override
	public boolean quickTestNoOverlap( Mark m, int regionID ) {
		return overlapWithQuick(m, regionID)==0;
	}
	
	
	// Radius bounds
	public Bound getRadiusBound() {
		return bndRadius;
	}

	// Set radius bounds
	public void setRadiusBound(Bound bndRadius) {
		this.bndRadius = bndRadius;
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
	
	// NB objects are scaled in pre-rotated position i.e. when aligned to axes
	// We actually scale in all 3 dimensions
	@Override
	public void scale( double mult_factor ) {
	//	throw new MPPException("scaleXY operation not supported");
		super.scale(mult_factor);
		
		if (this.bndRadius!=null) {
			this.bndRadius = this.bndRadius.duplicate();
			this.bndRadius.scale( mult_factor );
		}
		
		setRadius( this.radius * mult_factor );
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
		
    	this.radiusSq = Math.pow( this.radius, 2.0);
    	this.radiusExtraSq = Math.pow( this.radius + MarkSphere.SphereExtraRad, 2.0);
	}
	
	@Override
	public boolean equalsDeep(Mark m) {
		
		if (!super.equalsDeep(m)) {
			return false;
		}
		
		if (!(m instanceof MarkSphere)) {
			return false;
		}
		
		MarkSphere trgt = (MarkSphere) m;
		
		if (radius!=trgt.radius) {
			return false;
		}
		
		return true;
	}

	@Override
	public int numDims() {
		return 3;
	}

	public double getRadius() {
		return radius;
	}
	
	@Override
	public int numRegions() {
		return 2;
	}

	@Override
	public BoundingBox bboxAllRegions(ImageDim bndScene) {
		return bbox(bndScene, GlobalRegionIdentifiers.SUBMARK_SHELL);
	}
}
