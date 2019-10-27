package org.anchoranalysis.anchor.mpp.bean.proposer.radii;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.math.rotation.RotationMatrix;
import org.anchoranalysis.math.rotation.RotationMatrix2DFromRadianCreator;
import org.anchoranalysis.math.rotation.RotationMatrix3DFromRadianCreator;

import ch.ethz.biol.cell.imageprocessing.bound.BidirectionalBound;
import ch.ethz.biol.cell.imageprocessing.bound.RslvdBound;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.MarkBounds;
import ch.ethz.biol.cell.mpp.mark.MarkEllipse;

public class RadiiForOrientationProposer extends RadiiProposerWithBoundProposer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6229777913998235600L;
	private double boundsRatioThreshold = 1.3;
	
	private enum Axes {
		X,
		Y,
		Z
	}
	
	private RotationMatrix rotate90Degrees( RotationMatrix inMatrix, Axes axisRotate ) {

		double radians = Math.PI/2;
		
		RotationMatrix rotate90Degrees;
		
		if (inMatrix.getNumDim()==2) {
			rotate90Degrees = new RotationMatrix2DFromRadianCreator( radians ).createRotationMatrix();
		} else {
			switch( axisRotate ) {
			case X:
				rotate90Degrees = new RotationMatrix3DFromRadianCreator( radians, 0, 0 ).createRotationMatrix();
				break;
			case Y:
				rotate90Degrees = new RotationMatrix3DFromRadianCreator( 0, radians, 0 ).createRotationMatrix();
				break;
			case Z:
				rotate90Degrees = new RotationMatrix3DFromRadianCreator( 0, 0, radians ).createRotationMatrix();
				break;
			default:
				assert false;
				rotate90Degrees = null;
			}
			
		}
		//
		return inMatrix.mult(rotate90Degrees);
	}
	
	private RslvdBound createRslvdBound( String axisName, Axes axisRotate, RslvdBound boundIn, Point3d pos, RotationMatrix orientationIn, ImageDim bndScene, RslvdBound radiiBounds, ErrorNode proposerFailureDescription ) {
		
		// We require that our x-axis in the orientation direction is always larger than the y-axis
		//   so that it is orientated "in the direction of" our orientation
		// So we place the radius value as a maximum

		
		RotationMatrix orientationY = rotate90Degrees(orientationIn, axisRotate);
		
		return rslvBidirectional( pos, radiiBounds, boundIn, bndScene, orientationY, proposerFailureDescription, axisName);
	}
	
	@Override
	// When we have no bounds, we should create bounds from the boundCalculator
	public Point3d propose(Point3d pos, MarkBounds markBounds, RandomNumberGenerator re, ImageDim bndScene, Orientation orientation, ErrorNode proposerFailureDescription) {
		
		proposerFailureDescription = proposerFailureDescription.add("RadiiForOrientationProposer");
		
		RslvdBound markBoundInRslv = markBounds.calcMinMax( bndScene.getRes(), bndScene.getZ()>1 );
		
		if (markBoundInRslv==null) {
			//proposerFailureDescription.add( "no intersection between markBound for yAxis and the x radius" );
			proposerFailureDescription.add("markBoundInRslv cannot be resolved");
			return null;
		}
		
		RotationMatrix orientationX = orientation.createRotationMatrix();
		
		RslvdBound boundGenX = rslvBidirectional( pos, markBoundInRslv, markBoundInRslv, bndScene, orientationX, proposerFailureDescription, "xAxis");
		if (boundGenX==null) {
			return null;
		}
		
		
		// THIS IS THE FIRST POINT AFTER WHICH WE USE THE RANDOM GENERATOR
		//
		//  Any failures before now, should not cause any repeats, as they are deterministic
		//  Failures after this, are not deterministic, so trying again might give a better result
		//

		Point3d radiiProp = new Point3d();
		
		// We pick our X radius
		radiiProp.setX( boundGenX.randOpen(re) );

		{
			RslvdBound boundGenY = createRslvdBound( "y", Axes.X, markBoundInRslv, pos, orientationX, bndScene, markBoundInRslv, proposerFailureDescription );
			if (boundGenY==null) {
				return null;
			}
			
			// We pick our Y radius
			radiiProp.setY( boundGenY.randOpen(re) );
		}
		
		if (orientationX.getNumDim()>=3) {
			
			RslvdBound boundGenZ = createRslvdBound( "z", Axes.Y, markBoundInRslv, pos, orientationX, bndScene, markBoundInRslv, proposerFailureDescription );
			if (boundGenZ==null) {
				return null;
			}
			
			// We pick our Y radius
			radiiProp.setZ( boundGenZ.randOpen(re) );
			
		} else {
			radiiProp.setZ( 0 );
		}
        
		assert( radiiProp.getX() < bndScene.getX() );
		assert( radiiProp.getY() < bndScene.getY() );
		assert( radiiProp.getZ() < bndScene.getZ() );
		
		return new Point3d( radiiProp.getX(), radiiProp.getY(), radiiProp.getZ() );
	}

	@SuppressWarnings("unused")
	private static double ratioBounds( ImageDim sd, BidirectionalBound bi ) {
		
		if (bi.getForward()==null || bi.getReverse()==null) {
			return -1;
		}
		
		double fwd = bi.getForward().getMax();
		double rvrs = bi.getReverse().getMax();
		
		double maxBoth, minBoth;
		if (fwd >= rvrs) {
			maxBoth = fwd;
			minBoth = rvrs;
		} else {
			maxBoth = rvrs;
			minBoth = fwd;
		}
		
		return maxBoth / minBoth;
	}
	
	
	
	private RslvdBound rslvBidirectional( Point3d pos, RslvdBound minMaxBound, RslvdBound markBound, ImageDim bndScene, RotationMatrix orientation, ErrorNode proposerFailureDescription, String axisName ) {

		RslvdBound singleBound;

		// This calculates bounded limits along a line through the centre with orientation the same as the ellipse (we call this the x-axis bound) 
		BidirectionalBound biBound = getBoundProposer().propose(pos, orientation, bndScene, minMaxBound, proposerFailureDescription);
		
		if (biBound==null) {
			proposerFailureDescription.addFormatted("Cannot calculate %s bi-directional bounds", axisName);
			return null;
		}
		
		proposerFailureDescription.addFormatted("%s=%s", axisName, biBound.toString());			
		
		// LET's always take the minimum bound
		singleBound = biBound.calcMinimum();
		
		if (singleBound==null) {
			proposerFailureDescription.addFormatted( "no minima on %s bounds",axisName );
			return null;
		}
		
		// places a maximum
		singleBound = markBound.intersect( singleBound);
		
		if (singleBound==null) {
			proposerFailureDescription.addFormatted( "cannot resolve %s single bounds with the mark bound", axisName);
			return null;
		}
		
		return singleBound;
	}
	
	@Override
	public boolean isCompatibleWith(Mark testMark) {
		return testMark instanceof MarkEllipse;
	}

	public double getBoundsRatioThreshold() {
		return boundsRatioThreshold;
	}

	public void setBoundsRatioThreshold(double boundsRatioThreshold) {
		this.boundsRatioThreshold = boundsRatioThreshold;
	}
}
