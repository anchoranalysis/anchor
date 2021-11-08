package org.anchoranalysis.feature.io.results.calculation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.results.FeatureOutputMetadata;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Removes any columns containing only NaN columns from the final results.
 * 
 * @author Owen Feehan
 *
 */
class RemoveNaNColumns extends WriteLazy {
    
    /** 
     * The remaining indices to check for non-NaN values as feature-calculation results are encountered.
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
     * @param writerCreator creates a {@link FeatureCSVWriter} for writing the non-aggregated feature results.
     * @throws OutputWriteFailedException if a CSV for (non-aggregated) features fails to be
     *     created.
     */
    public RemoveNaNColumns(FeatureOutputMetadata outputMetadata, FeatureCSVWriterCreator writerCreator)
            throws OutputWriteFailedException {
        super(outputMetadata, writerCreator);
        
        // Contains integers 0 to (numberFeatures-1) inclusive.
        int numberFeatures = outputMetadata.featureNamesNonAggregate().size();
        indicesToCheck = IntStream.range(0, numberFeatures).boxed().collect(Collectors.toList());
    }
    
    @Override
    public void add(LabelledResultsVector results) {
        super.add(results);
        
        Iterator<Integer> iterator = indicesToCheck.iterator();
        while(iterator.hasNext()) {
            int index = iterator.next();
            if (!Double.isNaN(results.get(index))) {
                // No longer check the index, once a non-NaN value is encountered.
                iterator.remove();
            }
        }
    }

    @Override
    protected FeatureOutputMetadata processBeforeWriting(FeatureOutputMetadata metadata,
            List<LabelledResultsVector> results) {
        
        // Remove any features represented by indicesToCheck
        IndexRemover.removeResultsAtIndices(results, indicesToCheck);
        
        return IndexRemover.removeHeadersAtIndices(metadata, indicesToCheck);
    }
}
