package ch.ethz.biol.cell.core.line;

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

import org.anchoranalysis.core.geometry.Point2f;
import org.anchoranalysis.core.geometry.Point3d;

public class LineUtilities {

	private LineUtilities() {
		
	}
	
	// Returns angle
	public static double calculateAngle( Point2f pntA, Point2f pntB ) {
		return Math.atan2(
			pntA.getY() - pntB.getY(),
			pntA.getX() - pntB.getX()
		);
	}
	
	// Makes sure the angle is always positive
	public static double calculateAnglePositive( Point2f pntA, Point2f pntB ) {
		double a = Math.atan2( pntA.getY() - pntB.getY(), pntA.getX() - pntB.getX() );
		
		// We keep everything positive
		if (a < 0) {
			a = a + (2*Math.PI);
		}
		
		return a;
	}
	
	public static double dotProduct( Point3d a, Point3d b ) {
		return a.getX()*b.getX() + a.getY()*b.getY() + a.getZ()*b.getZ();
	}
	
	public static double l2norm( Point3d a ) {
		 double sumSquares = Math.pow(a.getX(), 2.0) + Math.pow(a.getY(), 2.0) + Math.pow(a.getZ(), 2.0);
		 return Math.sqrt( sumSquares );
	}
}
