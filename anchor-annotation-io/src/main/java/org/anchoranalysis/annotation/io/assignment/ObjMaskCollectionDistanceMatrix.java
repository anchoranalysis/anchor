package org.anchoranalysis.annotation.io.assignment;

/*-
 * #%L
 * anchor-annotation-io
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

import org.anchoranalysis.image.objmask.ObjMaskCollection;

public class ObjMaskCollectionDistanceMatrix {

	// A two-dimensional array mapping objs1 to objs2
	private double[][] distanceArr;
	
	private ObjMaskCollection objs1;
	
	private ObjMaskCollection objs2;

	public ObjMaskCollectionDistanceMatrix(ObjMaskCollection objs1,
			ObjMaskCollection objs2, double[][] distanceArr) {
		super();
		this.objs1 = objs1;
		this.objs2 = objs2;
		this.distanceArr = distanceArr;
		
		assert( objs1.size()>0 );
		assert( objs2.size()>0 );
		assert( distanceArr.length == objs1.size() );
		assert( distanceArr[0].length == objs2.size() );
	}
	
	public double getDistance( int indx1, int indx2 ) {
		return distanceArr[indx1][indx2];
	}

	public double[][] getDistanceArr() {
		return distanceArr;
	}

	public ObjMaskCollection getObjs1() {
		return objs1;
	}

	public ObjMaskCollection getObjs2() {
		return objs2;
	}

	public int sizeObjs1() {
		return objs1.size();
	}
	
	public int sizeObjs2() {
		return objs2.size();
	}
}
