package ch.ethz.biol.cell.mpp.mark;

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

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;

class TensorUtilities {
	
	public static double squared( double val ) {
		return Math.pow(val,  2);
	}

	/** A two element array, where the second value is 0.0 */
	public static double[] twoElementArray( double firstVal ) {
		return twoElementArray( firstVal, 0 );
	}
	
	public static double[] twoElementArray( double firstVal, double secondVal ) {
		return new double[]{ firstVal, secondVal };
	}
	
	public static double[] threeElementArray( double firstVal, double secondVal, double thirdVal ) {
		return new double[]{ firstVal, secondVal, thirdVal };
	}
	
	public static DoubleMatrix1D twoElementMatrix(double x, double y) {
		DoubleMatrix1D out = DoubleFactory1D.dense.make(2);
		out.set(0, x);
		out.set(1, y);
		return out;
	}
	
	public static DoubleMatrix1D threeElementMatrix(double x, double y, double z) {
		DoubleMatrix1D out = DoubleFactory1D.dense.make(3);
		out.set(0, x);
		out.set(1, y);
		out.set(2, z);
		return out;
	}
}
