package org.anchoranalysis.feature.io.csv.table;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.results.ResultsVectorList;

/**
 * Creates a {@link FeatureTableCSVGenerator}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureTableCSVGeneratorFactory {

    /**
     * Creates either a {@link FeatureTableCSVGenerator} in either horizontal or vertical style.
     *
     * @param manifestFunction identifier of function for the manifest file.
     * @param featureNames names-of-features that will appear in results.
     * @param horizontal if true, horizontal style is used (an entity's results form a row),
     *     otherwise vertical style (an entity's results form a column).
     * @return a newly created {@link FeatureTableCSVGenerator} in the above selected style.
     */
    public static FeatureTableCSVGenerator<ResultsVectorList> create(
            String manifestFunction, FeatureNameList featureNames, boolean horizontal) {
        if (horizontal) {
            return new HorizontalValues(manifestFunction, featureNames);
        } else {
            return new VerticalValues(manifestFunction, featureNames);
        }
    }
}
