package org.anchoranalysis.io.output.recorded;

import java.util.Optional;
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;
import org.anchoranalysis.io.output.bean.rules.Permissive;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOr;
import lombok.Getter;

/**
 * Rules to determine what outputting occurs, together with maybe an entity that records the output-names that are used when writing / querying.
 * @author Owen Feehan
 *
 */
public class RecordedOutputsWithRules {

    /**
     * If defined, records output-names that are written / not-written during the experiment.
     *
     * <p>This only occurs for first-level outputs, not second-level outputs.
     */
    @Getter
    private final Optional<MultiLevelRecordedOutputs> recordedOutputs;
    
    /**
     * If defined, default output-enabled rules for the particular task.
     * 
     * <p>Note that this object is treated as mutable, and additional outputs may be added.
     */
    private final Optional<OutputEnabledMutable> defaultRules;
    
    /**
     * Enabled-outputs that were always added to whichever rules are employed.
     * 
     * <p>e.g. these can be user-supplied outputs as <i>extras</i> from the command-line.
     */
    private final Optional<OutputEnabledMutable> additionalOutputs;
    
    /**
     * Creates with no rules or outputs defined.
     */
    public RecordedOutputsWithRules() {
        this.recordedOutputs = Optional.empty();
        this.defaultRules = Optional.empty();
        this.additionalOutputs = Optional.empty();
    }
    
    /**
     * Creates with all elements defined.
     * 
     * @param recordedOutputs where output-names are recorded as they are written / queried
     * @param defaultRules default rules for which outputs are enabled.
     */
    public RecordedOutputsWithRules(MultiLevelRecordedOutputs recordedOutputs, OutputEnabledMutable defaultRules, Optional<OutputEnabledMutable> additionalOutputs) {
        this.recordedOutputs = Optional.of(recordedOutputs);
        this.defaultRules = Optional.of(defaultRules);
        this.additionalOutputs = additionalOutputs;
    }
    
    /**
     * Selects which {@link OutputEnabledRules} to employ.
     *
     * <p>The order of precedence is:
     *
     * <ol>
     *   <li>{@code rulesHigherPrecedence} combined with user-supplied additions.
     *   <li>{@code defaultRules} combined with user-supplied additions.
     * </ol>
     * 
     * <p>Any user-supplied additional outputs are also added to both if they exist.
     *
     * @param rulesHigherPrecedence output-rules defined in the experiment, if they exist.
     * @return a {@link MultiLevelOutputEnabled} that combines one of the two sources of rules with user-supplied additional outputs.
     */
    public MultiLevelOutputEnabled selectOutputEnabled(
            Optional<OutputEnabledRules> rulesHigherPrecedence) {
        if (rulesHigherPrecedence.isPresent()) {
            return additionalCombinedWith(rulesHigherPrecedence.get());
        } else if (defaultRules.isPresent()) {
            return additionalCombinedWith(defaultRules.get());
        } else {
            return Permissive.INSTANCE;
        }
    }
    
    private MultiLevelOutputEnabled additionalCombinedWith( OutputEnabledMutable outputEnabled ) {
        additionalOutputs.ifPresent(outputEnabled::addEnabledOutputs);
        return outputEnabled;
    }
    
    private MultiLevelOutputEnabled additionalCombinedWith( OutputEnabledRules outputEnabledRules ) {
        if (additionalOutputs.isPresent()) {
            return new MultiLevelOr(additionalOutputs.get(), outputEnabledRules);
        } else {
            return outputEnabledRules;
        }
    }
}
