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
package org.anchoranalysis.io.output.enabled.single;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;

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

    /**
     * Determines whether an output is enabled via a combination of the two {@link
     * MultiLevelOutputEnabled}.
     *
     * @param first whether {@code enabled1} is enabled.
     * @param second whether {@code enabled2} is enabled.
     * @return whether the combined output is enabled.
     */
    protected abstract boolean combine(boolean first, boolean second);
}
