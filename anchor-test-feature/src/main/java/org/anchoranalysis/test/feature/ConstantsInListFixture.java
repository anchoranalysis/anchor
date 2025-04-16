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

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.bean.operator.Constant;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.results.ResultsVector;

/**
 * A test fixture for creating and validating feature lists with constant values.
 *
 * <p>This class provides utility methods to create a feature list containing three constant
 * features with predefined values (1.0, 2.0, and 3.0). It also offers a method to check if a
 * results vector matches these expected constant values.
 *
 * <p>This fixture is useful for testing scenarios where you need a consistent set of features with
 * known constant values, allowing for predictable and easily verifiable results.
 *
 * <p>Usage example:
 *
 * <pre>
 * FeatureList&lt;MyFeatureInput&gt; featureList = ConstantsInListFixture.create();
 * // Use the feature list in your test
 * ResultsVector results = // ... calculate results using the feature list
 * ConstantsInListFixture.checkResultsVector(results);
 * </pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantsInListFixture {

    private static final double F1_VALUE = 1.0;
    private static final double F2_VALUE = 2.0;
    private static final double F3_VALUE = 3.0;

    private static final double EPS = 1e-16;

    /**
     * Creates a feature-list associated with the fixture.
     *
     * @return a newly created feature-list.
     */
    public static <T extends FeatureInput> FeatureList<T> create() {
        return FeatureListFactory.from(
                constantFeatureFor(F1_VALUE),
                constantFeatureFor(F2_VALUE),
                constantFeatureFor(F3_VALUE));
    }

    /**
     * Checks that a result-vector has the results we expect from the feature-list associated with
     * this fixture.
     *
     * @param vector the results-vector to check.
     */
    public static void checkResultsVector(ResultsVector vector) {
        assertTrue(vector.equalsPrecision(EPS, F1_VALUE, F2_VALUE, F3_VALUE));
    }

    private static <T extends FeatureInput> Feature<T> constantFeatureFor(double value) {
        return new Constant<>(value);
    }
}
