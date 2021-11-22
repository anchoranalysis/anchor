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
package org.anchoranalysis.feature.io.results.calculation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.feature.io.results.FeatureOutputMetadata;
import org.anchoranalysis.feature.io.results.FeatureOutputNames;
import org.anchoranalysis.feature.io.results.LabelHeaders;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVector;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IndexRemover}.
 *
 * @author Owen Feehan
 */
class IndexRemoverTest {

    private static final double TOLERANCE = 1e-10;

    private static final List<Integer> INDICES_TO_REMOVE = Arrays.asList(1, 3);

    @Test
    void testResults() {

        List<LabelledResultsVector> results =
                Arrays.asList(
                        new LabelledResultsVector(createSequenceResults(5)),
                        new LabelledResultsVector(createSequenceResults(8)));
        IndexRemover.removeResultsAtIndices(results, INDICES_TO_REMOVE);
        assertEquals(2, results.size());
        assertEquals(4, results.get(0).get(2), TOLERANCE);
        assertEquals(7, results.get(1).get(5), TOLERANCE);
    }

    @Test
    void testMetadata() {
        FeatureNameList featureNames = new FeatureNameList(Arrays.asList("a", "b", "c", "d", "e"));
        FeatureOutputMetadata metadata =
                new FeatureOutputMetadata(
                        new LabelHeaders(new String[] {}), featureNames, new FeatureOutputNames());
        FeatureOutputMetadata result =
                IndexRemover.removeHeadersAtIndices(metadata, Arrays.asList(1, 3));
        assertEquals(Arrays.asList("a", "c", "e"), result.featureNamesNonAggregate().asList());
    }

    /**
     * Creates a {@link ResultsVector} where each value is identical to its index (zero-indexed).
     */
    private static ResultsVector createSequenceResults(int size) {
        ResultsVector results = new ResultsVector(size);
        for (int i = 0; i < size; i++) {
            results.set(i, i);
        }
        return results;
    }
}
