package org.anchoranalysis.io.output.bean.rules;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.output.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.bean.enabled.OutputEnabled;

/**
 * Bean that specifies an implementation of {@link MultiLevelOutputEnabled}.
 *
 * @author Owen Feehan
 */
public abstract class OutputEnabledRules extends AnchorBean<OutputEnabledRules>
        implements MultiLevelOutputEnabled {

    /**
     * Is a particular output (first-level) allowed?
     *
     * @return a class that indicates whether top-level outputs are allowed
     */
    public abstract OutputEnabled first();

    /**
     * Is a particular <b>first-level</b> output-allowed?
     *
     * @param outputName the name of the output
     * @return true iff the output is allowed
     */
    @Override
    public boolean isOutputEnabled(String outputName) {
        return first().isOutputAllowed(outputName);
    }
}
