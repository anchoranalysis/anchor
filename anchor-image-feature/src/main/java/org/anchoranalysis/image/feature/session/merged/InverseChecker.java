package org.anchoranalysis.image.feature.session.merged;

/*-
 * #%L
 * anchor-plugin-mpp-experiment
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

import java.util.function.Supplier;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.image.feature.objmask.pair.FeatureInputPairObjs;

class InverseChecker {

	private boolean includeFirst;
	private boolean includeSecond;
	private int numImageFeatures;
	private int numSingleFeatures;
	private Supplier<FeatureNameList> featureNamesSupplier;
		
	public InverseChecker(boolean includeFirst, boolean includeSecond, int numImageFeatures, int numSingleFeatures,
			Supplier<FeatureNameList> featureNamesSupplier) {
		super();
		this.includeFirst = includeFirst;
		this.includeSecond = includeSecond;
		this.numImageFeatures = numImageFeatures;
		this.numSingleFeatures = numSingleFeatures;
		this.featureNamesSupplier = featureNamesSupplier;
	}


	public void checkInverseEqual(ResultsVector rv, ResultsVector rvInverse, FeatureInputPairObjs params) throws FeatureCalcException {
		StringBuilder sb = new StringBuilder();
		if (!isInverseEqual(rv, rvInverse, sb)) {
			throw new FeatureCalcException(
				String.format("Feature values are not equal to the inverse for %s:%n%s", params, sb.toString() )
			);
		}
	}
	
	/**
	 * Generates a multi-line string describing which values are different.
	 * 
	 * Values are compared. Errors are treated as nulls.
	 * 
	 * @param other
	 * @return
	 * @throws FeatureCalcException 
	 */
	private boolean isInverseEqual(
		ResultsVector rv1,
		ResultsVector rv2,
		StringBuilder sb
	) throws FeatureCalcException {
		
		if (includeFirst!=includeSecond) {
			throw new FeatureCalcException("Cannot compare with inverse, as includeFirst!=includeSecond");
		}
		
		if (rv1.length()!=rv2.length()) {
			sb.append( String.format("lengths are different: %d vs %d", rv1.length(), rv2.length()) );
			return false;
		}
		
		if (includeFirst) {
			rv2 = switchFirstAndSecond(rv2, numImageFeatures, numSingleFeatures );
		}
		
		// we initialise the f
		FeatureNameList featureNames = null; 
		
		boolean allEqual = true;
		
		for( int i=0; i<rv1.length(); i++) {
			
			Double val1 = rv1.getDoubleOrNull(i);
			Double val2 = rv2.getDoubleOrNull(i);
			
			if (!areDoubleOrNullEquals(val1,val2)) {
				
				// Lazy creation, as we only need if an error occurs
				if (allEqual==true) {
					featureNames = featureNamesSupplier.get();
					allEqual = false;
				}
				
				String featName = featureNames.get(i);
				
				String diffFeat = String.format("Feature %s is different:\t%f\tvs\t%f%n", featName, val1, val2);
				sb.append(diffFeat);
			}
		}
		
		return allEqual;
	}
	
	
	private static boolean areDoubleOrNullEquals( Double val1, Double val2 ) {
		if (val1==null) {
			return (val2==null);
		}
		if (val2==null) {
			return (val1==null);
		}
		return val1.equals(val2);
	}
	

	/**
	 * Create a ResultsVector where the values for first and second are switched
	 * @param in
	 * @return
	 */
	private static ResultsVector switchFirstAndSecond( ResultsVector in, int numImageFeatures, int numSingleFeatures ) {
		ResultsVector out = new ResultsVector( in.length() );
		
		int cnt = 0;
		
		// Image features
		out.copyFrom(0, numImageFeatures, in, 0);
		
		cnt += numImageFeatures;
		
		// First features
		out.copyFrom(cnt, numSingleFeatures, in, cnt + numSingleFeatures );
		
		// Second features
		out.copyFrom(cnt + numSingleFeatures, numSingleFeatures, in, cnt );
		
		cnt += (numSingleFeatures*2);
		
		// Copy the rest unchanged
		out.copyFrom(cnt, in.length()-cnt, in, cnt);
		
		return out;
	}
}
