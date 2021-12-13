package org.anchoranalysis.feature.bean.results;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;

/**
 * The <b>minimum</b> of the results from a particular feature.
 *
 * @author Owen Feehan
 */
public class Minimum extends FeatureResultsStatistic {

    @Override
    protected double statisticFromFeatureValue(DoubleArrayList values)
            throws FeatureCalculationException {
        return Descriptive.min(values);
    }
}
