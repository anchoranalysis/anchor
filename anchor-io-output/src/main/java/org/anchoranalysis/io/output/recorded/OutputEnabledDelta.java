/*-
 * #%L
 * anchor-io-output
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.io.output.recorded;

import java.util.Optional;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelAnd;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelNot;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOr;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;

/**
 * Additional output-names to enable or disable from an existing set of output-enabled rules.
 *
 * <p>There is an order of application:
 *
 * <ul>
 *   <li>First, any {@code outputsToEnable} are applied.
 *   <li>Second, any {@code outputsToDisable} are disabled.
 * </ul>
 *
 * <p>This means that {@code outputsToDisable} has precedence over any enabling.
 *
 * @author Owen Feehan
 */
public class OutputEnabledDelta {

    /**
     * Output-names that are enabled, before additionally applying whichever other rules are
     * employed.
     *
     * <p>e.g. these can be user-supplied outputs as <i>extras</i> from the command-line.
     */
    private Optional<MultiLevelOutputEnabled> outputsToEnable = Optional.empty();

    /**
     * Output-names that disabled, before additionally applying whichever other rules are employed.
     *
     * <p>e.g. these can be user-supplied outputs as <i>extras</i> from the command-line.
     */
    private Optional<MultiLevelOutputEnabled> outputsToDisable = Optional.empty();

    /**
     * Applies the changes to enable or disable additional outputs, if they are defined.
     *
     * @param source the output-enabled rules before any changes are applied
     * @return output-enabled rules after applying changes
     */
    public MultiLevelOutputEnabled applyDelta(MultiLevelOutputEnabled source) {
        MultiLevelOutputEnabled inputAfterEnable = maybeEnable(source);
        return maybeDisable(inputAfterEnable);
    }

    /**
     * Assigns additional outputs to enable.
     *
     * <p>Note that this can be specific outputs, or it can be rules that permit everything.
     *
     * @param outputs the outputs to add
     */
    public void enableAdditionalOutputs(MultiLevelOutputEnabled outputs) {
        this.outputsToEnable = Optional.of(outputs);
    }

    /**
     * Assigns additional outputs to disable.
     *
     * @param outputs the outputs to add
     */
    public void disableAdditionalOutputs(MultiLevelOutputEnabled outputs) {
        this.outputsToDisable = Optional.of(outputs);
    }

    private MultiLevelOutputEnabled maybeEnable(MultiLevelOutputEnabled source) {
        if (outputsToEnable.isPresent()) {
            return new MultiLevelOr(outputsToEnable.get(), source);
        } else {
            return source;
        }
    }

    private MultiLevelOutputEnabled maybeDisable(MultiLevelOutputEnabled source) {
        if (outputsToDisable.isPresent()) {
            return new MultiLevelAnd(new MultiLevelNot(outputsToDisable.get()), source);
        } else {
            return source;
        }
    }
}
