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

import java.nio.file.Path;
import java.util.List;
import lombok.AllArgsConstructor;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.tabular.CSVGenerator;
import org.anchoranalysis.io.generator.tabular.CSVWriter;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Base class for a {@link Generator} that outputs a feature-table in CSV format.
 *
 * @author Owen Feehan
 * @param <T> type of object that describes <i>all</i> rows of feature calculations.
 */
@AllArgsConstructor
public abstract class FeatureTableCSVGenerator<T> extends CSVGenerator<T> {

    /** The headers of all columns for the CSV output. */
    private List<String> headerNames;

    @Override
    public void writeToFile(T element, OutputWriteSettings settings, Path filePath)
            throws OutputWriteFailedException {

        try (CSVWriter writer = CSVWriter.create(filePath)) {
            writeFeaturesToCSV(writer, element, headerNames);
        } catch (OutputWriteFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    /**
     * Writes the features to the CSV-file.
     *
     * @param writer the write to use
     * @param allFeatureResults all rows to write
     * @param headerNames header-names for columns, corresponding to the data in {@code rows}.
     * @throws OutputWriteFailedException if the output cannot be written.
     */
    protected abstract void writeFeaturesToCSV(
            CSVWriter writer, T allFeatureResults, List<String> headerNames)
            throws OutputWriteFailedException;
}
