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
import org.anchoranalysis.feature.calculate.results.ResultsVectorCollection;
import org.anchoranalysis.feature.input.FeatureInputResults;

public abstract class FeatureResultsFromIndex extends FeatureResults {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String id = "";
    // END BEAN PROPERTIES

    @Override
    public double calculate(FeatureInputResults params) throws FeatureCalculationException {

        try {
            int index = params.getFeatureNameIndex().indexOf(id);

            ResultsVectorCollection rvc = params.getResultsVectorCollection();

            if (rvc.size() == 0) {
                throw new FeatureCalculationException(
                        "No feature-values exist, so this operation is undefined");
            }

            return statisticFromFeatureValue(arrayListFrom(rvc, index));

        } catch (GetOperationFailedException e) {
            throw new FeatureCalculationException(e);
        }
    }

    protected abstract double statisticFromFeatureValue(DoubleArrayList featureVals)
            throws FeatureCalculationException;

    private static DoubleArrayList arrayListFrom(ResultsVectorCollection rvc, int index) {
        DoubleArrayList featureVals = new DoubleArrayList();
        for (int i = 0; i < rvc.size(); i++) {
            featureVals.add(rvc.get(i).get(index));
        }
        return featureVals;
    }
}
