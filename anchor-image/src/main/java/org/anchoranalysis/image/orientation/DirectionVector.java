package org.anchoranalysis.image.orientation;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

/*-
 * #%L
 * anchor-image
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
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.Tuple3d;
import org.anchoranalysis.core.geometry.Vector3d;

public class DirectionVector {
	
	private Tuple3d vector;
	
	public DirectionVector() {
		vector = new Vector3d();
	}
	
	public DirectionVector( Tuple3d vector ) {
		this.vector = vector;
	}
	
	public DirectionVector( double x, double y, double z ) {
		this();
		vector.setX( x );
		vector.setY( y );
		vector.setZ( z );
	}

	public final double getX() {
		return vector.getX();
	}

	public final double getY() {
		return vector.getY();
	}

	public final double getZ() {
		return vector.getZ();
	}

	public final void setX(double arg0) {
		vector.setX(arg0);
	}

	public final void setY(double arg0) {
		vector.setY(arg0);
	}

	public final void setZ(double arg0) {
		vector.setZ(arg0);
	}
	
	/** Sets an element of the vector by the index of its position,  index=0 is the X-element, index=1 is the Y-element etc. */
	public void setIndex( int index, double value ) {
		if (index==0) {
			vector.setX(value);
		} else if (index==1) {
			vector.setY(value);
		} else if (index==2) {
			vector.setZ(value);
		} else {
			throw new AnchorFriendlyRuntimeException("Index must be >= 0 and < 3");
		}
	}
	
	public static DirectionVector createBetweenTwoPoints( Point3d pnt1, Point3d pnt2 ) {
		
		double sx = pnt2.getX() - pnt1.getX();
		double sy = pnt2.getY() - pnt1.getY();
		double sz = pnt2.getZ() - pnt1.getZ();
		
		double norm = Math.sqrt( Math.pow(sx,2.0) + Math.pow(sy,2.0) + Math.pow(sz,2.0) );
		
		DirectionVector out = new DirectionVector();
		out.setX( sx / norm );
		out.setY( sy / norm );
		out.setZ( sz / norm );
		return out;
	}
	
	public static DirectionVector createBetweenTwoPoints( Point3i pnt1, Point3i pnt2 ) {
		
		double sx = (double) pnt2.getX() - pnt1.getX();
		double sy = (double) pnt2.getY() - pnt1.getY();
		double sz = (double) pnt2.getZ() - pnt1.getZ();
		
		double norm = Math.sqrt( Math.pow(sx,2.0) + Math.pow(sy,2.0) + Math.pow(sz,2.0) );
		
		DirectionVector out = new DirectionVector();
		out.setX( sx / norm );
		out.setY( sy / norm );
		out.setZ( sz / norm );
		return out;
	}
	
	public Vector3d createVector3d() {
		return new Vector3d(vector);
	}

	
}
