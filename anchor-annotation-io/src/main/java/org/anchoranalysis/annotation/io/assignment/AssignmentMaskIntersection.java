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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ops.ObjMaskMerger;

public class AssignmentMaskIntersection implements Assignment {

	private ObjMask omLeft;
	private ObjMask omRight;
	
	private int numIntersectingPixels;
	private int numUnionPixels;
	private int sizeLeft;
	private int sizeRight;
	
	public AssignmentMaskIntersection(ObjMask omLeft, ObjMask omRight) {
		super();
		this.omLeft = omLeft;
		this.omRight = omRight;
		
		numIntersectingPixels = omLeft.countIntersectingPixels(omRight);
		numUnionPixels = ObjMaskMerger.merge(omLeft, omRight).numPixels();
		
		sizeLeft = omLeft.numPixels();
		sizeRight = omRight.numPixels();
	}
	
	@Override
	public int numPaired() {
		return isIntersectionPresent() ? 1 : 0;
	}

	@Override
	public int numUnassigned(boolean left) {
		return isIntersectionPresent() ? 0 : 1;
	}

	@Override
	public List<ObjMask> getListPaired(boolean left) {
		List<ObjMask> out = new ArrayList<>();
		if (isIntersectionPresent()) {
			addObjToList(out, left);
		}
		return out;
	}

	@Override
	public List<ObjMask> getListUnassigned(boolean left) {
		List<ObjMask> out = new ArrayList<>();
		if (!isIntersectionPresent()) {
			addObjToList(out, left);
		}
		return out;
	}

	@Override
	public List<String> createStatisticsHeaderNames() {
		return Arrays.asList("dice", "jaccard", "numIntersectingPixels", "numUnionPixels", "sizeLeft", "sizeRight");
	}

	@Override
	public List<TypedValue> createStatistics() {
		List<TypedValue> out = new ArrayList<>();
		addDouble( out, calcDice() );
		addDouble( out, calcJaccard() );

		addInt( out, numIntersectingPixels );
		addInt( out, numUnionPixels );
		addInt( out, sizeLeft );
		addInt( out, sizeRight );
		return out;
	}
	
	private static void addDouble( List<TypedValue> list, double val ) {
		list.add( new TypedValue( val , 4) );
	}
	
	private static void addInt( List<TypedValue> list, int val ) {
		list.add( new TypedValue( val) );
	}
		
	private void addObjToList( List<ObjMask> out, boolean left ) {
		if (left) {
			out.add(omLeft);
		} else {
			out.add(omRight);
		}
	}
	
	private boolean isIntersectionPresent() {
		return numIntersectingPixels > 0;
	}
	
	private double calcDice() {
		int num = 2 *  numIntersectingPixels;
		int dem = sizeLeft + sizeRight;
		return ((double) num)/dem;
	}
	
	private double calcJaccard() {
		return (double) numIntersectingPixels / numUnionPixels;
	}

}
