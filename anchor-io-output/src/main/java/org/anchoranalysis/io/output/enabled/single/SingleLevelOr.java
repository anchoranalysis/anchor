package org.anchoranalysis.io.output.enabled.single;

import lombok.AllArgsConstructor;

/**
 * Outputs are enabled if they are contained in either of two {@link SingleLevelOutputEnabled}.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class SingleLevelOr implements SingleLevelOutputEnabled {

    /** The first source of output-names that are enabled. */
    private final SingleLevelOutputEnabled enabled1;
    
    /** The second source of output-names that are enabled. */
    private final SingleLevelOutputEnabled enabled2;

    @Override
    public boolean isOutputEnabled(String outputName) {
        return enabled1.isOutputEnabled(outputName) || enabled2.isOutputEnabled(outputName);
    }
}
