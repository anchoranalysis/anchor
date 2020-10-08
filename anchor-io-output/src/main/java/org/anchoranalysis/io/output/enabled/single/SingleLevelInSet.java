package org.anchoranalysis.io.output.enabled.single;

import java.util.Set;
import lombok.AllArgsConstructor;

/**
 * Outputs are enabled if they are contained in a set.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class SingleLevelInSet implements SingleLevelOutputEnabled {

    /** The first source of output-names that are enabled. */
    private final Set<String> set;

    @Override
    public boolean isOutputEnabled(String outputName) {
        return set.contains(outputName);
    }
}
