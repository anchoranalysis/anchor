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

import java.util.Iterator;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVector;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class IndexRemover {

    /**
     * Removes calculations at particular indices in {@code results}.
     *
     * @param results the list whose elements are changed (in-place).
     * @param indicesToRemove the indices of header elements to remove. These <b>must be in
     *     ascending order</b>.
     */
    public static void removeResultsAtIndices(
            List<LabelledResultsVector> results, List<Integer> indicesToRemove) {

        if (indicesToRemove.isEmpty()) {
            // Exit early as there's nothing to do
        }

        for (int i = 0; i < results.size(); i++) {
            LabelledResultsVector labelledResult = results.get(i);

            ResultsVector replacement =
                    removeIndicesFromResults(labelledResult.getResults(), indicesToRemove);

            results.set(i, new LabelledResultsVector(labelledResult.getLabels(), replacement));
        }
    }

    /**
     * Create a {@link FeatureCSVMetadata} but with headers at specific indices removed.
     *
     * @param featureNames the feature-names from which certain names are removed (those at {@code
     *     indicesToRemove})
     * @param indicesToRemove the indices of header elements to remove. These <b>must be in
     *     ascending order</b>.
     * @return the metadata newly-created (if headers removed) or reused (if no headers removed).
     */
    public static FeatureNameList removeHeadersAtIndices(
            FeatureNameList featureNames, List<Integer> indicesToRemove) {

        if (indicesToRemove.isEmpty()) {
            // Exit early as there's nothing to do
            return featureNames;
        }

        FeatureNameList namesKept = new FeatureNameList();

        Iterator<Integer> iteratorOmitIndices = indicesToRemove.iterator();
        int omitNext = nextIndexToOmit(iteratorOmitIndices);

        for (int i = 0; i < featureNames.size(); i++) {
            // The indices are guaranteed to be in ascending order so we check if we match the next
            // index
            if (i == omitNext) {
                omitNext = nextIndexToOmit(iteratorOmitIndices);
            } else {
                namesKept.add(featureNames.get(i));
            }
        }

        return namesKept;
    }

    /**
     * Returns the next index to omit (in ascending order), or -1 if there are no more indices to
     * omit.
     */
    private static int nextIndexToOmit(Iterator<Integer> iteratorOmitIndices) {
        if (iteratorOmitIndices.hasNext()) {
            return iteratorOmitIndices.next();
        } else {
            return -1;
        }
    }

    /**
     * Creates {@link ResultsVector} which has particular indices removed, but is otherwise
     * identical.
     */
    private static ResultsVector removeIndicesFromResults(
            ResultsVector results, List<Integer> indicesToRemove) {
        ResultsVector out = new ResultsVector(results.size() - indicesToRemove.size());

        Iterator<Integer> iteratorOmitIndices = indicesToRemove.iterator();
        int omitNext = nextIndexToOmit(iteratorOmitIndices);

        int outIndex = 0;
        for (int i = 0; i < results.size(); i++) {
            // The indices are guaranteed to be in ascending order so we check if we match the next
            // index
            if (i == omitNext) {
                omitNext = nextIndexToOmit(iteratorOmitIndices);
            } else {
                out.set(outIndex++, results.get(i));
            }
        }

        return out;
    }
}
