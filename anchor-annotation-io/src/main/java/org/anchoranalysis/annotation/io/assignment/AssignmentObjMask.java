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
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;

public class AssignmentObjMask implements Assignment {

	private List<ObjectMask> listUnassignedLeft = new ArrayList<>();
	private List<ObjectMask> listUnassignedRight = new ArrayList<>();
	private List<PairObjMask> listPairs = new ArrayList<>();
	
	/** The headers needed to create statistics */
	@Override
	public List<String> createStatisticsHeaderNames() {
		
		List<String> headerNames = new ArrayList<>();
		headerNames.add("percentMatchesInAnnotation");
		headerNames.add("percentMatchesInResult");
		
		headerNames.add("matches");
		headerNames.add("unmatchedAnnotation");
		headerNames.add("numItemsInAnnotation");
		

		headerNames.add("unmatchedResult");
		headerNames.add("numItemsInResult");
		
		headerNames.add("meanOverlapFromPaired");
		headerNames.add("minOverlapFromPaired");
		headerNames.add("maxOverlapFromPaired");
		return headerNames;
	}
	
	@Override
	public List<TypedValue> createStatistics() {
		Elements rowElements = new Elements();
		rowElements.add( percentLeftMatched() );
		rowElements.add( percentRightMatched() );
		
		rowElements.add( numPaired() );
		
		
		rowElements.add( numUnassignedLeft() );
		rowElements.add( leftSize() );
		
		
		rowElements.add( numUnassignedRight() );
		rowElements.add( rightSize() );
		
		rowElements.add( meanOverlapFromPaired() );
		rowElements.add( minOverlapFromPaired() );
		rowElements.add( maxOverlapFromPaired() );
		
		return rowElements.getList();	
	}
	

	private static class Elements {
		private List<TypedValue> delegate = new ArrayList<>();
		
		public void add( double val ) {
			delegate.add( new TypedValue( val, 2 ));
		}

		public List<TypedValue> getList() {
			return delegate;
		}
	}

	public void removeTouchingBorderXY( ImageDim sd ) {
		removeTouchingBorderXYObjMask( sd, listUnassignedLeft );
		removeTouchingBorderXYObjMask( sd, listUnassignedRight );
		removeTouchingBorderXYPairObjMask( sd, listPairs );
	}
	
	@Override
	public List<ObjectMask> getListPaired( boolean left ) {
		List<ObjectMask> out = new ArrayList<>();
		for( PairObjMask om : listPairs) {
			out.add( om.getMultiplex( left ) );
		}
		return out;
	}

	@Override
	public List<ObjectMask> getListUnassigned( boolean left ) {
		if (left) {
			return getListUnassignedLeft();
		} else {
			return getListUnassignedRight();
		}
	}
	
	public double meanOverlapFromPaired() {
		return sumOverlapFromPaired() / listPairs.size();
	}
	
	public double sumOverlapFromPaired() {
		double sum = 0.0;
		for( PairObjMask om : listPairs) {
			sum += om.getOverlapRatio();
		}
		return sum;
	}

	public double minOverlapFromPaired() {
		double min = Double.NaN;
		for( PairObjMask om : listPairs) {
			if (Double.isNaN(min) || om.getOverlapRatio()<min) {
				min = om.getOverlapRatio();
			}
		}
		return min;
	}

	public double maxOverlapFromPaired() {
		double max = Double.NaN;
		for( PairObjMask om : listPairs) {
			if (Double.isNaN(max) || om.getOverlapRatio()>max) {
				max = om.getOverlapRatio();
			}
		}
		return max;
	}
	
	public List<ObjectMask> getListUnassignedLeft() {
		return listUnassignedLeft;
	}

	public List<ObjectMask> getListUnassignedRight() {
		return listUnassignedRight;
	}

	public void addLeftObj( ObjectMask om ) {
		listUnassignedLeft.add(om);
	}

	public void addLeftObjs( ObjectMaskCollection om ) {
		listUnassignedLeft.addAll(om.asList());
	}
	
	public void addRightObj( ObjectMask om ) {
		listUnassignedRight.add(om);
	}
	
	public void addRightObjs( ObjectMaskCollection om ) {
		listUnassignedRight.addAll(om.asList());
	}
	
	public void addPair( ObjectMask om1, ObjectMask om2, double overlapRatio ) {
		listPairs.add( new PairObjMask(om1, om2, overlapRatio) );
	}
	
	public double percentLeftMatched() { 
		int size = leftSize();
		if (size==0) {
			return Double.NaN;
		}
		return ((double) numPaired())*100 / size;
	}

	public double percentRightMatched() {
		int size = rightSize();
		if (size==0) {
			return Double.NaN;
		}
		return ((double) numPaired())*100 / size;
	}
	
	@Override
	public int numPaired() {
		return listPairs.size();
	}
	

	@Override
	public int numUnassigned(boolean left) {
		if (left) {
			return numUnassignedLeft();
		} else {
			return numUnassignedRight();
		}
	}
	
	public int numUnassignedLeft() {
		return listUnassignedLeft.size();
	}

	public int numUnassignedRight() {
		return listUnassignedRight.size();
	}
	
	public int leftSize() {
		return listPairs.size() + listUnassignedLeft.size();
	}
	
	public int rightSize() {
		return listPairs.size() + listUnassignedRight.size();
	}
		
	private static void removeTouchingBorderXYObjMask( ImageDim sd, List<ObjectMask> list ) {
		Iterator<ObjectMask> itr = list.iterator();
		while( itr.hasNext() ) {
			ObjectMask om = itr.next();
			if (om.getBoundingBox().atBorderXY(sd)) {
				itr.remove();
			}
		}
	}
	
	private static void removeTouchingBorderXYPairObjMask( ImageDim sd, List<PairObjMask> list ) {
		Iterator<PairObjMask> itr = list.iterator();
		while( itr.hasNext() ) {
			PairObjMask pom = itr.next();
			if (pom.atBorderXY(sd)) {
				itr.remove();
			}
		}
	}

}
