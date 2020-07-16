/* (C)2020 */
package org.anchoranalysis.image.feature.histogram;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.histogram.Histogram;

public class Mean extends FeatureHistogramStatistic {

    @Override
    protected double calcStatisticFrom(Histogram histogram) throws FeatureCalcException {
        try {
            return histogram.mean();
        } catch (OperationFailedException e) {
            throw new FeatureCalcException(e);
        }
    }
}
