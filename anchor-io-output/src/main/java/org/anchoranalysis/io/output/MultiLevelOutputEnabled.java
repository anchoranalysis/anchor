package org.anchoranalysis.io.output;

import org.anchoranalysis.io.output.bean.enabled.OutputEnabled;

/**
 * Whether an output is enabled or not in a context of hierarchy of different rules for outputting.
 *
 * <p>The rules have two levels:
 *
 * <ul>
 *   <li>a first-level (top-most) enabling particular output-names.
 *   <li>a second-level, given a particular output-name from the first-level, enabling particular
 *       sub-output-names or not.
 * </ul>
 *
 * <p>e.g. the first-level might enable a collection of images in general or not, and the
 * second-level might enable particular names of particular images or not.
 *
 * @author Owen Feehan
 */
public interface MultiLevelOutputEnabled {

    /**
     * Is a particular <b>first-level</b> output-enabled?
     *
     * @param outputName the name of the output
     * @return true iff the output is allowed
     */
    boolean isOutputEnabled(String outputName);

    /**
     * A second-level of {@link OutputEnabled} for a particular {@code outputName} as used in
     * first-level.
     */
    OutputEnabled second(String outputName);
}
