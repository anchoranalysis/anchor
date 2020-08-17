/*-
 * #%L
 * anchor-test-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.test.feature;

import static org.junit.Assert.assertTrue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.bean.operator.Constant;
import org.anchoranalysis.feature.calculate.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantsInListFixture {

    private static final double F1_VALUE = 1.0;
    private static final double F2_VALUE = 2.0;
    private static final double F3_VALUE = 3.0;

    private static final double EPS = 1e-16;

    /** creates a feature-list associated with the fixture */
    public static <T extends FeatureInput> FeatureList<T> create() {
        return FeatureListFactory.from(
                constantFeatureFor(F1_VALUE),
                constantFeatureFor(F2_VALUE),
                constantFeatureFor(F3_VALUE));
    }

    /**
     * checks that a result-vector has the results we expect from the feature-list associated with
     * this fixture
     */
    public static void checkResultVector(ResultsVector resultsVector) {
        assertTrue(resultsVector.equalsPrecision(EPS, F1_VALUE, F2_VALUE, F3_VALUE));
    }

    private static <T extends FeatureInput> Feature<T> constantFeatureFor(double value) {
        return new Constant<>(value);
    }
}
