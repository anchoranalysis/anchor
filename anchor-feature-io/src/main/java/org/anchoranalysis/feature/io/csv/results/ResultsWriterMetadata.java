package org.anchoranalysis.feature.io.csv.results;

import java.util.Optional;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.feature.io.csv.writer.FeatureCSVMetadata;
import org.anchoranalysis.feature.list.NamedFeatureStore;
import org.anchoranalysis.feature.name.FeatureNameList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Additional information needed for the outputs in {@link ResultsWriter}.
 * 
 * <p>i.e. headers, the output-name etc.
 * 
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor @Accessors(fluent=true)
public class ResultsWriterMetadata {

    /**
     * Names for any outputs produced by {@link ResultsWriter}.
     */
    @Getter private final ResultsWriterOutputNames outputNames;
            
    /**
     * Headers for the CSV File
     */
    private final LabelHeaders labelHeaders;
    
    /**
     * Names of each feature in the feature columns.
     */
    @Getter private final FeatureNameList featureNamesNonAggregate;
    
    public ResultsWriterMetadata(LabelHeaders labelHeaders, FeatureNameList featureNamesNonAggregate) {
        this.outputNames = new ResultsWriterOutputNames();
        this.labelHeaders = labelHeaders;
        this.featureNamesNonAggregate = featureNamesNonAggregate;
    }   
        
    /**
     * Derives metadata for the non-aggregated CSV output.
     * 
     * @return newly created metadata
     */
    public FeatureCSVMetadata metadataNonAggregated() {
        return createMetadata(outputNames.getCsvFeaturesNonAggregated(), labelHeaders.allHeaders(), featureNamesNonAggregate.shallowCopy());
    }
    
    /**
     * Derives metadata for the aggregated CSV output, if enabled.
     * 
     * @return newly created metadata, or {@code Optional.empty()} if its disabled.
     */
    public Optional<FeatureCSVMetadata> metadataAggregated(NamedFeatureStore<FeatureInputResults> featuresAggregate) {
        return outputNames.getCsvFeaturesAggregated().map( outputName ->
            createMetadata(outputName, labelHeaders.getGroupHeaders(), featuresAggregate.createFeatureNames())
        );
    }
    
    private static FeatureCSVMetadata createMetadata(String outputName, String[] nonFeatureHeaders, FeatureNameList featureNamesNewlyCreated) {
        featureNamesNewlyCreated.insertBeginning(nonFeatureHeaders);
        return new FeatureCSVMetadata(outputName, featureNamesNewlyCreated.asList() );        
    }
}
