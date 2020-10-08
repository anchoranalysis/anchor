package org.anchoranalysis.io.output.enabled.multi;

import lombok.AllArgsConstructor;
import org.anchoranalysis.io.output.enabled.single.SingleLevelNot;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;

/**
 * The complement of an existing {@link MultiLevelOutputEnabled} where disabled outputs are enabled,
 * and vice-versa.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class MultiLevelNot implements MultiLevelOutputEnabled {

    /** The {@link MultiLevelOutputEnabled} to be complemented. */
    private final MultiLevelOutputEnabled source;

    @Override
    public boolean isOutputEnabled(String outputName) {
        return !source.isOutputEnabled(outputName);
    }

    @Override
    public SingleLevelOutputEnabled second(String outputName) {
        return new SingleLevelNot(source.second(outputName));
    }
}
