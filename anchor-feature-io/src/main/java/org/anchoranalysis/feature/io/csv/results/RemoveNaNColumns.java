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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadataForOutput;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Removes any columns containing only NaN columns from the final results.
 *
 * @author Owen Feehan
 */
class RemoveNaNColumns extends WriteLazy {

    /**
     * The remaining indices to check for non-NaN values as feature-calculation results are
     * encountered.
     *
     * <p>Initially this list, is initialized to an index corresponding to every feature.
     *
     * <p>Once a non-NaN, value is first encountered, the index is removed, and no longer checked.
     */
    private List<Integer> indicesToCheck = new ArrayList<>();

    /**
     * Creates with appropriate support classes for outputting.
     *
     * @param outputMetadata metadata needed for determining output-names and CSV headers.
     * @param writerCreator creates a {@link FeatureCSVWriter} for writing the non-aggregated
     *     feature results.
     * @param consumeAfterAdding After adding a {@link LabelledResultsVector}, this function is also
     *     called, if it is defined.
     * @throws OutputWriteFailedException if a CSV for (non-aggregated) features fails to be
     *     created.
     */
    public RemoveNaNColumns(
            FeatureCSVMetadataForOutput outputMetadata,
            FeatureCSVWriterFactory writerCreator,
            Optional<Consumer<LabelledResultsVector>> consumeAfterAdding)
            throws OutputWriteFailedException {
        super(outputMetadata, writerCreator, consumeAfterAdding);

        // Contains integers 0 to (numberFeatures-1) inclusive.
        int numberFeatures = outputMetadata.featureNames().size();
        indicesToCheck = IntStream.range(0, numberFeatures).boxed().toList();
    }

    @Override
    public void add(LabelledResultsVector results) {
        super.add(results);

        Iterator<Integer> iterator = indicesToCheck.iterator();
        while (iterator.hasNext()) {
            int index = iterator.next();
            if (!Double.isNaN(results.get(index))) {
                // No longer check the index, once a non-NaN value is encountered.
                iterator.remove();
            }
        }
    }

    @Override
    protected FeatureCSVMetadata processBeforeWriting(
            FeatureCSVMetadataForOutput metadata, List<LabelledResultsVector> results) {

        // Remove any features represented by indicesToCheck
        IndexRemover.removeResultsAtIndices(results, indicesToCheck);

        FeatureNameList namesKept =
                IndexRemover.removeHeadersAtIndices(metadata.featureNames(), indicesToCheck);
        return new FeatureCSVMetadata(
                metadata.outputName(), metadata.nonFeatureHeaders(), namesKept);
    }
}
