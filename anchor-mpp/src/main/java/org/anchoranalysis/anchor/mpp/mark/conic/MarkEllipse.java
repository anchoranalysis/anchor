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
import java.util.function.BiFunction;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.ISetMarksExplicit;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkAbstractRadii;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation2D;
import org.anchoranalysis.math.rotation.RotationMatrix;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;

import static org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities.*;
import static org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers.*;
import static org.anchoranalysis.anchor.mpp.mark.conic.PropertyUtilities.*;
import static org.anchoranalysis.anchor.mpp.mark.conic.TensorUtilities.*;

public class MarkEllipse extends MarkAbstractRadii implements Serializable, ISetMarksExplicit  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2678275834893266874L;

	private static final int MatNumDim = 2;
	
	private static byte FLAG_SUBMARK_NONE = flagForNoRegion();
	private static byte FLAG_SUBMARK_REGION0 = flagForRegion( SUBMARK_INSIDE, SUBMARK_CORE );
	private static byte FLAG_SUBMARK_REGION1 = flagForRegion( SUBMARK_INSIDE, SUBMARK_SHELL );
	private static byte FLAG_SUBMARK_REGION2 = flagForRegion( SUBMARK_SHELL, SUBMARK_SHELL_OUTSIDE);
	private static byte FLAG_SUBMARK_REGION3 = flagForRegion( SUBMARK_OUTSIDE );
	
	// START Configurable parameters	
	private double shellRad = 0.2;
	// END configurable parameters

	// START mark state
	private Point2d radii;
	private Orientation orientation = new Orientation2D();
	// END mark state
	
	// START internal objects
	private EllipsoidMatrixCalculator ellipsoidCalculator;
	private double shellInt;
	private double shellExt;
	private double shellExtOut;
	
	private double shellIntSq;
	private double shellExtSq;
	private double shellExtOutSq;
	
	private double radiiShellMaxSq;
    // END internal objects

    // Default Constructor
	public MarkEllipse() {
		super();
		
		this.radii = new Point2d();
		
		ellipsoidCalculator = new EllipsoidMatrixCalculator(MatNumDim);
	}
	
	// Copy Constructor
	public MarkEllipse(MarkEllipse src) {
		super(src);
		this.radii = new Point2d( src.radii );
		
		this.shellRad = src.shellRad;
        
		this.ellipsoidCalculator = new EllipsoidMatrixCalculator(src.ellipsoidCalculator);
		this.orientation = src.orientation.duplicate();
		
		this.shellExt = src.shellExt;
        this.shellInt = src.shellInt;
        this.shellExtOut = src.shellExtOut;
        this.shellExtSq = src.shellExtSq;
        this.shellIntSq = src.shellIntSq;
        this.shellExtOutSq = src.shellExtOutSq;
        
        this.radiiShellMaxSq = src.radiiShellMaxSq;
	}


	@Override
	public String getName() {
		return new String("ellipsoid");
	}

	public static double getEllipseSum( double x, double y, DoubleMatrix2D mat ) {
		return x * (x * mat.get(0, 0) + y*mat.get(1, 0))
			+ y * (x * mat.get(0, 1) + y * mat.get(1, 1) );		
	}
	
	// Where is a point in relation to the current object
	@Override
	public final byte evalPntInside( Point3d pt ) {
		
		if (pt.distanceSquared(this.getPos()) > radiiShellMaxSq) {
			return FLAG_SUBMARK_NONE;
		}
		
		// It is permissible to mutate the point during calculation
		double x = pt.getX() - getPos().getX();
		double y = pt.getY() - getPos().getY();

		// We exit early if it's inside the internal shell
		double sum = getEllipseSum(x, y, ellipsoidCalculator.getEllipsoidMatrix() );

		if( sum <= shellIntSq ) {
			return FLAG_SUBMARK_REGION0;
		}
		
		if( sum <= 1 ) {
			return FLAG_SUBMARK_REGION1;
		}
		
		if( sum <= shellExtSq ) {
			return FLAG_SUBMARK_REGION2;
		}

		if( sum <= shellExtOutSq ) {
			return FLAG_SUBMARK_REGION3;
		}
		
		return FLAG_SUBMARK_NONE;
	}

    @Override
	public double volume(int regionID) {
    	
    	if (regionID==GlobalRegionIdentifiers.SUBMARK_INSIDE) {
    		return areaForShell(1);
    	} else if (regionID==GlobalRegionIdentifiers.SUBMARK_SHELL) {
    		return areaForShell(shellExt) - areaForShell(shellInt);
		} else {
			assert false;
			return 0.0;
		}
    }
    
    private double areaForShell( double multiplier ) {
    	return ( Math.PI * this.radii.getX() * this.radii.getY() * Math.pow(multiplier,2) );
    }

    
	
    // Circumference
	public double circumference(int regionID) {
		if (regionID==GlobalRegionIdentifiers.SUBMARK_SHELL) {
			return calcCircumferenceUsingRamunjanApprox( this.radii.getX() * (1.0 + shellRad), this.radii.getY() * (1.0 + shellRad) );
		} else {
			return calcCircumferenceUsingRamunjanApprox( this.radii.getX(), this.radii.getY() );
		}
	}
    
    
	private static double calcCircumferenceUsingRamunjanApprox( double a, double b ) {
		// http://www.mathsisfun.com/geometry/ellipse-perimeter.html
		
		double first = 3 * (a+b);
		double second = ((3*a) + b) * (a + (3*b)); 
		
		return Math.PI * (first - Math.sqrt(second));
	}
	
	
	@Override
	public MarkEllipse duplicate() {
		return new MarkEllipse( this );
	}


	@Override
	public String toString() {
		return String.format("%s %s pos=%s %s vol=%e shellRad=%f", "Ellpsd", strId(), strPos(), strMarks(), volume(0), shellRad );
	}



	

	
	public void updateShellRad( double shellRad ) {
		setShellRad(shellRad);
		updateAfterMarkChange();
	}
	
	private void updateAfterMarkChange() {
		
		assert( shellRad > 0 );
		
		DoubleMatrix2D matRot = orientation.createRotationMatrix().getMatrix();
		  
		double[] radiusArray = twoElementArray( this.radii.getX(), this.radii.getY() );
		this.ellipsoidCalculator.update( radiusArray, matRot );
		
		this.shellInt = 1.0 - this.shellRad;
		this.shellExt = 1.0 + this.shellRad;
		this.shellExtOut = 1.0 + (this.shellRad*2);
	
		this.shellIntSq = squared(shellInt);
		this.shellExtSq = squared(shellExt);
		this.shellExtOutSq = squared(shellExtOut);
		this.radiiShellMaxSq = squared( ellipsoidCalculator.getMaximumRadius()*shellExt );
		
		assert( !Double.isNaN(this.ellipsoidCalculator.getEllipsoidMatrix().get(0, 0)) ); 
	}
	
	public void setMarksExplicit( Point3d pos, Orientation orientation, Point2d radii ) {
		assert( pos.getZ() == 0 );
		super.setPos(pos);
		this.orientation = orientation;
		this.radii = radii;
		updateAfterMarkChange();
		clearCacheID();
	}
	
	@Override
	public void setMarksExplicit(Point3d pos) {
		setMarksExplicit(pos, orientation, radii);
	}
	
	@Override
	public void setMarksExplicit(Point3d pos, Orientation orientation) {
		setMarksExplicit(pos, orientation, radii);
	}

	public void setMarksExplicit( Point3d pos, Orientation orientation, Point3d radii ) {
		setMarksExplicit(pos, orientation, new Point2d( radii.getX(), radii.getY() ) );
	}
	
	@Override
	public BoundingBox bbox( ImageDim bndScene, int regionID ) {
		
        DoubleMatrix1D bboxMatrix = ellipsoidCalculator.getBoundingBoxMatrix().copy();

        if (regionID==GlobalRegionIdentifiers.SUBMARK_SHELL) {
			bboxMatrix.assign( Functions.mult(shellExtOut) );
		} 
            
		return BoundsCalculator.bboxFromBounds( getPos(), bboxMatrix, false, bndScene );
	}

	// Does a quick test to see if we can reject the possibility
	// of overlap
	//   true -> no overlap
	//   false -> maybe overlap, maybe not
	@SuppressWarnings("static-access")
	@Override
	public boolean quickTestNoOverlap( Mark m, int regionID ) {
		
		// No quick tests unless it's the same type of class
		if (!(m instanceof MarkEllipse)) {
			return false;
		}
		
		MarkEllipse trgtMark = (MarkEllipse) m;
		
		DoubleMatrix1D relPos = twoElementMatrix(
			trgtMark.getPos().getX() - getPos().getX(),
			trgtMark.getPos().getY() - getPos().getY()
		);
		
		DoubleMatrix1D relPosSq = relPos.copy();
		relPosSq.assign( Functions.functions.square );
		double dist = relPosSq.zSum();
		
		// Definitely outside
		if( dist > Math.pow(getMaximumRadius() + trgtMark.getMaximumRadius(), 2.0) ) {
			return true;
		}
		
		return false;
	}
	
	private double getMaximumRadius() {
		return ellipsoidCalculator.getMaximumRadius();
	}

	public double getShellRad() {
		return shellRad;
	}

	public void setShellRad(double shellRad) {
		this.shellRad = shellRad;
		clearCacheID();
	}

	public void setMarks(Point2d radii, Orientation orientation ) {
		this.orientation = orientation;
		this.radii = radii;
		clearCacheID();
		updateAfterMarkChange();
	}
	
	@Override
	public void assignFrom( Mark srcMark ) throws OptionalOperationUnsupportedException {
		
		if (!(srcMark instanceof MarkEllipse)) {
			throw new OptionalOperationUnsupportedException("srcMark must be of type MarkEllipse");
		}
		
		MarkEllipse srcMarkEll = (MarkEllipse) srcMark;
		setMarks( srcMarkEll.getRadii(), srcMarkEll.getOrientation() );
		
		// As the cacheID might be cleared by previous sets
		super.assignFrom( srcMark);
	}
	
	public void scaleRadii( double mult_factor ) {
		this.radii.setX( this.radii.getX() * mult_factor );
		this.radii.setY( this.radii.getY() * mult_factor );
		updateAfterMarkChange();
		clearCacheID();
	}
	
	// NB objects are scaled in pre-rotated position i.e. when aligned to axes
	@Override
	public void scale( double mult_factor ) {
		super.scale(mult_factor);
	
		this.radii.setX( this.radii.getX() * mult_factor );
		this.radii.setY( this.radii.getY() * mult_factor );
		updateAfterMarkChange();
		clearCacheID();
	}

	@Override
	public boolean equalsDeep(Mark m) {
		
		if (!super.equalsDeep(m)) {
			return false;
		}
		
		if (!(m instanceof MarkEllipse)) {
			return false;
		}
		
		MarkEllipse trgt = (MarkEllipse) m;
		
		if (!radii.equals(trgt.radii)) {
			return false;
		}
		
		if (!orientation.equals(trgt.orientation)) {
			return false;
		}
		
		return true;
	}

	
	 
	@Override
	public ObjMaskWithProperties calcMask( ImageDim bndScene, RegionMembershipWithFlags rm, BinaryValuesByte bvOut) {
		
		ObjMaskWithProperties mask = super.calcMask(bndScene, rm, bvOut );
		orientation.addPropertiesToMask(mask);
		
		// Axis orientation
		addAxisOrientationProperties(mask, rm);
			
		return mask;
	}
		
	@Override
	public OverlayProperties generateProperties(ImageRes sr) {
		OverlayProperties op = super.generateProperties(sr);
		
		op.addDoubleAsString("Radius X (pixels)", radii.getX() );
		op.addDoubleAsString("Radius Y (pixels)", radii.getY() );
		orientation.addProperties(op.getNameValueSet());
		op.addDoubleAsString("Shell Radius (pixels)", shellRad );
		
		return op;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public Point2d getRadii() {
		return radii;
	}

	@Override
	public int numDims() {
		return 2;
	}
	
	@Override
	public double[] createRadiiArray() {
		return twoElementArray(	this.radii.getX(), this.radii.getY() );
	}
	
	@Override
	public double[] createRadiiArrayRslvd( ImageRes res ) {
		Point2d radii = getRadii();
		return twoElementArray( radii.getX(), radii.getY() );
	}

	@Override
	public int numRegions() {
		return 2;
	}
	
	@Override
	public BoundingBox bboxAllRegions(ImageDim bndScene) {
		return bbox(bndScene, GlobalRegionIdentifiers.SUBMARK_SHELL);
	}

	private void addAxisOrientationProperties(ObjMaskWithProperties mask, RegionMembershipWithFlags rm) {
		
		// NOTE can we do this more smartly?
		double radiiFactor = rm.getRegionID()==0 ? 1.0 : 1.0+shellRad;
		
		double radiusProjectedX = radii.getX() * radiiFactor;
		
		RotationMatrix rotMat = orientation.createRotationMatrix();
		double[] endPoint1 = rotMat.calcRotatedPoint( twoElementArray(-1 * radiusProjectedX) );
		double[] endPoint2 = rotMat.calcRotatedPoint( twoElementArray(radiusProjectedX) );
				
		double[] xMinMax = minMaxEndPoint(endPoint1, endPoint2, 0, getPos().getX() );
		double[] yMinMax = minMaxEndPoint(endPoint1, endPoint2, 1, getPos().getY() );
		
		addPoint2dProperty(mask, "xAxisMin", xMinMax[0], yMinMax[0] );
		addPoint2dProperty(mask, "xAxisMax", xMinMax[1], yMinMax[1] );		
	}

	private String strMarks() {
		return String.format("rad=[%3.3f, %3.3f] rot=%s", this.radii.getX(), this.radii.getY(), this.orientation );
	}
	
	private static double[] minMaxEndPoint( double[] endPoint1, double[] endPoint2, int dimIndex, double toAdd ) {
		return twoElementArray(
			applyEndPoint(endPoint1, endPoint2, dimIndex, toAdd, Math::min),
			applyEndPoint(endPoint1, endPoint2, dimIndex, toAdd, Math::max)
		);
	}
		
	private static double applyEndPoint( double[] endPoint1, double[] endPoint2, int dimIndex, double toAdd, BiFunction<Double,Double,Double> func ) {
		return func.apply(endPoint1[dimIndex], endPoint2[dimIndex]) + toAdd;
	}

}
