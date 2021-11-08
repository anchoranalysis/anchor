package org.anchoranalysis.feature.io.results.calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.csv.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.results.FeatureOutputMetadata;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.store.NamedFeatureStore;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

/**
 * Delays writing feature-results until the final {{@link #flushAndClose}}.
 *
 * <p>Each result is stored in memory until then.
 *
 * @author Owen Feehan
 */
abstract class WriteLazy extends WriteWithGroups {

    private FeatureOutputMetadata outputMetadata;
    private FeatureCSVWriterCreator writerCreator;

    /**
     * Creates with appropriate support classes for outputting.
     *
     * @param outputMetadata metadata needed for determining output-names and CSV headers.
     * @param writerCreator creates a {@link FeatureCSVWriter} for writing the non-aggregated
     *     feature results.
     * @throws OutputWriteFailedException if a CSV for (non-aggregated) features fails to be
     *     created.
     */
    public WriteLazy(FeatureOutputMetadata outputMetadata, FeatureCSVWriterCreator writerCreator)
            throws OutputWriteFailedException {
        this.outputMetadata = outputMetadata;
        this.writerCreator = writerCreator;
    }

    /** The results that have been saved to write upon completion. */
    private List<LabelledResultsVector> storedResults = new ArrayList<>();

    @Override
    public void add(LabelledResultsVector results) {
        storedResults.add(results);
    }

    @Override
    public void flushAndClose(
            Optional<NamedFeatureStore<FeatureInputResults>> featuresAggregate,
            boolean includeGroups,
            Function<InputOutputContext, FeatureCSVWriterCreator> csvWriterCreator,
            InputOutputContext context)
            throws OutputWriteFailedException {

        outputMetadata = processBeforeWriting(outputMetadata, storedResults);

        // Where non-group results are outputted
        singleWriter = writerCreator.create(outputMetadata.metadataNonAggregated());

        for (LabelledResultsVector results : storedResults) {
            groupedResults.addResultsFor(results);

            if (singleWriter.isPresent()) {
                singleWriter.get().addRow(results); // NOSONAR
            }
        }

        storedResults.clear();

        super.flushAndClose(featuresAggregate, includeGroups, csvWriterCreator, context);
    }

    @Override
    protected FeatureOutputMetadata outputMetadataForGroups() {
        return outputMetadata;
    }

    /**
     * Optionally changes {#code results} and derives a changed {@link FeatureCSVMetadata}.
     *
     * @param metadata the metadata for non-aggregated features, before any changes.
     * @param results the results of non-aggregated features, which may be changed inplace.
     * @return the metadata for non-aggregated features, after any changes.
     */
    protected abstract FeatureOutputMetadata processBeforeWriting(
            FeatureOutputMetadata metadata, List<LabelledResultsVector> results);
}
