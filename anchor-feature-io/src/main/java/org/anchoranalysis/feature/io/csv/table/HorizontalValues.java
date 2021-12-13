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
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.feature.results.ResultsVectorList;
import org.anchoranalysis.io.generator.tabular.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * CSV file where an entity's various feature result values span across a <b>horizontal</b> column.
 *
 * @author Owen Feehan
 */
class HorizontalValues extends FeatureTableCSVGenerator<ResultsVectorList> {

    /**
     * Creates without setting any <i>results</i> (i.e. row-data).
     *
     * <p>The CSV file is populated with the <i>results</i> of calculations corresponding to these
     * features.
     *
     * @param manifestFunction identifier of function for the manifest file.
     * @param featureNames names-of-features that will appear in results.
     */
    public HorizontalValues(String manifestFunction, FeatureNameList featureNames) {
        super(manifestFunction, featureNames.asList());
    }

    @Override
    protected void writeFeaturesToCSV(
            CSVWriter writer, ResultsVectorList allFeatureResults, List<String> headerNames)
            throws OutputWriteFailedException {

        // We add a header line
        writer.writeHeaders(headerNames);

        for (ResultsVector results : allFeatureResults) {

            List<TypedValue> csvRow = new ArrayList<>();
            results.addTypedValuesTo(csvRow, FeatureCSVWriter.NUMBER_DECIMAL_PLACES);
            writer.writeRow(csvRow);
        }
    }
}
