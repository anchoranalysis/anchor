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
import org.anchoranalysis.core.value.TypedValue;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.csv.metadata.RowLabels;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.results.ResultsVector;
import org.anchoranalysis.io.generator.tabular.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;

/**
 * Writes the results of feature-calculations as a CSV file.
 *
 * @author Owen Feehan
 */
public class FeatureCSVWriter {

    /**
     * The number of decimal places to use for {@code double} values, unless {@code
     * visuallyShortened==true}.
     */
    public static final int NUMBER_DECIMAL_PLACES = 10;

    /** Underlying CSV writer, which if null, it means the writer is disabled */
    private final CSVWriter writer;

    private final int numberDecimalPlaces;

    /**
     * Creates for a {@link CSVWriter}.
     *
     * @param writer underlying CSV writer, which if null, it means the writer is disabled.
     * @param visuallyShortenedDecimals when true {@code double} values are printed to be as short
     *     as possible without losing precision, otherwse with {@link #NUMBER_DECIMAL_PLACES}.
     */
    public FeatureCSVWriter(CSVWriter writer, boolean visuallyShortenedDecimals) {
        this.writer = writer;
        this.numberDecimalPlaces = visuallyShortenedDecimals ? -1 : NUMBER_DECIMAL_PLACES;
    }

    /**
     * Maybe creates a {@link FeatureCSVWriter} depending if the output is allowed.
     *
     * @param metadata metadata needed for writing the feature-results.
     * @param outputter determines if the output is allowed.
     * @param visuallyShortenedDecimals when true {@code double} values are printed to be as short
     *     as possible without losing precision.
     * @return a write, if it is allowed.
     * @throws OutputWriteFailedException if the CSV file cannot be created successfully.
     */
    public static Optional<FeatureCSVWriter> create(
            FeatureCSVMetadata metadata, Outputter outputter, boolean visuallyShortenedDecimals)
            throws OutputWriteFailedException {

        if (!outputter.outputsEnabled().isOutputEnabled(metadata.getOutputName())) {
            return Optional.of(new FeatureCSVWriter(null, visuallyShortenedDecimals));
        }

        Optional<CSVWriter> writerOptional =
                CSVWriter.createFromOutputter(metadata.getOutputName(), outputter.getChecked());
        return writerOptional.map(
                writer -> {
                    writer.writeHeaders(metadata.getHeaders());
                    return new FeatureCSVWriter(writer, visuallyShortenedDecimals);
                });
    }

    /**
     * Directly adds a row of feature-values.
     *
     * @param results results for the row, along with corresponding labels.
     */
    public void addRow(LabelledResultsVector results) {
        if (writer == null) {
            return;
        }
        addRow(buildCSVRow(results.getLabels(), results.getResults()));
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
    private List<TypedValue> buildCSVRow(RowLabels identifier, ResultsVector resultsFromFeatures) {

        List<TypedValue> csvRow = new ArrayList<>();
        identifier.addToRow(csvRow);
        resultsFromFeatures.addTypedValuesTo(csvRow, numberDecimalPlaces);
        return csvRow;
    }
}
