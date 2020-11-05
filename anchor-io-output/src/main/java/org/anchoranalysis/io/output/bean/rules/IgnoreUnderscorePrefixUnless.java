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

package org.anchoranalysis.io.output.bean.rules;

import java.util.Optional;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.io.output.bean.enabled.IgnoreUnderscorePrefix;
import org.anchoranalysis.io.output.bean.enabled.OutputEnabled;
import org.anchoranalysis.io.output.bean.enabled.SpecificEnabled;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;

/**
 * Like {@link IgnoreUnderscorePrefix} for all first and level-outputs unless a particular outputs
 * are explicitly specified.
 *
 * <p>If first-level are specified, this takes precedence, and only these outputs are allowed.
 *
 * <p>Similarly if any particular second-level outputs are specified, these replace {@link
 * IgnoreUnderscorePrefix}.
 *
 * <p>Otherwise {@link IgnoreUnderscorePrefix} is used.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class IgnoreUnderscorePrefixUnless extends OutputEnableRulesSpecify {

    private static final OutputEnabled OTHER = IgnoreUnderscorePrefix.INSTANCE;

    private class IgnoreUnderscorePrefixPlusImplementation implements MultiLevelOutputEnabled {

        @Override
        public boolean isOutputEnabled(String outputName) {
            if (isFirstDefined()) {
                return firstLevelContains(outputName);
            } else {
                return OTHER.isOutputEnabled(outputName);
            }
        }

        @Override
        public SingleLevelOutputEnabled second(String outputName) {
            return secondLevelOutputs(outputName, OTHER);
        }
    }

    /**
     * Create with first-level output names
     *
     * @param first first-level output-names
     */
    public IgnoreUnderscorePrefixUnless(StringSet first) {
        super(first);
    }

    @Override
    public MultiLevelOutputEnabled create(Optional<MultiLevelOutputEnabled> defaultRules) {
        return new IgnoreUnderscorePrefixPlusImplementation();
    }

    @Override
    protected SingleLevelOutputEnabled createSecondLevelFromSet(StringSet outputNames) {
        return new SpecificEnabled(outputNames);
    }
}
