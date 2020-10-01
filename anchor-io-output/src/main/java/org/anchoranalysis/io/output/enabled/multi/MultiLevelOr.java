package org.anchoranalysis.io.output.enabled.multi;

import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOr;
import lombok.AllArgsConstructor;

/**
 * Outputs are enabled if they are contained in a set <i> or enabled in another {@link MultiLevelOutputEnabled}.
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class MultiLevelOr implements MultiLevelOutputEnabled {

    /** The first source of output-names that are enabled. */
    private final MultiLevelOutputEnabled enabled1;
    
    /** The second source of output-names that are enabled. */
    private final MultiLevelOutputEnabled enabled2;

    @Override
    public boolean isOutputEnabled(String outputName) {
        return enabled1.isOutputEnabled(outputName) || enabled2.isOutputEnabled(outputName);
    }

    @Override
    public SingleLevelOutputEnabled second(String outputName) {
        return new SingleLevelOr(enabled1.second(outputName), enabled2.second(outputName));
    }
}
