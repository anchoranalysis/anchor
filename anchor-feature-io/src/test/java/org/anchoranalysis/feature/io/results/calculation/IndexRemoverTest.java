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
 *
 */
class IndexRemoverTest {

    private static final double TOLERANCE = 1e-10;
    
    private static final List<Integer> INDICES_TO_REMOVE = Arrays.asList(1, 3);
    
    @Test
    void testResults() {

        List<LabelledResultsVector> results = Arrays.asList(
                new LabelledResultsVector(createSequenceResults(5)),
                new LabelledResultsVector(createSequenceResults(8))
        );
        IndexRemover.removeResultsAtIndices(results, INDICES_TO_REMOVE);
        assertEquals(2, results.size());
        assertEquals(4, results.get(0).get(2), TOLERANCE);
        assertEquals(7, results.get(1).get(5), TOLERANCE);
    }
    
    @Test
    void testMetadata() {
        FeatureNameList featureNames = new FeatureNameList( Arrays.asList("a", "b", "c", "d", "e") );
        FeatureOutputMetadata metadata = new FeatureOutputMetadata(new LabelHeaders(new String[] {}), featureNames, new FeatureOutputNames());
        FeatureOutputMetadata result = IndexRemover.removeHeadersAtIndices(metadata, Arrays.asList(1, 3));
        assertEquals(Arrays.asList("a", "c", "e"), result.featureNamesNonAggregate().asList());
    }
    
    /** Creates a {@link ResultsVector} where each value is identical to its index (zero-indexed). */
    private static ResultsVector createSequenceResults(int size) {
        ResultsVector results = new ResultsVector(size);
        for (int i=0; i<size; i++) {
            results.set(i, i);
        }
        return results;
    }
}
