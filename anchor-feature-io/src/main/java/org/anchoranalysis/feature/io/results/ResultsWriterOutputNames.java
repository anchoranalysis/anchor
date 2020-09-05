package org.anchoranalysis.feature.io.results;

import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import lombok.Getter;

/**
 * The customizable output names used by {@link ResultsWriter}, which all follow a pattern based on a prefix.
 * 
 * <p>The group and aggregated outputs can be toggled on and off.
 * 
 * @author Owen Feehan
 *
 */
public class ResultsWriterOutputNames {

    /** The CSV of non-aggregated feature-results */
    @Getter private String csvFeaturesNonAggregated;
    
    /** The CSV of aggregated feature-results, if enabled. */
    @Getter private Optional<String> csvFeaturesAggregated;
        
    /** The CSV individually written for each group, if enabled. */
    @Getter private Optional<String> csvFeaturesGroup;

    /** The name of the XML file outputted with aggregated values for each group, if enabled. */
    @Getter private Optional<String> xmlAggregatedGroup;
    
    /**
     * Creates using default names, equivalent to a prefix of <i>features</i>.
     * 
     * <p>All outputs are enabled.
     */
    public ResultsWriterOutputNames() {
        this("features", true, true);
    }
    
    /**
     * Creates output-names based upon a prefix.
     * 
     * @param prefix prefix used to determine the output-names
     * @param enableAggregated whether to enable the aggregated outputs
     * @param enableGroup whether to enable the group outputs
     */
    public ResultsWriterOutputNames(String prefix, boolean enableAggregated, boolean enableGroup) {
        csvFeaturesNonAggregated = prefix;   // No suffix
        csvFeaturesAggregated = joinIfEnabled(enableAggregated, prefix, "Aggregated");
        csvFeaturesGroup = joinIfEnabled(enableGroup, prefix, "Group");
        xmlAggregatedGroup = joinIfEnabled(enableGroup, prefix, "AggregatedGroup");
    }
    
    private static Optional<String> joinIfEnabled(boolean enabled, String prefix, String suffix) {
        return OptionalUtilities.createFromFlag(enabled, () -> prefix + suffix);
    }
}
