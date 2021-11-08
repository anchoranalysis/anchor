package org.anchoranalysis.feature.io.results.calculation;

import java.util.Optional;
import org.anchoranalysis.feature.io.csv.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.csv.FeatureCSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * To facilitate delayed creation of a {@link FeatureCSVWriter}.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface FeatureCSVWriterCreator {

    /**
     * Maybe creates a {@link FeatureCSVWriter} corresponding to particular metadata.
     *
     * @param metadata the metadata.
     * @return the writer, if created.
     * @throws OutputWriteFailedException if the operation cannot be successfully created.
     */
    Optional<FeatureCSVWriter> create(FeatureCSVMetadata metadata)
            throws OutputWriteFailedException;
}
