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
import java.util.Iterator;
import java.util.List;
import java.util.stream.DoubleStream;

import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Pairs objects in left with objects in right
 * 
 * <p>Several statistics based upon overlap, the number of pairs, the number of unassigned objects are derived.</p>
 * 
 * @author Owen Feehan
 *
 */
public class AssignmentOverlapFromPairs implements Assignment {

	private List<ObjectMask> listUnassignedLeft = new ArrayList<>();
	private List<ObjectMask> listUnassignedRight = new ArrayList<>();
	private List<ObjectMaskPair> listPairs = new ArrayList<>();
	
	/** The headers needed to create statistics */
	@Override
	public List<String> createStatisticsHeaderNames() {
		return Arrays.asList(
			"percentMatchesInAnnotation",
			"percentMatchesInResult",
		
			"matches",
			"unmatchedAnnotation",
			"numItemsInAnnotation",
		
			"unmatchedResult",
			"numItemsInResult",
		
			"meanOverlapFromPaired",
			"minOverlapFromPaired",
			"maxOverlapFromPaired"
		);
	}
	
	@Override
	public List<TypedValue> createStatistics() {
		WrappedTypeValueList elements = new WrappedTypeValueList(2);
		
		elements.add(
			percentLeftMatched(),
			percentRightMatched()
		);
		
		elements.add(
			numPaired(),
			numUnassignedLeft(),
			leftSize(),
			numUnassignedRight(),
			rightSize()
		);
		
		elements.add(
			meanOverlapFromPaired(),
			minOverlapFromPaired(),
			maxOverlapFromPaired()
		);
		
		return elements.asList();	
	}
	
	public void removeTouchingBorderXY( ImageDimensions sd ) {
		removeTouchingBorderXYObjects( sd, listUnassignedLeft );
		removeTouchingBorderXYObjects( sd, listUnassignedRight );
		removeTouchingBorderXYPairObjects( sd, listPairs );
	}
	
	@Override
	public List<ObjectMask> getListPaired( boolean left ) {
		return FunctionalList.mapToList(
			listPairs,
			object -> object.getMultiplex( left )
		);
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
		for( ObjectMaskPair om : listPairs) {
			sum += om.getOverlapRatio();
		}
		return sum;
	}

	public double minOverlapFromPaired() {
		return pairsOverlapStream().min().orElse(Double.NaN);
	}

	public double maxOverlapFromPaired() {
		return pairsOverlapStream().max().orElse(Double.NaN);
	}
	
	private DoubleStream pairsOverlapStream() {
		return listPairs.stream().mapToDouble(
			ObjectMaskPair::getOverlapRatio
		);
	}
	
	public List<ObjectMask> getListUnassignedLeft() {
		return listUnassignedLeft;
	}

	public List<ObjectMask> getListUnassignedRight() {
		return listUnassignedRight;
	}

	public void addLeftObject( ObjectMask object ) {
		listUnassignedLeft.add(object);
	}

	public void addLeftObjects( ObjectCollection objects ) {
		listUnassignedLeft.addAll(objects.asList());
	}
	
	public void addRightObject( ObjectMask object ) {
		listUnassignedRight.add(object);
	}
	
	public void addRightObjects( ObjectCollection objects ) {
		listUnassignedRight.addAll(objects.asList());
	}
	
	public void addPair( ObjectMask object1, ObjectMask object2, double overlapRatio ) {
		listPairs.add( new ObjectMaskPair(object1, object2, overlapRatio) );
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
		
	private static void removeTouchingBorderXYObjects( ImageDimensions sd, List<ObjectMask> list ) {
		Iterator<ObjectMask> itr = list.iterator();
		while( itr.hasNext() ) {
			if (itr.next().getBoundingBox().atBorderXY(sd)) {
				itr.remove();
			}
		}
	}
	
	private static void removeTouchingBorderXYPairObjects( ImageDimensions sd, List<ObjectMaskPair> list ) {
		Iterator<ObjectMaskPair> itr = list.iterator();
		while( itr.hasNext() ) {
			ObjectMaskPair pom = itr.next();
			if (pom.atBorderXY(sd)) {
				itr.remove();
			}
		}
	}
}
