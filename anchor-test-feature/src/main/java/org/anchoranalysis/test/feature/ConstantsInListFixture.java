package org.anchoranalysis.test.feature;

/*-
 * #%L
 * anchor-test-feature
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

import static org.junit.Assert.assertTrue;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.operator.Constant;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

public class ConstantsInListFixture {

	private static double f1Value = 1.0;
	private static double f2Value = 2.0;
	private static double f3Value = 3.0;

	private static double eps = 1e-16;
	
	/** creates a feature-list associated with the fixture */
	public static <T extends FeatureInput> FeatureList<T> create() {
		Feature<T> f1 = new Constant<>(f1Value);
		Feature<T> f2 = new Constant<>(f2Value);
		Feature<T> f3 = new Constant<>(f3Value);
		
		FeatureList<T> list = new FeatureList<>();
		list.add(f1);
		list.add(f2);
		list.add(f3);
		return list;
	}
		
	/** checks that a result-vector has the results we expect from the feature-list associated with this fixture */
	public static void checkResultVector( ResultsVector rv ) {
		assertTrue( rv.equalsPrecision(eps, f1Value, f2Value, f3Value ) );
	}
}
