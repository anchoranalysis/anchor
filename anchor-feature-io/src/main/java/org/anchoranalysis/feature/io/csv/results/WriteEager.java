/*-
 * #%L
 * anchor-feature-io
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.feature.io.csv.results;

import java.util.Optional;
import java.util.function.Consumer;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadataForOutput;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes the results to the file-system as soon as they are added.
 *
 * @author Owen Feehan
 */
class WriteEager extends LabelledResultsCSVWriter {

    /** The writer to use for writing single results CSV. */
    private Optional<FeatureCSVWriter> singleWriter = Optional.empty();

    /**
     * Creates with appropriate support classes for outputting.
     *
     * @param outputMetadata metadata needed for writing the CSV file.
     * @param writerCreator creates a {@link FeatureCSVWriter} for writing the non-aggregated
     *     feature results.
     * @param consumeAfterAdding After adding a {@link LabelledResultsVector}, this function is also
     *     called, if it is defined.
     */
    protected WriteEager(
            FeatureCSVMetadataForOutput outputMetadata,
            FeatureCSVWriterFactory writerCreator,
            Optional<Consumer<LabelledResultsVector>> consumeAfterAdding) {
        super(outputMetadata, writerCreator, consumeAfterAdding);
    }

    @Override
    public void start() throws OutputWriteFailedException {
        singleWriter = createWriter(outputMetadata.metadata());
    }

    @Override
    public void add(LabelledResultsVector results) {

        maybeConsumeResults(results);

        if (singleWriter.isPresent()) {
            singleWriter.get().addRow(results);
        }
    }

    @Override
    public void end() {
        singleWriter.ifPresent(FeatureCSVWriter::close);
    }
}
