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
import org.anchoranalysis.bean.primitive.StringSet;
import org.anchoranalysis.io.output.bean.enabled.All;
import org.anchoranalysis.io.output.bean.enabled.SpecificDisabled;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.enabled.single.SingleLevelOutputEnabled;

/**
 * All outputs are enabled except particular ones.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class PermissiveExcept extends OutputEnableRulesSpecify {

    private class PermissiveExceptImplementation implements MultiLevelOutputEnabled {

        @Override
        public boolean isOutputEnabled(String outputName) {
            return !firstLevelContains(outputName);
        }

        @Override
        public SingleLevelOutputEnabled second(String outputName) {
            return secondLevelOutputs(outputName, All.INSTANCE);
        }
    }

    /**
     * Create with first-level output names
     *
     * @param first first-level output-names
     */
    public PermissiveExcept(StringSet first) {
        super(first);
    }

    @Override
    public MultiLevelOutputEnabled create(Optional<MultiLevelOutputEnabled> defaultRules) {
        return new PermissiveExceptImplementation();
    }

    @Override
    protected SingleLevelOutputEnabled createSecondLevelFromSet(StringSet outputNames) {
        return new SpecificDisabled(outputNames);
    }
}
