package org.anchoranalysis.io.output.enabled.multi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MultiLevelBinary implements MultiLevelOutputEnabled {

    /** The first source of output-names that are enabled. */
    private final MultiLevelOutputEnabled enabled1;

    /** The second source of output-names that are enabled. */
    private final MultiLevelOutputEnabled enabled2;

    @Override
    public boolean isOutputEnabled(String outputName) {
        return combine(enabled1.isOutputEnabled(outputName), enabled2.isOutputEnabled(outputName));
    }

    @Override
    public SingleLevelOutputEnabled second(String outputName) {
        return combineSecond(enabled1.second(outputName), enabled2.second(outputName));
    }

    protected abstract boolean combine(boolean first, boolean second);

    protected abstract SingleLevelOutputEnabled combineSecond(
            SingleLevelOutputEnabled first, SingleLevelOutputEnabled second);
}
