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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadataForOutput;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.io.generator.tabular.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows results to be written to a {@link FeatureCSVWriter}, possibly adding further processing.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LabelledResultsCSVWriter {

    // START REQUIRED ARGUMENTS
    /** Metadata defining which CSV file to write to, and which headers. */
    protected final FeatureCSVMetadataForOutput outputMetadata;

    /** Creates a {@link FeatureCSVWriter} for writing the non-aggregated feature results. */
    private final FeatureCSVWriterFactory writerCreator;

    /**
     * After adding a {@link LabelledResultsVector}, this function is also called, if it is defined.
     */
    private final Optional<Consumer<LabelledResultsVector>> consumeAfterAdding;
    // END REQUIRED ARGUMENTS

    /**
     * Starts writing the CSV file.
     *
     * <p>This should be called once, <b>before any calls</b> to {@link
     * #add(LabelledResultsVector)}.
     *
     * @throws OutputWriteFailedException if the output file cannot be created.
     */
    public abstract void start() throws OutputWriteFailedException;

    /**
     * Adds a results-element to be written.
     *
     * <p>This should be called once for each set of results to be written.
     *
     * @param results the results.
     */
    public abstract void add(LabelledResultsVector results);

    /**
     * Stops writing the CSV file.
     *
     * <p>This should be called once, <b>after all calls</b> to {@link #add(LabelledResultsVector)}.
     *
     * @throws OutputWriteFailedException if the output file cannot be created.
     */
    public abstract void end() throws OutputWriteFailedException;

    /**
     * Creates a {@link CSVWriter}.
     *
     * @param csvMetadata the metadata for the writer.
     * @return the newly-created writer, if possible.
     * @throws OutputWriteFailedException if the CSV file cannot be created successfully.
     */
    protected Optional<FeatureCSVWriter> createWriter(FeatureCSVMetadata csvMetadata)
            throws OutputWriteFailedException {
        return writerCreator.create(csvMetadata);
    }

    /**
     * Consumes the {@link LabelledResultsVector} if {@code consumeAfterAdding} is present.
     *
     * @param results the results to maybe consume.
     */
    protected void maybeConsumeResults(LabelledResultsVector results) {
        consumeAfterAdding.ifPresent(consumer -> consumer.accept(results));
    }
}
