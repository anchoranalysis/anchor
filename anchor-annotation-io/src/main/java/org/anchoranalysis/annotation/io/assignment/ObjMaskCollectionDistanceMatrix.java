package org.anchoranalysis.annotation.io.assignment;

/*-
 * #%L
 * anchor-annotation-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.object.ObjectCollection;

public class ObjMaskCollectionDistanceMatrix {

	// A two-dimensional array mapping objs1 to objs2
	private double[][] distanceArr;
	
	private ObjectCollection objs1;
	private ObjectCollection objs2;

	public ObjMaskCollectionDistanceMatrix(
		ObjectCollection objs1,
		ObjectCollection objs2,
		double[][] distanceArr
	) throws CreateException {
		super();
		
		this.objs1 = objs1;
		this.objs2 = objs2;
		this.distanceArr = distanceArr;
		
		if (objs1.isEmpty()) {
			throw new CreateException("objs1 must be non-empty");
		}
		
		if (objs2.isEmpty()) {
			throw new CreateException("objs2 must be non-empty");
		}
		
		if ((distanceArr.length != objs1.size()) || distanceArr[0].length != objs2.size()) {
			throw new CreateException("The distance-array has incorrect dimensions to match the objects");
		}		
	}
	
	public double getDistance( int indx1, int indx2 ) {
		return distanceArr[indx1][indx2];
	}

	public double[][] getDistanceArr() {
		return distanceArr;
	}

	public ObjectCollection getObjs1() {
		return objs1;
	}

	public ObjectCollection getObjs2() {
		return objs2;
	}

	public int sizeObjs1() {
		return objs1.size();
	}
	
	public int sizeObjs2() {
		return objs2.size();
	}
}
