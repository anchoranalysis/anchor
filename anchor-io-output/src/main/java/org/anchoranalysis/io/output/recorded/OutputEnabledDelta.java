package org.anchoranalysis.io.output.recorded;

import java.util.Optional;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelAnd;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelNot;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOr;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;

/**
 * Additional output-names to enable or disable from an existing set of output-enabled rules.
 *
 * <p>There is an order of application:
 *
 * <ul>
 *   <li>First, any {@code outputsToEnable} are applied.
 *   <li>Second, any {@code outputsToDisable} are disabled.
 * </ul>
 *
 * <p>This means that {@code outputsToDisable} has precedence over any enabling.
 *
 * @author Owen Feehan
 */
public class OutputEnabledDelta {

    /**
     * Output-names that are enabled, before additionally applying whichever other rules are
     * employed.
     *
     * <p>e.g. these can be user-supplied outputs as <i>extras</i> from the command-line.
     */
    private Optional<MultiLevelOutputEnabled> outputsToEnable = Optional.empty();

    /**
     * Output-names that disabled, before additionally applying whichever other rules are employed.
     *
     * <p>e.g. these can be user-supplied outputs as <i>extras</i> from the command-line.
     */
    private Optional<MultiLevelOutputEnabled> outputsToDisable = Optional.empty();

    /**
     * Applies the changes to enable or disable additional outputs, if they are defined.
     *
     * @param source the output-enabled rules before any changes are applied
     * @return output-enabled rules after applying changes
     */
    public MultiLevelOutputEnabled applyDelta(MultiLevelOutputEnabled source) {
        MultiLevelOutputEnabled inputAfterEnable = maybeEnable(source);
        return maybeDisable(inputAfterEnable);
    }

    /**
     * Assigns additional outputs to enable.
     *
     * <p>Note that this can be specific outputs, or it can be rules that permit everything.
     *
     * @param outputs the outputs to add
     */
    public void enableAdditionalOutputs(MultiLevelOutputEnabled outputs) {
        this.outputsToEnable = Optional.of(outputs);
    }

    /**
     * Assigns additional outputs to disable.
     *
     * @param outputs the outputs to add
     */
    public void disableAdditionalOutputs(MultiLevelOutputEnabled outputs) {
        this.outputsToDisable = Optional.of(outputs);
    }

    private MultiLevelOutputEnabled maybeEnable(MultiLevelOutputEnabled source) {
        if (outputsToEnable.isPresent()) {
            return new MultiLevelOr(outputsToEnable.get(), source);
        } else {
            return source;
        }
    }

    private MultiLevelOutputEnabled maybeDisable(MultiLevelOutputEnabled source) {
        if (outputsToDisable.isPresent()) {
            return new MultiLevelAnd(new MultiLevelNot(outputsToDisable.get()), source);
        } else {
            return source;
        }
    }
}
