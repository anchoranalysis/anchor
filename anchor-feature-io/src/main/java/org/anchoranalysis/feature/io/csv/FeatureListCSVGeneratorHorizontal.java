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

package org.anchoranalysis.feature.io.csv;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.feature.calculate.results.ResultsVector;
import org.anchoranalysis.feature.calculate.results.ResultsVectorCollection;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.io.output.csv.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * CSV file where each feature is a column (spanning horizontally).
 *
 * @author Owen Feehan
 */
public class FeatureListCSVGeneratorHorizontal
        extends FeatureTableCSVGenerator<ResultsVectorCollection> {

    /**
     * Creates without setting any <i>results</i> (i.e. row-data).
     *
     * <p>The CSV file is populated with the <i>results</i> of calculations corresponding to these
     * features.
     *
     * @param manifestFunction identifier of function for the manifest file.
     * @param featureNames names-of-features that will appear in results.
     */
    public FeatureListCSVGeneratorHorizontal(
            String manifestFunction, FeatureNameList featureNames) {
        super(manifestFunction, featureNames.asList());
    }

    /**
     * Creates without setting any <i>results</i> (i.e. row-data).
     *
     * @param manifestFunction identifier of function for the manifest file.
     * @param featureNames names-of-features in {@code results}.
     * @param results the results (i.e. row data) to set as current element for the generator.
     */
    public FeatureListCSVGeneratorHorizontal(
            String manifestFunction,
            FeatureNameList featureNames,
            ResultsVectorCollection results) {
        this(manifestFunction, featureNames);
        assignElement(results);
    }

    @Override
    protected void writeFeaturesToCSV(
            CSVWriter writer, ResultsVectorCollection allFeatureResults, List<String> headerNames)
            throws OutputWriteFailedException {

        // We add a header line
        writer.writeHeaders(headerNames);

        for (ResultsVector results : allFeatureResults) {

            List<TypedValue> csvRow = new ArrayList<>();
            results.addToTypeValueCollection(csvRow, 10);
            writer.writeRow(csvRow);
        }
    }
}
