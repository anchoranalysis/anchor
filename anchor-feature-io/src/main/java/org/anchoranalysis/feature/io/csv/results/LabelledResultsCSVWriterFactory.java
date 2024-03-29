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
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadataForOutput;
import org.anchoranalysis.feature.io.results.LabelledResultsCollector;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Creates a new instance of {@link LabelledResultsCSVWriter}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LabelledResultsCSVWriterFactory {

    /**
     * Creates with appropriate support classes for outputting.
     *
     * @param outputMetadata metadata needed for determining output-names and CSV headers.
     * @param writerCreator when true, columns containing all {@link Double#NaN} values are removed
     *     before outputting.
     * @param consumeAfterAdding After adding a {@link LabelledResultsVector}, this function is also
     *     called, if it is defined.
     * @param removeNaNColumns when true, columns containing all {@link Double#NaN} values are
     *     removed before outputting.
     * @return a newly created {@link LabelledResultsCollector}.
     * @throws OutputWriteFailedException if a CSV for (non-aggregated) features fails to be
     *     created.
     */
    public static LabelledResultsCSVWriter create(
            FeatureCSVMetadataForOutput outputMetadata,
            FeatureCSVWriterFactory writerCreator,
            Optional<Consumer<LabelledResultsVector>> consumeAfterAdding,
            boolean removeNaNColumns)
            throws OutputWriteFailedException {
        if (removeNaNColumns) {
            return new RemoveNaNColumns(outputMetadata, writerCreator, consumeAfterAdding);
        } else {
            return new WriteEager(outputMetadata, writerCreator, consumeAfterAdding);
        }
    }
}
