package org.anchoranalysis.feature.io.csv.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.feature.name.FeatureNameList;

/**
 * Like a {@link FeatureCSVMetadata} but additionally associates an output-name.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class FeatureCSVMetadataForOutput {

    /** Headers for the CSV File */
    @Getter private final String[] nonFeatureHeaders;

    /** Names of each feature in the feature columns. */
    @Getter private final FeatureNameList featureNames;

    /** Names for the produced output. */
    @Getter private final String outputName;

    /**
     * Derives metadata for writing to a CSV file
     *
     * @return newly created metadata
     */
    public FeatureCSVMetadata metadata() {
        return new FeatureCSVMetadata(
                outputName, nonFeatureHeaders, featureNames.duplicateShallow());
    }
}
