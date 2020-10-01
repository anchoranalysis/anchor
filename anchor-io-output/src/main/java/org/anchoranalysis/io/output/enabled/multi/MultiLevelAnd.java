package org.anchoranalysis.io.output.enabled.multi;

import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;
import org.anchoranalysis.io.output.enabled.single.SingleLevelAnd;

/**
 * Outputs are enabled if they are contained in <b>both</b> of two {@link MultiLevelOutputEnabled}s.
 * 
 * @author Owen Feehan
 *
 */
public class MultiLevelAnd extends MultiLevelBinary {

    /**
     * Creates using two {@link MultiLevelOutputEnabled}s.
     * 
     * @param enabled1 the first source of output-names that are enabled.
     * @param enabled2 the second source of output-names that are enabled.
     */
    public MultiLevelAnd(MultiLevelOutputEnabled enabled1, MultiLevelOutputEnabled enabled2) {
        super(enabled1, enabled2);
    }

    @Override
    protected boolean combine(boolean first, boolean second) {
        return first && second;
    }

    @Override
    protected SingleLevelOutputEnabled combineSecond(SingleLevelOutputEnabled first,
            SingleLevelOutputEnabled second) {
        return new SingleLevelAnd(first, second);
    }
}
