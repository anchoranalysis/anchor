package org.anchoranalysis.feature.io.csv.results.group;

import java.util.Optional;
import org.anchoranalysis.feature.calculate.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.csv.results.ResultsWriterMetadata;
import org.anchoranalysis.feature.io.csv.writer.FeatureCSVMetadata;
import org.anchoranalysis.feature.io.csv.writer.FeatureCSVWriter;
import org.anchoranalysis.feature.io.csv.writer.RowLabels;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.io.output.bound.CacheSubdirectoryContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupWriter {
    
    // START REQUIRED ARGUMENTS
    private final ResultsWriterMetadata outputMetadata;
    // END REQUIRED ARGUMENTS

    /**
     * A map which stores an aggregate structure for all entries (based on their unique names) and
     * also on an aggregation-key extracted from the name
     */
    private ResultsMap map = new ResultsMap();
    

    /**
     * Adds a result to the group-writer, but doesn't write yet.
     * 
     * @param labels labels for the result
     * @param results the results
     */
    public void addResultsFor(RowLabels labels, ResultsVector results) {
        map.addResultsFor(labels, results);
    }
    
    /**
     * Writes outputs for groups that have been previously added with {@link #addResultsFor}.
     * 
     * @param featuresAggregate
     * @param includeGroups
     * @param context
     * @param contextGroups
     * @throws AnchorIOException
     */
    public void writeGroupResults(Optional<NamedFeatureStore<FeatureInputResults>> featuresAggregate, boolean includeGroups,
            BoundIOContext context, CacheSubdirectoryContext contextGroups) throws AnchorIOException {
        if (includeGroups) {
            writeAllGroups(contextGroups);
        }

        if (featuresAggregate.isPresent()) {
            writeAggregated(featuresAggregate.get(), context, contextGroups);
        }
    }

    private void writeAllGroups(CacheSubdirectoryContext context) {
        outputMetadata.outputNames().getCsvFeaturesGroup().ifPresent( outputName -> {
            WriteCSVForGroup groupedCSVWriter = new WriteCSVForGroup(
                    outputName,
                    outputMetadata.featureNamesNonAggregate(),
                    context
            );
            map.iterateResults(groupedCSVWriter::write);
        });
    }

    private void writeAggregated(
            NamedFeatureStore<FeatureInputResults> featuresAggregate,
            BoundIOContext context,
            CacheSubdirectoryContext contextGroups)
            throws AnchorIOException {
        
        if (map.isEmpty()) {
            // NOTHING TO DO, exit early
            return;
        }
        
        Optional<FeatureCSVMetadata> csvMetadata = outputMetadata.metadataAggregated(featuresAggregate); 
        
        if (!csvMetadata.isPresent()) {
            // NOTHING TO DO, exit early
            return;
        }

        Optional<FeatureCSVWriter> csvWriter =
                FeatureCSVWriter.create(csvMetadata.get(), context.getOutputManager());

        try {
            map.iterateResults( (groupName,results) -> {
                if (results.size() != 0) {
                    new WriteXMLForGroup(featuresAggregate, results).maybeWrite(groupName, outputMetadata, csvWriter, contextGroups);
                }
            });
        } finally {
            csvWriter.ifPresent(FeatureCSVWriter::close);
        }
    }
}
