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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureEvaluator;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectCollection;
import org.anchoranalysis.math.optimization.HungarianAlgorithm;

public class AssignmentObjMaskFactory {
	
	private FeatureEvaluator<FeatureInputPairObjects> featureEvaluator;
	private boolean useMIP;
	
	// Remember the cost matrix, in case we need it later
	private ObjMaskCollectionDistanceMatrix cost;
	
	public AssignmentObjMaskFactory(FeatureEvaluator<FeatureInputPairObjects> featureEvaluator, boolean useMIP ) {
		super();
		this.featureEvaluator = featureEvaluator;
		this.useMIP = useMIP;
	}
	
	public ObjMaskCollectionDistanceMatrix getCost() {
		return cost;
	}

	public AssignmentOverlapFromPairs createAssignment(
		ObjectCollection leftObjs,
		ObjectCollection rightObjs,
		double maxAcceptedCost,
		ImageDim dim
	) throws FeatureCalcException {

		// Empty annotations
		if (leftObjs.size()==0) {
			// N.B. Also sensibly handles the case where annotationObjs and resultObjs are both empty
			AssignmentOverlapFromPairs out = new AssignmentOverlapFromPairs();
			out.addRightObjs(rightObjs);
			return out;
		}
		
		// Empty result objects
		if (rightObjs.size()==0) {
			AssignmentOverlapFromPairs out = new AssignmentOverlapFromPairs();
			out.addLeftObjs(leftObjs);
			return out;
		}
		
		cost = createCostMatrix(
			maybeProject(leftObjs),
			maybeProject(rightObjs),
			dim
		);
		
		// Non empty both
		
		HungarianAlgorithm ha = new HungarianAlgorithm(cost.getDistanceArr());
		int[] assign = ha.execute();
		
		AssignmentOverlapFromPairs ass = createAssignment(cost, assign, maxAcceptedCost);
		if (ass.rightSize()!=rightObjs.size()) {
			throw new FeatureCalcException("assignment.rightSize() does not equal the number of the right objects. This should never happen!");
		}
		return ass;
		
	}
	
	private ObjectCollection maybeProject(ObjectCollection objs) {
		if (useMIP) {
			return objs.stream().map(ObjectMask::maxIntensityProjection);
		} else {
			return objs;
		}
	}
		
	private ObjMaskCollectionDistanceMatrix createCostMatrix( ObjectCollection annotation, ObjectCollection result, ImageDim dim) throws FeatureCalcException {

		FeatureCalculatorSingle<FeatureInputPairObjects> session;
		try {
			session = featureEvaluator.createAndStartSession();
		} catch (OperationFailedException e) {
			throw new FeatureCalcException(e);
		}
		
		NRGStackWithParams nrgStack = new NRGStackWithParams(dim);
		
		double[][] outArr = new double[annotation.size()][result.size()];
		
		for( int i=0; i<annotation.size(); i++) {
			ObjectMask objA = annotation.get(i);
			for( int j=0; j<result.size(); j++) {
				
				ObjectMask objR = result.get(j);
	
				double costObjs = session.calc(
					paramsFor(objA, objR, nrgStack)
				);
				outArr[i][j] = costObjs;
				
				if (Double.isNaN(costObjs)) {
					throw new FeatureCalcException("Cost is NaN");
				}
			}
		}
		
		try {
			return new ObjMaskCollectionDistanceMatrix( annotation, result, outArr );
		} catch (CreateException e) {
			throw new FeatureCalcException(e);
		}
	}
	
	private static FeatureInputPairObjects paramsFor( ObjectMask objMask1, ObjectMask objMask2, NRGStackWithParams nrgStack) {
		return new FeatureInputPairObjects(
			objMask1,
			objMask2,
			Optional.of(nrgStack)
		);
	}
	
	private static AssignmentOverlapFromPairs createAssignment( ObjMaskCollectionDistanceMatrix costMatrix, int[] assign, double maxAcceptedCost) {
		
		AssignmentOverlapFromPairs assignment = new AssignmentOverlapFromPairs();
		
		ObjectCollection leftObjs = costMatrix.getObjs1();
		ObjectCollection rightObjs = costMatrix.getObjs2();
		
		Set<Integer> setAnnotationObjs = new HashSet<>();
		for( int i=0; i<leftObjs.size(); i++) {
			setAnnotationObjs.add(i);
		}
		
		Set<Integer> setResultObjs = new HashSet<>();
		for( int i=0; i<rightObjs.size(); i++) {
			setResultObjs.add(i);
		}
		
		for( int i=0; i<assign.length; i++) {
			int ref = assign[i];
			if( ref!=-1) {
				double cost = costMatrix.getDistance(i,ref); 
				if(cost < maxAcceptedCost) {
					assignment.addPair(leftObjs.get(i), rightObjs.get(ref), 1.0 - cost );
					setAnnotationObjs.remove(i);
					setResultObjs.remove(ref);
				}
			}
		}
		
		for( int i : setAnnotationObjs) {
			assignment.addLeftObj( leftObjs.get(i) );
		}
		
		for( int i : setResultObjs) {
			assignment.addRightObj( rightObjs.get(i) );
		}
		
		return assignment;
	}
}
