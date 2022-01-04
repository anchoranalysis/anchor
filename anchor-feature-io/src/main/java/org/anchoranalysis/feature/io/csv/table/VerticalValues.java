/*-
 * #%L
 * anchor-feature-io
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

package org.anchoranalysis.feature.io.csv.table;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.value.TypedValue;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.results.ResultsVectorList;
import org.anchoranalysis.io.generator.tabular.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * CSV file where an entity's various feature result values span across a <b>vertical</b> column.
 *
 * <p>The CSV file is populated with the <i>results</i> of calculations corresponding to these
 * features.
 *
 * @author Owen Feehan
 */
class VerticalValues extends FeatureTableCSVGenerator<ResultsVectorList> {

    /**
     * Creates without setting any <i>results</i> (i.e. column-data).
     *
     * @param featureNames names-of-features that will appear in results.
     */
    public VerticalValues(FeatureNameList featureNames) {
        super(featureNames.asList());
    }

    @Override
    protected void writeFeaturesToCSV(
            CSVWriter writer, ResultsVectorList allFeatureResults, List<String> headerNames)
            throws OutputWriteFailedException {

        int size = headerNames.size();

        for (int featureIndex = 0; featureIndex < size; featureIndex++) {
            String featureName = headerNames.get(featureIndex);

            writer.writeRow(generateRow(featureName, allFeatureResults, featureIndex, size));
        }
    }

    private static List<TypedValue> generateRow(
            String featureName, ResultsVectorList allFeatureResults, int featureIndex, int size)
            throws OutputWriteFailedException {

        List<TypedValue> csvRow = new ArrayList<>();

        // The Name
        csvRow.add(new TypedValue(featureName));

        for (ResultsVector results : allFeatureResults) {

            if (results.size() != size) {
                throw new OutputWriteFailedException(
                        String.format(
                                "ResultsVector has size (%d) != featureNames vector (%d)",
                                results.size(), size));
            }

            csvRow.add(replaceNaN(results.get(featureIndex)));
        }

        return csvRow;
    }

    /** Replaces NaN with error */
    private static TypedValue replaceNaN(double val) {
        if (Double.isNaN(val)) {
            return new TypedValue("Error");
        } else {
            return new TypedValue(val, 10);
        }
    }
}
