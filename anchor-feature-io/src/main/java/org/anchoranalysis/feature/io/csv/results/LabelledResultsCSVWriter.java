package org.anchoranalysis.feature.io.csv.results;

import java.util.Optional;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.csv.metadata.FeatureCSVMetadataForOutput;
import org.anchoranalysis.feature.io.results.LabelledResultsVector;
import org.anchoranalysis.io.generator.tabular.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows results to be written to a {@link FeatureCSVWriter}, possibly adding further processing.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LabelledResultsCSVWriter {

    // START REQUIRED ARGUMENTS
    /** Metadata defining which CSV file to write to, and which headers. */
    protected final FeatureCSVMetadataForOutput outputMetadata;

    /** Creates a {@link FeatureCSVWriter} for writing the non-aggregated feature results. */
    private final FeatureCSVWriterFactory writerCreator;

    /**
     * After adding a {@link LabelledResultsVector}, this function is also called, if it is defined.
     */
    private final Optional<Consumer<LabelledResultsVector>> consumeAfterAdding;
    // END REQUIRED ARGUMENTS

    /**
     * Starts writing the CSV file.
     *
     * <p>This should be called once, <b>before any calls</b> to {@link
     * #add(LabelledResultsVector)}.
     *
     * @throws OutputWriteFailedException if the output file cannot be created.
     */
    public abstract void start() throws OutputWriteFailedException;

    /**
     * Adds a results-element to be written.
     *
     * <p>This should be called once for each set of results to be written.
     *
     * @param results the results.
     */
    public abstract void add(LabelledResultsVector results);

    /**
     * Stops writing the CSV file.
     *
     * <p>This should be called once, <b>after all calls</b> to {@link #add(LabelledResultsVector)}.
     *
     * @throws OutputWriteFailedException if the output file cannot be created.
     */
    public abstract void end() throws OutputWriteFailedException;

    /**
     * Creates a {@link CSVWriter}.
     *
     * @param csvMetadata the metadata for the writer.
     * @return the newly-created writer, if possible.
     * @throws OutputWriteFailedException if the CSV file cannot be created successfully.
     */
    protected Optional<FeatureCSVWriter> createWriter(FeatureCSVMetadata csvMetadata)
            throws OutputWriteFailedException {
        return writerCreator.create(csvMetadata);
    }

    /**
     * Consumes the {@link LabelledResultsVector} if {@code consumeAfterAdding} is present.
     *
     * @param results the results to maybe consume.
     */
    protected void maybeConsumeResults(LabelledResultsVector results) {
        consumeAfterAdding.ifPresent(consumer -> consumer.accept(results));
    }
}
