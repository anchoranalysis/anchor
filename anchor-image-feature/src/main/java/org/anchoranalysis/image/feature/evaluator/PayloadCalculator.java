/* (C)2020 */
package org.anchoranalysis.image.feature.evaluator;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.object.ObjectMask;

@FunctionalInterface
public interface PayloadCalculator {

    double calc(ObjectMask object) throws FeatureCalcException;
}
