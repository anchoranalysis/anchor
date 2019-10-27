package org.anchoranalysis.image.orientation;

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

import org.anchoranalysis.core.geometry.Vector3d;
import org.anchoranalysis.core.name.provider.NameValueSet;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.math.rotation.RotationMatrix;
import org.anchoranalysis.math.rotation.RotationMatrixFromAxisAngleCreator;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class OrientationAxisAngle extends Orientation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2592680414423106545L;
	
	// START BEAN PROPERTIES
	private Vector3d axis;	// should be normalised
	private double angle;	// in radians
	// END BEAN PROPERTIES
	
	@Override
	public String toString() {
		return String.format("angle=%f axis=%s",angle, axis.toString());
	}
	
	@Override
	public OrientationAxisAngle duplicate() {
		OrientationAxisAngle out = new OrientationAxisAngle();
		out.axis = new Vector3d(axis);
		out.angle = angle;
		return out;
	}

	@Override
	public RotationMatrix createRotationMatrix() {
		return new RotationMatrixFromAxisAngleCreator(axis,angle).createRotationMatrix();
	}

	@Override
	public boolean equals(Object other) {
		
		if (other == null) { return false; }
		if (other == this) { return true; }
		
		if (!(other instanceof OrientationAxisAngle)) {
			return false;
		}
		
		OrientationAxisAngle otherC = (OrientationAxisAngle) other;
		return axis.equals(otherC.axis) && angle==otherC.angle;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(axis)
				.append(angle)
				.toHashCode();
	}

	@Override
	public Orientation negative() {
		OrientationAxisAngle dup = duplicate();
		dup.angle = dup.angle + Math.PI;
		return dup;
	}

	@Override
	public void addProperties(NameValueSet<String> nvc) {
	}

	@Override
	public void addPropertiesToMask(ObjMaskWithProperties mask) {
	}

	public static Orientation rotateOneVectorOntoAnother( Vector3d vecSrc, Vector3d vecOnto ) {
		
		final double ep = 10e-12;
		// See http://www.gamedev.net/topic/591937-rotate-one-vector-onto-another/
		
		// Dot product
		double dotProd = vecSrc.dot(vecOnto);
		
		Vector3d crossProd = Vector3d.cross(vecSrc, vecOnto);
		
		double mag = crossProd.length();
		
		// Also useful as reference
		// http://math.stackexchange.com/questions/293116/rotating-one-3-vector-to-another
		
		
		if (mag > ep) {
		
			crossProd.scale(1/mag);
			
			//out.angle = Math.acos(dotProd);
		
			OrientationAxisAngle out = new OrientationAxisAngle();
			out.angle = Math.atan2(mag, dotProd);
			out.axis = crossProd;
			return out;
			
		} else {
			
			if (dotProd > 0){

				 // Nearly positively aligned; skip rotation, or compute
			    // axis and angle using other means
				return new OrientationIdentity(3);
				
			} else {
				
				// negatively aligned we set an angle of PI

				
				 // Nearly negatively aligned; axis is any vector perpendicular
			    // to either vector, and angle is 180 degrees
				
				// TO find a perpendic
				
				
				OrientationAxisAngle out = new OrientationAxisAngle();
				out.angle = Math.PI;
				out.axis = findPerpVector( vecSrc );
				return out;

			}
		}
	}


	// 
	private static Vector3d findPerpVector( Vector3d vec ) {
		// We needto find any vector whose dot product is 0
		if (vec.getX()>0 || vec.getY()>0) {
			return new Vector3d( vec.getY()*-1, vec.getX(), 0);
		} else {
			// This handle's the case where both X and Y are 0
			return new Vector3d( 0, vec.getZ()*-1, vec.getY() );
		}
	}


	public Vector3d getAxis() {
		return axis;
	}

	public void setAxis(Vector3d axis) {
		this.axis = axis;
	}

	public double getAngle() {
		return angle;
	}

	// In radians
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	@Override
	public int getNumDims() {
		return 3;
	}

}
