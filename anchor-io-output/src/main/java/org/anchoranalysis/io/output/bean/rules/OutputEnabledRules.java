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
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.output.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.SingleLevelOutputEnabled;
import org.anchoranalysis.io.output.bean.enabled.OutputEnabled;

/**
 * Bean that specifies an implementation of {@link MultiLevelOutputEnabled}.
 *
 * @author Owen Feehan
 */
public abstract class OutputEnabledRules extends AnchorBean<OutputEnabledRules>
        implements MultiLevelOutputEnabled {

    /**
     * Is a particular output (first-level) allowed?
     *
     * @return a class that indicates whether top-level outputs are allowed
     */
    public abstract SingleLevelOutputEnabled first();

    /**
     * Is a particular <b>first-level</b> output-allowed?
     *
     * @param outputName the name of the output
     * @return true iff the output is allowed
     */
    @Override
    public boolean isOutputEnabled(String outputName) {
        return first().isOutputEnabled(outputName);
    }
    
    @Override
    public SingleLevelOutputEnabled second(String outputName, SingleLevelOutputEnabled alternative) {
        return selectSecond(outputName).orElse(alternative);
    }
    
    /**
     * A second-level of {@link OutputEnabled} for a particular {@code outputName} as used in
     * first-level.
     * 
     * @return the a matching {@link OutputEnabled} if one is defined for the particular {@code outputName}, otherwise {@link Optional#empty}. 
     */
    protected abstract Optional<SingleLevelOutputEnabled> selectSecond(String outputName);
}
