package org.anchoranalysis.io.output.enabled.single;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Base class for a {@link SingleLevelOutputEnabled} that combines two existing such classes.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SingleLevelBinary implements SingleLevelOutputEnabled {

    /** The first source of output-names that are enabled. */
    private final SingleLevelOutputEnabled enabled1;

    /** The second source of output-names that are enabled. */
    private final SingleLevelOutputEnabled enabled2;

    @Override
    public boolean isOutputEnabled(String outputName) {
        return combine(enabled1.isOutputEnabled(outputName), enabled2.isOutputEnabled(outputName));
    }

    public abstract boolean combine(boolean first, boolean second);
}
