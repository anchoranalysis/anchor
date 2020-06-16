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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ops.ObjMaskMerger;

/**
 * Calculates statistics (DICE, Jaccard etc.) based upon corresponding two object-masks
 * 
 * @author Owen Feehan
 *
 */
public class AssignmentMaskIntersection implements Assignment {

	private ObjectMask omLeft;
	private ObjectMask omRight;
	
	private int numIntersectingPixels;
	private int numUnionPixels;
	private int sizeLeft;
	private int sizeRight;
	
	public AssignmentMaskIntersection(ObjectMask omLeft, ObjectMask omRight) {
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
	public List<ObjectMask> getListPaired(boolean left) {
		return multiplexObjIf(
			isIntersectionPresent(),
			left
		);
	}

	@Override
	public List<ObjectMask> getListUnassigned(boolean left) {
		return multiplexObjIf(
			!isIntersectionPresent(),
			left
		);
	}

	@Override
	public List<String> createStatisticsHeaderNames() {
		return Arrays.asList("dice", "jaccard", "numIntersectingPixels", "numUnionPixels", "sizeLeft", "sizeRight");
	}

	@Override
	public List<TypedValue> createStatistics() {
		WrappedTypeValueList out = new WrappedTypeValueList(4);
		out.add( calcDice(), calcJaccard() );
		out.add( numIntersectingPixels, numUnionPixels, sizeLeft, sizeRight );
		return out.asList();
	}
	
	private List<ObjectMask> multiplexObjIf( boolean cond, boolean left ) {
		if (cond) {
			return multiplexObj(left);
		} else {
			return Collections.emptyList();
		}
	}
	
	private List<ObjectMask> multiplexObj( boolean left ) {
		return Arrays.asList(
			left ? omLeft : omRight
		);
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
		return ((double) numIntersectingPixels) / numUnionPixels;
	}
}
