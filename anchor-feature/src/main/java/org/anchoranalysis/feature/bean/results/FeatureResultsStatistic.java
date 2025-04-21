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
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.results.ResultsVectorList;

/**
 * Base class for an instance of {@link FeatureResults} that calculating a statistic across all
 * results in the {@link ResultsVectorList} for a particular feature-value.
 *
 * @author Owen Feehan
 */
public abstract class FeatureResultsStatistic extends FeatureResults {

    // START BEAN PROPERTIES
    /** The name of the feature, whose results will provide the statistic. */
    @BeanField @Getter @Setter private String id = "";

    // END BEAN PROPERTIES

    @Override
    public double calculate(FeatureInputResults input) throws FeatureCalculationException {

        try {
            int index = input.getFeatureNameIndex().indexOf(id);

            ResultsVectorList results = input.getResults();

            if (results.isEmpty()) {
                throw new FeatureCalculationException(
                        "No feature-values exist, so this operation is undefined");
            }

            return statisticFromFeatureValue(extractResultsForFeature(results, index));

        } catch (GetOperationFailedException e) {
            throw new FeatureCalculationException(e);
        }
    }

    /**
     * Calculates the statistic for a given list of result-value.
     *
     * @param values the values to calculate the statistic for.
     * @return the calculated statistic.
     * @throws FeatureCalculationException if the calculation cannot complete successfully.
     */
    protected abstract double statisticFromFeatureValue(DoubleArrayList values)
            throws FeatureCalculationException;

    /** Extracts the results from a {@link ResultsVectorList} for one particular feature. */
    private static DoubleArrayList extractResultsForFeature(ResultsVectorList results, int index) {
        DoubleArrayList featureValues = new DoubleArrayList();
        results.stream().forEach(resultSingle -> featureValues.add(resultSingle.get(index)));
        return featureValues;
    }
}
