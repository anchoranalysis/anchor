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
import lombok.Getter;
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;
import org.anchoranalysis.io.output.bean.rules.Permissive;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;

/**
 * Rules to determine what outputting occurs, together with maybe an entity that records the
 * output-names that are used when writing / querying.
 *
 * @author Owen Feehan
 */
public class RecordedOutputsWithRules {

    /**
     * If defined, records output-names that are written / not-written during the experiment.
     *
     * <p>This only occurs for first-level outputs, not second-level outputs.
     */
    @Getter private final Optional<MultiLevelRecordedOutputs> recordedOutputs;

    /**
     * If defined, default output-enabled rules for the particular task.
     *
     * <p>Note that this object is treated as mutable, and additional outputs may be added.
     */
    private final Optional<MultiLevelOutputEnabled> defaultRules;

    /**
     * Enabled-outputs that were always added to whichever rules are employed.
     *
     * <p>e.g. these can be user-supplied outputs as <i>extras</i> from the command-line.
     */
    private final Optional<OutputEnabledDelta> delta;

    /** Creates with no rules or outputs defined. */
    public RecordedOutputsWithRules() {
        this.recordedOutputs = Optional.empty();
        this.defaultRules = Optional.empty();
        this.delta = Optional.empty();
    }

    /**
     * Creates with all elements defined.
     *
     * @param recordedOutputs where output-names are recorded as they are written / queried
     * @param defaultRules default rules for which outputs are enabled.
     */
    public RecordedOutputsWithRules(
            MultiLevelRecordedOutputs recordedOutputs,
            MultiLevelOutputEnabled defaultRules,
            OutputEnabledDelta delta) {
        this.recordedOutputs = Optional.of(recordedOutputs);
        this.defaultRules = Optional.of(defaultRules);
        this.delta = Optional.of(delta);
    }

    /**
     * Selects which {@link OutputEnabledRules} to employ.
     *
     * <p>The order of precedence is:
     *
     * <ol>
     *   <li>{@code rulesHigherPrecedence} combined with user-supplied additions.
     *   <li>{@code defaultRules} combined with user-supplied additions.
     * </ol>
     *
     * <p>Any user-supplied additional outputs are also added to both if they exist.
     *
     * @param rulesHigherPrecedence output-rules defined in the experiment, if they exist.
     * @return a {@link MultiLevelOutputEnabled} that combines one of the two sources of rules with
     *     user-supplied additional outputs.
     */
    public MultiLevelOutputEnabled selectOutputEnabled(
            Optional<OutputEnabledRules> rulesHigherPrecedence) {
        if (rulesHigherPrecedence.isPresent()) {
            return additionalCombinedWith(rulesHigherPrecedence.get().create(defaultRules));
        } else if (defaultRules.isPresent()) {
            return additionalCombinedWith(defaultRules.get());
        } else {
            return Permissive.INSTANCE;
        }
    }

    private MultiLevelOutputEnabled additionalCombinedWith(MultiLevelOutputEnabled other) {
        if (delta.isPresent()) {
            return delta.get().applyDelta(other);
        } else {
            return other;
        }
    }
}
