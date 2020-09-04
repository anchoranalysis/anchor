package org.anchoranalysis.feature.io.csv.results.group;

import java.util.Optional;
import org.anchoranalysis.feature.calculate.results.ResultsVectorCollection;
import org.anchoranalysis.feature.io.csv.name.MultiName;
import org.anchoranalysis.feature.io.csv.writer.FeatureListCSVGeneratorHorizontal;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.bound.CacheSubdirectoryContext;
import lombok.AllArgsConstructor;


/**
 * Writes the aggregated results for a single group as CSV to the filesystem.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class WriteCSVForGroup {
    
    private static final String MANIFEST_FUNCTION_FEATURES_GROUP = "groupedFeatureResults";

    private String outputName;
    private FeatureNameList featureNames;
    private CacheSubdirectoryContext context;
    
    public void write( Optional<MultiName> groupName, ResultsVectorCollection results) {
        if (groupName.isPresent()) {
            writeGroupFeatures(
                    context.get(groupName.map(MultiName::toString)).getOutputManager(),
                    results);
        }
    }
    
    
    /** Writes a table of features in CSV for a particular group */
    private void writeGroupFeatures(
            BoundOutputManagerRouteErrors outputManager,
            ResultsVectorCollection results) {
        outputManager
                .getWriterCheckIfAllowed()
                .write(
                        outputName,
                        () ->
                                new FeatureListCSVGeneratorHorizontal(
                                        MANIFEST_FUNCTION_FEATURES_GROUP, featureNames, results));
    }
}
