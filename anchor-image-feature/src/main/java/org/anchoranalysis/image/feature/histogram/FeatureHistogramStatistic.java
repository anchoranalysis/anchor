/* (C)2020 */
package org.anchoranalysis.image.feature.histogram;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.feature.bean.FeatureHistogram;
import org.anchoranalysis.image.histogram.Histogram;

public abstract class FeatureHistogramStatistic extends FeatureHistogram {

    // START BEAN PROPERTIES
    /**
     * If true, then an exception is thrown if the histogram is empty, otherwise {@link
     * #valueIfEmpty is returned}.
     */
    @BeanField @Getter @Setter private boolean exceptionIfEmpty = true;

    /** The value to return iff {@link #exceptionifEmpty} is false */
    @BeanField @Getter @Setter private double valueIfEmpty = 0;
    // END BEAN PROPERTIES

    @Override
    public double calc(SessionInput<FeatureInputHistogram> input) throws FeatureCalcException {
        Histogram histogram = input.get().getHistogram();

        if (histogram.isEmpty()) {

            if (exceptionIfEmpty) {
                throw new FeatureCalcException(
                        "Histogram is empty, so abandoning feature calculation.");
            } else {
                return valueIfEmpty;
            }
        }

        return calcStatisticFrom(histogram);
    }

    protected abstract double calcStatisticFrom(Histogram histogram) throws FeatureCalcException;
}
