package org.anchoranalysis.math.rotation;

/*
 * #%L
 * anchor-core
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

import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.Point3d;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;

public class RotationMatrix implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DoubleMatrix2D delegate;
	
	public RotationMatrix( int numDim ) {
		delegate = DoubleFactory2D.dense.make(numDim, numDim);
	}
	
	public RotationMatrix( DoubleMatrix2D matrix ) {
		assert matrix!=null;
		this.delegate = matrix;
	}

	public Point3d calcRotatedPoint( Point3d pntIn ) {

		if (delegate.rows()==3) {
			double[] dIn = new double[3];
			dIn[0] = pntIn.getX();
			dIn[1] = pntIn.getY();
			dIn[2] = pntIn.getZ();
			
			double[] rot = calcRotatedPoint(dIn);
			return new Point3d( rot[0], rot[1], rot[2]);
		} else if (delegate.rows()==2) {
			double[] dIn = new double[2];
			dIn[0] = pntIn.getX();
			dIn[1] = pntIn.getY();
			
			double[] rot = calcRotatedPoint(dIn);
			return new Point3d( rot[0], rot[1], 0);			
		} else {
			throw new AnchorImpossibleSituationException();
		}
	}
	
	public double[] calcRotatedPoint( double[] pntIn ) {
		
		assert pntIn.length == delegate.rows();
		
		int numDim = pntIn.length;
		
		DoubleFactory2D factory = DoubleFactory2D.dense;
		DoubleMatrix2D matIn = factory.make(numDim,1);
		
		for (int i=0; i<numDim; i++) {
			matIn.set(i, 0, pntIn[i] );
		}
		
		DoubleMatrix2D matOut = delegate.zMult(matIn, null);
		
		double[] pntOut = new double[numDim];
		for (int i=0; i<numDim; i++) {
			pntOut[i] = matOut.get(i, 0); 
		}
		return pntOut;
	}
	
	public static RotationMatrix createFrom3Vecs( DoubleMatrix1D vec1,  DoubleMatrix1D vec2, DoubleMatrix1D vec3 ) {
		
		DoubleFactory2D factory = DoubleFactory2D.dense;
		DoubleMatrix2D mat = factory.make(3,3);
		
		mat.set(0, 0, vec1.get(0) );
		mat.set(1, 0, vec1.get(1) );
		mat.set(2, 0, vec1.get(2) );
		
		mat.set(0, 1, vec2.get(0) );
		mat.set(1, 1, vec2.get(1) );
		mat.set(2, 1, vec2.get(2) );
		
		mat.set(0, 2, vec3.get(0) );
		mat.set(1, 2, vec3.get(1) );
		mat.set(2, 2, vec3.get(2) );
		
		return new RotationMatrix(mat);
	}
	
	public RotationMatrix mult( RotationMatrix other ) {
		return new RotationMatrix( delegate.zMult(other.delegate, null) );
	}
	
	public Point3d column( int colNum ) {
		DoubleMatrix1D vector = delegate.viewColumn(colNum);
		return new Point3d( vector.get(0), vector.get(1), vector.get(2) );
	}
	
	public int getNumDim() {
		return delegate.columns();
	}
	
	public DoubleMatrix2D getMatrix() {
		return delegate;
	}
	
	public void multConstant( double value ) {
		delegate.assign( Functions.mult(value) );
	}
	
	public RotationMatrix transpose() {
		return new RotationMatrix( delegate.viewDice().copy() );
	}
	
	public RotationMatrix duplicate() {
		return new RotationMatrix( delegate.copy() );
	}

	@Override
	public String toString() {
		return delegate.toString();
	}
}
