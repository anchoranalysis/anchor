package org.anchoranalysis.feature.io.results.calculation;

import java.util.Optional;
import java.util.function.Function;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.feature.store.NamedFeatureStore;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

/**
 * Stores {@link LabelledResultsVector} as they are calculated, and writes to the file-system.
 *
 * @author Owen Feehan
 */
public interface FeatureCalculationResults {

    /**
     * Adds a results-element to be written.
     *
     * <p>This should be called once for each set of results to be written.
     *
     * @param results the results.
     */
    void add(LabelledResultsVector results);

    /**
     * Writes any queued (but not yet written elements to the file-system), and closes open
     * file-handles.
     *
     * <p>This should be called <i>once only</i> after all calls to {@link
     * #add(LabelledResultsVector)} are completed.
     *
     * @param featuresAggregate aggregate-features.
     * @param includeGroups iff true a group-column is included in the CSV file and the group
     *     exports occur, otherwise not.
     * @param csvWriterCreator creates a CSV writer for a particular IO-context.     
     * @param context input-output context.
     * @throws OutputWriteFailedException if writing fails.
     */
    void flushAndClose(
            Optional<NamedFeatureStore<FeatureInputResults>> featuresAggregate,
            boolean includeGroups,
            Function<InputOutputContext, FeatureCSVWriterCreator> csvWriterCreator,
            InputOutputContext context)
            throws OutputWriteFailedException;
}
