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
package org.anchoranalysis.io.output;

import org.anchoranalysis.io.output.bean.enabled.OutputEnabled;

/**
 * Whether an output is enabled or not in a context of hierarchy of different rules for outputting.
 *
 * <p>The rules have two levels:
 *
 * <ul>
 *   <li>a first-level (top-most) enabling particular output-names via a {@link SingleLevelOutputEnabled}.
 *   <li>a second-level, given a particular output-name from the first-level, enabling particular
 *       sub-output-names or not.
 * </ul>
 *
 * <p>e.g. the first-level might enable a collection of images in general or not, and the
 * second-level might enable particular names of particular images or not.
 *
 * @author Owen Feehan
 */
public interface MultiLevelOutputEnabled extends SingleLevelOutputEnabled {

    /**
     * A second-level of {@link OutputEnabled} for a particular {@code outputName} as used in
     * first-level.
     */
    OutputEnabled second(String outputName);
}
