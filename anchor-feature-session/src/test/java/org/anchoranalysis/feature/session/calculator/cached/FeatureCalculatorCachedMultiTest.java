/* (C)2020 */
package org.anchoranalysis.feature.session.calculator.cached;

import static org.junit.Assert.*;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.junit.Before;
import org.junit.Test;

public class FeatureCalculatorCachedMultiTest {

    private static final MockFeatureInput INPUT_1 = new MockFeatureInput("1");
    private static final MockFeatureInput INPUT_2 = new MockFeatureInput("2");
    private static final MockFeatureInput INPUT_1_REPEATED = new MockFeatureInput("1");

    private FeatureCalculatorCachedMulti<MockFeatureInput> cached;

    @Before
    public void setup() throws FeatureCalcException {
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
     * @throws FeatureCalcException
     */
    @Test
    public void testCacheContainsExisting() throws FeatureCalcException {

        assertCurrentLoad(0);

        assertInputMissing(INPUT_1);
        cached.calc(INPUT_1);
        assertInputExists(INPUT_1);
        assertCurrentLoad(1);

        cached.calc(INPUT_2);
        assertInputExists(INPUT_1);
        assertInputExists(INPUT_2);

        // Check that INPUT_1 remains in the cache
        cached.calc(INPUT_1_REPEATED);
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
