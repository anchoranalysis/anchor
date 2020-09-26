package org.anchoranalysis.io.output.bean.rules;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;

/**
 * Whether an output is allowed or not in a context of hierarchy of different rules for outputting.
 * 
 * @author Owen Feehan
 *
 */
public abstract class OutputEnabledRules extends AnchorBean<OutputEnabledRules> {
    
    public abstract boolean isOutputAllowed(String outputName);

    /**
     * A second-level of OutputAllowed for a particular key, or null if none is defined for this key
     */
    public abstract OutputAllowed outputAllowedSecondLevel(String key);
}
