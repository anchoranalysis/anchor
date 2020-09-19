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

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.csv.CSVGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.csv.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Base class for a {@link IterableGenerator} that outputs a feature-table in CSV format.
 *
 * @author Owen Feehan
 * @param <T> type of object that describes <i>all</i> rows of feature calculations.
 */
public abstract class FeatureTableCSVGenerator<T> extends CSVGenerator<T> {

    private List<String> headerNames;

    /**
     * Creates for a particular manifest-function and headers.
     *
     * @param manifestFunction the manifest-function.
     * @param headerNames the headers of all columns for the CSV output.
     */
    public FeatureTableCSVGenerator(String manifestFunction, List<String> headerNames) {
        super(manifestFunction);
        this.headerNames = headerNames;
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {

        try (CSVWriter writer = CSVWriter.create(filePath)) {
            writeFeaturesToCSV(writer, getIterableElement(), headerNames);
        } catch (AnchorIOException e) {
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
