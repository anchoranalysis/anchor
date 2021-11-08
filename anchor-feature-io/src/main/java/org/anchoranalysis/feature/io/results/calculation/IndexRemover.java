package org.anchoranalysis.feature.io.results.calculation;

import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.feature.io.csv.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.results.FeatureOutputMetadata;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVector;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class IndexRemover {

    /**
     * Removes calculations at particular indices in {@code results}.
     * 
     * @param results the list whose elements are changed (in-place).
     * @param indicesToRemove the indices of header elements to remove. These <b>must be in ascending order</b>.
     */
    public static void removeResultsAtIndices(List<LabelledResultsVector> results, List<Integer> indicesToRemove) {
        
        if (indicesToRemove.isEmpty()) {
            // Exit early as there's nothing to do
        }
        
        for (int i=0; i<results.size(); i++) {
            LabelledResultsVector labelledResult = results.get(i);
            
            ResultsVector replacement = removeIndicesFromResults(labelledResult.getResults(), indicesToRemove);
            
            results.set(i, new LabelledResultsVector(labelledResult.getLabels(), replacement));
        }
    }
    
    /**
     * Create a duplicated {@link FeatureCSVMetadata} but with headers at specific indices removed.
     * 
     * @param metadata the metadata to copy from.
     * @param indicesToRemove the indices of header elements to remove. These <b>must be in ascending order</b>.
     * @return the metadata newly-created (if headers removed) or reused (if no headers removed).
     */
    public static FeatureOutputMetadata removeHeadersAtIndices(FeatureOutputMetadata metadata, List<Integer> indicesToRemove) {
        
        if (indicesToRemove.isEmpty()) {
            // Exit early as there's nothing to do
            return metadata;
        }
        
        FeatureNameList namesKept = new FeatureNameList();

        Iterator<Integer> iteratorOmitIndices = indicesToRemove.iterator();
        int omitNext = nextIndexToOmit(iteratorOmitIndices);
        
        for( int i=0; i<metadata.featureNamesNonAggregate().size(); i++) {
            // The indices are guaranteed to be in ascending order so we check if we match the next index
            if (i==omitNext) {
                omitNext = nextIndexToOmit(iteratorOmitIndices);
            } else {
                namesKept.add( metadata.featureNamesNonAggregate().get(i) );
            }
        }
        
        return new FeatureOutputMetadata(metadata.labelHeaders(), namesKept, metadata.outputNames());
    }
    
    /** Returns the next index to omit (in ascending order), or -1 if there are no more indices to omit. */
    private static int nextIndexToOmit(Iterator<Integer> iteratorOmitIndices) {
        if (iteratorOmitIndices.hasNext()) {
            return iteratorOmitIndices.next();
        } else {
            return -1;
        }
    }
    
    
    /** Creates {@link ResultsVector} which has particular indices removed, but is otherwise identical. */
    private static ResultsVector removeIndicesFromResults(ResultsVector results, List<Integer> indicesToRemove) {
        ResultsVector out = new ResultsVector(results.size() - indicesToRemove.size());
        
        Iterator<Integer> iteratorOmitIndices = indicesToRemove.iterator();
        int omitNext = nextIndexToOmit(iteratorOmitIndices);
        
        int outIndex = 0;
        for( int i=0; i<results.size(); i++) {
            // The indices are guaranteed to be in ascending order so we check if we match the next index
            if (i==omitNext) {
                omitNext = nextIndexToOmit(iteratorOmitIndices);
            } else {
                out.set(outIndex++, results.get(i) );
            }
        }
        
        return out;
    }
}