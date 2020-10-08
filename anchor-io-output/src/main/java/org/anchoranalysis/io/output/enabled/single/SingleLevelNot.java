package org.anchoranalysis.io.output.enabled.single;

import lombok.AllArgsConstructor;

/**
 * The complement of an existing {@link SingleLevelOutputEnabled} where disabled outputs are
 * enabled, and vice-versa.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class SingleLevelNot implements SingleLevelOutputEnabled {

    /** The {@link SingleLevelOutputEnabled} to be complemented. */
    private final SingleLevelOutputEnabled source;

    @Override
    public boolean isOutputEnabled(String outputName) {
        return !source.isOutputEnabled(outputName);
    }
}
