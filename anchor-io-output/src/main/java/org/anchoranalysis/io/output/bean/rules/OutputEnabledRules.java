package org.anchoranalysis.io.output.bean.rules;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;

/**
 * Whether an output is allowed or not in a context of hierarchy of different rules for outputting.
 * 
 * <p>The rules have two levels: 
 * <ul>
 * <li>a first-level (top-most) allowing particular output-names.
 * <li>a second-level, given a particular output-name from the first-level, allowing particular sub-output-names or not.
 * </ul>
 * 
 * <p>e.g. the first-level might allow a collection of images in general or not, and the second-level might allow particular
 * names of particular images or not.
 * 
 * @author Owen Feehan
 *
 */
public abstract class OutputEnabledRules extends AnchorBean<OutputEnabledRules> {
    
    /**
     * Is a particular output (first-level) allowed?
     * 
     * @return a class that indicates whether top-level outputs are allowed
     */
    public abstract OutputAllowed first();
    
    /**
     * A second-level of {@link OutputAllowed} for a particular {@code outputName} as used in {@link #first}.
     */
    public abstract OutputAllowed second(String outputName);
        
    /**
     * Is a particular <b>first-level</b> output-allowed?
     * 
     * @param outputName the name of the output
     * @return true iff the output is allowed
     */
    public boolean isOutputAllowed( String outputName ) {
        return first().isOutputAllowed(outputName);
    }
}
