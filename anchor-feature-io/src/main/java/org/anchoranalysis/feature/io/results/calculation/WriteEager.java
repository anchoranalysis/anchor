package org.anchoranalysis.feature.io.results.calculation;

import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.results.FeatureOutputMetadata;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Writes the results to the file-system as soon as they are added.
 *
 * @author Owen Feehan
 */
class WriteEager extends WriteWithGroups {

    private final FeatureOutputMetadata outputMetadata;

    /**
     * Creates with appropriate support classes for outputting.
     *
     * @param outputMetadata metadata needed for determining output-names and CSV headers.
     * @param writerCreator creates a {@link FeatureCSVWriter} for writing the non-aggregated
     *     feature results.
     * @throws OutputWriteFailedException if a CSV for (non-aggregated) features fails to be
     *     created.
     */
    public WriteEager(FeatureOutputMetadata outputMetadata, FeatureCSVWriterCreator writerCreator)
            throws OutputWriteFailedException {
        this.outputMetadata = outputMetadata;

        // Where non-group results are outputted
        singleWriter = writerCreator.create(outputMetadata.metadataNonAggregated());
    }

    @Override
    public void add(LabelledResultsVector results) {
        groupedResults.addResultsFor(results);

        if (singleWriter.isPresent()) {
            singleWriter.get().addRow(results);
        }
    }

    @Override
    protected FeatureOutputMetadata outputMetadataForGroups() {
        return outputMetadata;
    }
}
