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

/**
 * Outputs are enabled if they are contained in <b>both</b> of two {@link SingleLevelOutputEnabled}.
 *
 * @author Owen Feehan
 */
public class SingleLevelAnd extends SingleLevelBinary {

    /**
     * Creates using two {@link SingleLevelOutputEnabled}s.
     *
     * @param enabled1 the first source of output-names that are enabled
     * @param enabled2 the second source of output-names that are enabled
     */
    public SingleLevelAnd(SingleLevelOutputEnabled enabled1, SingleLevelOutputEnabled enabled2) {
        super(enabled1, enabled2);
    }

    @Override
    public boolean combine(boolean first, boolean second) {
        return first && second;
    }
}
