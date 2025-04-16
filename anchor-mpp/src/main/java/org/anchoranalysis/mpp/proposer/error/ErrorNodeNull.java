/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.proposer.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A singleton implementation of ErrorNode that does nothing with errors.
 *
 * <p>This class implements the Null Object pattern for ErrorNode, allowing
 * error handling to be safely ignored when desired.</p>
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorNodeNull extends ErrorNode {

    private static final long serialVersionUID = 5512508477564211253L;

    /** The single instance of ErrorNodeNull. */
    private static ErrorNodeNull instance = new ErrorNodeNull();

    /**
     * Gets the singleton instance of ErrorNodeNull.
     *
     * @return the ErrorNodeNull instance
     */
    public static ErrorNodeNull instance() {
        return instance;
    }

    @Override
    public ErrorNode add(String errorMessage) {
        // do nothing
        return this;
    }

    @Override
    public ErrorNode addFormatted(String formatString, Object... args) {
        return this;
    }

    @Override
    public ErrorNode add(Exception e) {
        return this;
    }

    @Override
    public void addErrorDescription(StringBuilder sb) {
        // NOTHING TO DO
    }
}