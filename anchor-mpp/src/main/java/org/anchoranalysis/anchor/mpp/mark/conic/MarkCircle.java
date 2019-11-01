package org.anchoranalysis.anchor.mpp.mark.conic;

import static org.anchoranalysis.anchor.mpp.mark.conic.TensorUtilities.*;

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

import org.anchoranalysis.anchor.mpp.bean.bounds.BoundCalculator;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities;
import org.anchoranalysis.anchor.mpp.bounds.Bound;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkAbstractPosition;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;


// A spherical mark
public class MarkCircle extends MarkAbstractPosition implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 8551900716243748046L;
	
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
    public MarkCircle() {
    }
    
    // Constructor
    public MarkCircle( Bound bnd_radius ) {
    	super();
    	this.bndRadius = bnd_radius;
    }
    
    // Copy Constructor - we do not copy scene
    public MarkCircle( MarkCircle src ) {
    	super( src );
    	this.bndRadius = src.bndRadius;
    	this.radius = src.radius;
    	this.radiusSq = src.radiusSq;
    	this.radiusExtraSq = src.radiusExtraSq;
    }
    
	@Override
	public String getName() {
		return new String("circle");
	}

    public boolean randomizeMarks( RandomNumberGenerator re, ImageRes sr, BoundCalculator boundGenerator ) {
    	setRadius( this.bndRadius.rslv(sr, false).randOpen( re ) );
    	return true;
    }
    
    @Override
	public double volume( int regionID ) {
    	// TODO shellRad
    	return (2 * Math.PI * squared(this.radius) );
    }
    
    @Override
	public String toString() {
    	return String.format("%s %s pos=%s %s", "circle", strId(), strPos(), strMarks() );
    }
    
    public String strMarks() {
    	return String.format("rad=%8.3f", this.radius );
    }

	@Override
	public BoundingBox bbox( ImageDim bndScene, int regionID ) {
		
		double exRad = regionID==GlobalRegionIdentifiers.SUBMARK_SHELL ? SphereExtraRad : 0;
		return BoundsCalculator.bboxFromBounds( getPos(), radius + exRad + 0.5, false, bndScene);
	}
	
	// The duplicate operation for the marks (we avoid clone() in case its confusing, we might not always shallow copy)
	@Override
	public Mark duplicate() {
		return new MarkCircle( this );
	}
	
	
	@Override
	public  boolean hasOverlapWithQuick( ) {
		return true;
	}
	
	
	@Override
	// note that this does not return the number of voxels of
	//  overlap, but some sort of approximation based upon
	//  the volume ratios
	public double overlapWithQuick( Mark m, int regionID ) {

		if (MarkCircle.class.isAssignableFrom(m.getClass())) {
			MarkCircle target = (MarkCircle) m;
			return this.radius + target.radius - this.getPos().distance(target.getPos());
		}
		assert false;
		return 0.0;
	}
	
	
	// Does a quick test to see if we can reject the possibility
	// of overlap
	//   true -> no overlap
	//   false -> maybe overlap, maybe not
	@Override
	public boolean quickTestNoOverlap( Mark m, int regionID ) {
		return overlapWithQuick(m,regionID)==0;
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
		
		byte emptyFlag = 0;
		
		byte flagSubmark = dist<=radiusSq ? FLAG_SUBMARK_INSIDE : FLAG_SUBMARK_SHELL;
		return RegionMembershipUtilities.setAsMemberFlag( emptyFlag, flagSubmark );

	}
	
	
	// NB objects are scaled in pre-rotated position i.e. when aligned to axes
	// We actually scale in all 3 dimensions
	@Override
	public void scale( double mult_factor )  {
		super.scale(mult_factor);
		
		if (this.bndRadius!=null) {
			this.bndRadius = this.bndRadius.duplicate();
			this.bndRadius.scale( mult_factor );
		}
		
		setRadius( this.radius * mult_factor );
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
		
    	this.radiusSq = squared( this.radius);
    	this.radiusExtraSq = squared( this.radius + MarkCircle.SphereExtraRad);
	}
	
	@Override
	public boolean equalsDeep(Mark m) {
		
		if (!super.equalsDeep(m)) {
			return false;
		}
		
		if (!(m instanceof MarkCircle)) {
			return false;
		}
		
		MarkCircle trgt = (MarkCircle) m;
		
		if (radius!=trgt.radius) {
			return false;
		}
		
		return true;
	}

	@Override
	public int numDims() {
		return 2;
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
