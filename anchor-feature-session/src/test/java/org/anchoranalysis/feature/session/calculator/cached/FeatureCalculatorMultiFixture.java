/* (C)2020 */
package org.anchoranalysis.feature.session.calculator.cached;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;

class FeatureCalculatorMultiFixture {

    /** Creates a feature-calculator than returns a constant result */
    public static <T extends FeatureInput> FeatureCalculatorMulti<T> createFeatureCalculator(
            ResultsVector rv) throws FeatureCalcException {

        @SuppressWarnings("unchecked")
        FeatureCalculatorMulti<T> calculator = mock(FeatureCalculatorMulti.class);
        when(calculator.calc(any())).thenReturn(rv);
        when(calculator.calcSuppressErrors(any(), any())).thenReturn(rv);
        return calculator;
    }
}
