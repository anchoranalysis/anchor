/*-
 * #%L
 * anchor-feature-session
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

package org.anchoranalysis.feature.session.calculator.multi;

import static org.junit.Assert.*;

import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.NamedFeatureCalculateException;
import org.anchoranalysis.feature.results.ResultsVector;
import org.junit.Before;
import org.junit.Test;

public class FeatureCalculatorCachedMultiTest {

    private static final MockFeatureInput INPUT_1 = new MockFeatureInput("1");
    private static final MockFeatureInput INPUT_2 = new MockFeatureInput("2");
    private static final MockFeatureInput INPUT_1_REPEATED = new MockFeatureInput("1");

    private FeatureCalculatorCachedMulti<MockFeatureInput> cached;

    @Before
    public void setup() throws FeatureCalculationException {
        cached =
                new FeatureCalculatorCachedMulti<>(
                        FeatureCalculatorMultiFixture.createFeatureCalculator(
                                new ResultsVector(1) // Result is irrelevant
                                ));
    }

    /**
     * Checks the cache contains an item, when a new item with different object but identical to an
     * existing one is passed
     *
     * @throws NamedFeatureCalculateException
     */
    @Test
    public void testCacheContainsExisting() throws NamedFeatureCalculateException {

        assertCurrentLoad(0);

        assertInputMissing(INPUT_1);
        cached.calculate(INPUT_1);
        assertInputExists(INPUT_1);
        assertCurrentLoad(1);

        cached.calculate(INPUT_2);
        assertInputExists(INPUT_1);
        assertInputExists(INPUT_2);

        // Check that INPUT_1 remains in the cache
        cached.calculate(INPUT_1_REPEATED);
        assertInputExists(INPUT_1);

        // There should be 2 items in the cache at the end
        assertCurrentLoad(2);
    }

    private void assertCurrentLoad(int expectedLoad) {
        assertEquals(expectedLoad, cached.sizeCurrentLoad());
    }

    private void assertInputExists(MockFeatureInput input) {
        assertTrue(cached.has(input));
    }

    private void assertInputMissing(MockFeatureInput input) {
        assertTrue(!cached.has(input));
    }
}
