package org.anchoranalysis.image.feature.evaluator;

import java.util.function.UnaryOperator;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A {@link FeatureCalculatorMulti} with associated feature-names
 * 
 * @author Owen Feehan
 *
 * @param <T> feature input type
 */
@Value @AllArgsConstructor
public class NamedFeatureCalculatorMulti<T extends FeatureInput> {

    private FeatureCalculatorMulti<T> calculator;
    private FeatureNameList names;
    
    public NamedFeatureCalculatorMulti<T> mapCalculator( UnaryOperator<FeatureCalculatorMulti<T>> mapOperator ) {
        return new NamedFeatureCalculatorMulti<>( mapOperator.apply(calculator), names );
    }
}
