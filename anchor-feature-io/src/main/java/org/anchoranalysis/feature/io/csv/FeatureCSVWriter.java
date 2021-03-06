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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.value.TypedValue;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.io.generator.tabular.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Writes the results of feature-calculations as a CSV file.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class FeatureCSVWriter {

    /** Underlying CSV write, which if null, it means the writer is disabled */
    private final CSVWriter writer;

    /**
     * Maybe creates a {@link FeatureCSVWriter} depending if the output is allowed.
     *
     * @param metadata metadata needed for writing the reeature-results
     * @param outputter determines if the output is allowed.
     * @return a write, if it is allowed.
     * @throws OutputWriteFailedException if outputting fails
     */
    public static Optional<FeatureCSVWriter> create(
            FeatureCSVMetadata metadata, Outputter outputter) throws OutputWriteFailedException {

        if (!outputter.outputsEnabled().isOutputEnabled(metadata.getOutputName())) {
            return Optional.of(new FeatureCSVWriter(null));
        }

        Optional<CSVWriter> writerOptional =
                CSVWriter.createFromOutputter(metadata.getOutputName(), outputter.getChecked());
        return writerOptional.map(
                writer -> {
                    writer.writeHeaders(metadata.getHeaders());
                    return new FeatureCSVWriter(writer);
                });
    }

    /**
     * Directly adds a row of feature-values.
     *
     * @param labels laels for the row
     * @param featureResults results for the row
     */
    public void addRow(RowLabels labels, ResultsVector featureResults) {
        if (writer == null) {
            return;
        }
        addRow(buildCsvRow(labels, featureResults));
    }

    /**
     * Directly adds a row in the form of typed-values.
     *
     * @param values a list of typed-values corresponding to a row in a CSV file.
     */
    public void addRow(List<TypedValue> values) {

        if (writer == null) {
            return;
        }

        writer.writeRow(values);
    }

    /**
     * Closes any open file-handles.
     *
     * <p>This operation should always be called <i>once</i> at the end of writing.
     */
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
     * @param resultsFromFeatures results to include in row
     * @return a list of typed-values as forms a row in a CSV file.
     */
    private static List<TypedValue> buildCsvRow(
            RowLabels identifier, ResultsVector resultsFromFeatures) {

        List<TypedValue> csvRow = new ArrayList<>();
        identifier.addToRow(csvRow);
        resultsFromFeatures.addToTypeValueCollection(csvRow, 10);
        return csvRow;
    }
}
