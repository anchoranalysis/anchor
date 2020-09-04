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

package org.anchoranalysis.feature.io.csv.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.feature.calculate.results.ResultsVector;
import org.anchoranalysis.feature.calculate.results.ResultsVectorCollection;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.csv.CSVWriter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeatureCSVWriter {

    /** Underlying CSV write, which if null, it means the writer is disabled */
    private final CSVWriter writer; 

    public static Optional<FeatureCSVWriter> create(
            FeatureCSVMetadata metadata,
            BoundOutputManagerRouteErrors outputManager
    )
            throws AnchorIOException {

        if (!outputManager.isOutputAllowed(metadata.getOutputName())) {
            return Optional.of(new FeatureCSVWriter(null));
        }

        Optional<CSVWriter> writerOptional =
                CSVWriter.createFromOutputManager(metadata.getOutputName(), outputManager.getDelegate());
        return writerOptional.map(
                writer -> {
                    writer.writeHeaders(metadata.getHeaders());
                    return new FeatureCSVWriter(writer);
                });
    }

    public void addResultsVector(
            RowLabels identifier, ResultsVector resultsFromFeatures) {
        if (writer == null) {
            return;
        }

        addRow(buildCsvRow(identifier, resultsFromFeatures));
    }

    /** Directly adds a row without any ResultsVector */
    public void addRow(List<TypedValue> values) {

        if (writer == null) {
            return;
        }

        writer.writeRow(values);
    }

    public void addResultsVector(
            RowLabels identifier,
            ResultsVectorCollection resultsCollectionFromFeatures) {

        if (writer == null) {
            return;
        }

        for (ResultsVector rv : resultsCollectionFromFeatures) {
            assert (rv != null);
            addResultsVector(identifier, rv);
        }
    }

    public void close() {

        if (writer == null) {
            return;
        }

        writer.close();
    }

    /**
     * Build a row for the CSV output
     *
     * @param identifier unique identifier for the row and/or the group it belongs to
     * @param resultsFromFeatures results to incluide in row
     * @return
     */
    private static List<TypedValue> buildCsvRow(
            RowLabels identifier, ResultsVector resultsFromFeatures) {

        List<TypedValue> csvRow = new ArrayList<>();
        identifier.addToRow(csvRow);
        resultsFromFeatures.addToTypeValueCollection(csvRow, 10);
        return csvRow;
    }
}
