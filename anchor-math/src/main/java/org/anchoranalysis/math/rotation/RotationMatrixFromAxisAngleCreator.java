package org.anchoranalysis.math.rotation;

/*-
 * #%L
 * anchor-math
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

import cern.colt.matrix.DoubleMatrix2D;

public class RotationMatrixFromAxisAngleCreator extends RotationMatrixCreator {

	private Vector3d point;
	private double angle;
	
	// assumes point is a unit-vector 
	public RotationMatrixFromAxisAngleCreator(Vector3d point, double angle) {
		super();
		this.point = point;
		this.angle = angle;
	}

	@Override
	public void createRotationMatrix(RotationMatrix matrix) {
		// http://en.wikipedia.org/wiki/Rotation_matrix
		
		DoubleMatrix2D mat = matrix.getMatrix();
		
		double oneMinusCos = 1-Math.cos(angle);
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		mat.set(0, 0, cos + point.getX()*point.getX()*oneMinusCos);
		mat.set(1, 0, point.getY()*point.getX()*oneMinusCos + point.getZ()*sin );
		mat.set(2, 0, point.getZ()*point.getX()*oneMinusCos - point.getY()*sin );
		
		mat.set(0, 1, point.getX()*point.getY()*oneMinusCos - point.getZ()*sin );
		mat.set(1, 1, cos + point.getY()*point.getY()*oneMinusCos);
		mat.set(2, 1, point.getZ()*point.getY()*oneMinusCos + point.getX()*sin );

		mat.set(0, 2, point.getX()*point.getZ()*oneMinusCos + point.getY()*sin );
		mat.set(1, 2, point.getY()*point.getZ()*oneMinusCos - point.getX()*sin );
		mat.set(2, 2, cos + point.getZ()*point.getZ()*oneMinusCos);
	}

	@Override
	public int getNumDim() {
		return 3;
	}

}
