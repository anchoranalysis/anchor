/*-
 * #%L
 * anchor-plugin-mpp-feature
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

package org.anchoranalysis.feature.bean.results;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * A <b>quantile</b> of the results from a particular feature.
 *
 * @author Owen Feehan
 */
public class Quantile extends FeatureResultsStatistic {

    // START BEAN PROPERTIES
    /** The quantile. */
    @BeanField @Getter @Setter private double quantile = 0; // NOSONAR

    /** If true, the quantile is interpreted as a percentage rather than a decimal. */
    @BeanField @Getter @Setter private boolean asPercentage = false;

    // END BEAN PROPERTIES

    @Override
    protected double statisticFromFeatureValue(DoubleArrayList values) {
        values.sort();

        double quantileFinal = asPercentage ? quantile / 100 : quantile;
        return Descriptive.quantile(values, quantileFinal);
    }
}
