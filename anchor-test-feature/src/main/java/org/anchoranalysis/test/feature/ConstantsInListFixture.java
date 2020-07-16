/* (C)2020 */
package org.anchoranalysis.test.feature;

import static org.junit.Assert.assertTrue;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.bean.operator.Constant;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;

public class ConstantsInListFixture {

    private static double f1Value = 1.0;
    private static double f2Value = 2.0;
    private static double f3Value = 3.0;

    private static double eps = 1e-16;

    private ConstantsInListFixture() {}

    /** creates a feature-list associated with the fixture */
    public static <T extends FeatureInput> FeatureList<T> create() {
        return FeatureListFactory.from(
                constantFeatureFor(f1Value),
                constantFeatureFor(f2Value),
                constantFeatureFor(f3Value));
    }

    /**
     * checks that a result-vector has the results we expect from the feature-list associated with
     * this fixture
     */
    public static void checkResultVector(ResultsVector rv) {
        assertTrue(rv.equalsPrecision(eps, f1Value, f2Value, f3Value));
    }

    private static <T extends FeatureInput> Feature<T> constantFeatureFor(double val) {
        return new Constant<>(val);
    }
}
