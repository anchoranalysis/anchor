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

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;


public abstract class MarkAbstractPosition extends Mark implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6976277985708631268L;
	
	// START mark state
	private Point3d pos;
	// END mark state
	
	// Constructor
	public MarkAbstractPosition() {
		super();
		this.pos = new Point3d();
	}

	// Copy constructor
	public MarkAbstractPosition(MarkAbstractPosition src) {
		super(src);
    	this.pos = new Point3d( src.pos );
	}

    
    public String strPos() {
    	return String.format("[%6.1f,%6.1f,%6.1f]", this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }
    
   
    
    @Override
	public void scale( double mult_factor ) {
    	scaleXYPnt(this.pos,mult_factor);
    }
    
    public static void scaleXYPnt( Point3d pnt, double mult_factor ) {
    	pnt.setX( pnt.getX() * mult_factor ); 
    	pnt.setY( pnt.getY() * mult_factor );
    }
    
	@Override
	public Point3d centerPoint() {
		return getPos();
	}
	
	public Point3d getPos() {
		return pos;
	}

	public void setPos(Point3d pos) {
		this.pos = pos;
		clearCacheID();
	}
	
	// Checks if two marks are equal by comparing all attributes
	@Override
	public boolean equalsDeep(Mark m) {
		
		if (!super.equalsDeep(m)) {
			return false;
		}
		
		if (!(m instanceof MarkAbstractPosition)) {
			return false;
		}
		
		MarkAbstractPosition trgt = (MarkAbstractPosition) m;
		
		if (!pos.equals(trgt.pos)) {
			return false;
		}

		return true;
	}
	
	@Override
	public ObjectWithProperties calcMask( ImageDimensions bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut ) {
		
		ObjectWithProperties mask = super.calcMask(bndScene, rm, bvOut);
		mask.setProperty(
			"midpointInt",
			calcRelativePoint(pos, mask.getBoundingBox().cornerMin())
		);
		return mask;
	}
	
	@Override
	public OverlayProperties generateProperties(ImageResolution sr) {
		OverlayProperties op = super.generateProperties(sr);
		
		int numDims = numDims();
		
		if (numDims>=1) {
			op.addDoubleAsString("Pos X", pos.getX());
		}
		if (numDims>=2) {
			op.addDoubleAsString("Pos Y", pos.getY());
		}
		if (numDims>=3) {
			op.addDoubleAsString("Pos Z", pos.getZ());
		}
		return op;
	}
	
	@Override
	public void assignFrom( Mark srcMark ) throws OptionalOperationUnsupportedException {
		
		if (!(srcMark instanceof MarkAbstractPosition)) {
			throw new OptionalOperationUnsupportedException("srcMark must be of type MarkAbstractPosition");
		}
		
		MarkAbstractPosition srcMarkAbstractPos = (MarkAbstractPosition) srcMark;
		this.pos = srcMarkAbstractPos.getPos();
		
		// As the cacheID might be cleared by previous sets
		super.assignFrom( srcMark );
	}
	
	/** Calculates a relative-point from pntGlobal to pntBase */
	private static Point3i calcRelativePoint(Point3d pntGlobal, ReadableTuple3i pntBase) {
		Point3i pntOut = PointConverter.intFromDouble(pntGlobal);
		pntOut.subtract(pntBase);
		return pntOut;
	}
}
