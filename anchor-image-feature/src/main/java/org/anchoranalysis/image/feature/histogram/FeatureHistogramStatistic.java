/*-
 * #%L
 * anchor-image-feature
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
