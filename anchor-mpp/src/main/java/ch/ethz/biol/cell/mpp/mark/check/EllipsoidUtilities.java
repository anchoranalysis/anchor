package ch.ethz.biol.cell.mpp.mark.check;

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

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.math.rotation.RotationMatrix;

import ch.ethz.biol.cell.mpp.mark.MarkEllipsoid;

public class EllipsoidUtilities {

	public static double[] normalisedRadii( MarkEllipsoid mark, ImageRes res ) {
		// We re-calculate all the bounds to take account of the different z-resolution
		
		// We get the rotated points of (1,0,0)*getRadii().getX() and (0,1,0)*getRadii().getY() and (0,1,0)*getRadii().getZ() 
		RotationMatrix rotMatrix = mark.getOrientation().createRotationMatrix();
		
		Point3d xRot = rotMatrix.calcRotatedPoint( new Point3d(mark.getRadii().getX(),0,0) );
		Point3d yRot = rotMatrix.calcRotatedPoint( new Point3d(0,mark.getRadii().getY(),0) );
		Point3d zRot = rotMatrix.calcRotatedPoint( new Point3d(0,0,mark.getRadii().getZ()) );
		
		double zRel = res.getZRelRes();
		// We adjust each point for the z contribution
		xRot.setZ( xRot.getZ() * zRel );
		yRot.setZ( yRot.getZ() * zRel );
		zRot.setZ( zRot.getZ() * zRel );
		
		Point3d zero = new Point3d(0,0,0);
		
		double xNorm = xRot.distance(zero);
		double yNorm = yRot.distance(zero);
		double zNorm = zRot.distance(zero);	
		
		return new double[] { xNorm, yNorm, zNorm };
	}
}
