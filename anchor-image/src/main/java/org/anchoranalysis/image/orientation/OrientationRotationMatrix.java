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

import org.anchoranalysis.math.rotation.RotationMatrix;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class OrientationRotationMatrix extends Orientation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -496736778234811706L;
	
	private RotationMatrix rotationMatrix;
	
	public OrientationRotationMatrix() {
		
	}
	
	public OrientationRotationMatrix( RotationMatrix rotationMatrix ) {
		this.rotationMatrix = rotationMatrix;
	}
	
	@Override
	public Orientation duplicate() {
		OrientationRotationMatrix out = new OrientationRotationMatrix();
		out.rotationMatrix = rotationMatrix.duplicate();
		return out;
	}

	@Override
	public RotationMatrix createRotationMatrix() {
		return rotationMatrix;
	}

	@Override
	public boolean equals(Object other) {
		
		if (other == null) { return false; }
		if (other == this) { return true; }
		
		if (!(other instanceof OrientationRotationMatrix)) {
			return false;
		}
		
		OrientationRotationMatrix otherC = (OrientationRotationMatrix) other;
		return rotationMatrix.equals(otherC.rotationMatrix);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(rotationMatrix)
				.toHashCode();
	}

	@Override
	public Orientation negative() {
		// The inverse of a rotation matrix is equal to it's transpose because it's an orthogonal matrix
		RotationMatrix mat = rotationMatrix.duplicate();
		mat.multConstant(-1);
		return new OrientationRotationMatrix(mat);
	}

	public RotationMatrix getRotationMatrix() {
		return rotationMatrix;
	}

	public void setRotationMatrix(RotationMatrix rotationMatrix) {
		this.rotationMatrix = rotationMatrix;
	}

	@Override
	public int getNumDims() {
		return rotationMatrix.getNumDim();
	}
}
