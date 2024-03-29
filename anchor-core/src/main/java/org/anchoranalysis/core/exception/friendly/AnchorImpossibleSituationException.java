package org.anchoranalysis.core.exception.friendly;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

/**
 * A run-time exception to throw in situations which should never logically occur by design.
 *
 * <p>This is conveient to use instead of {@code assert false} as assertions are not always checked
 * by the JVM.
 *
 * <p>If this is somehow thrown, it's an indication there is a logical error in the code.
 *
 * @author Owen Feehan
 */
public class AnchorImpossibleSituationException extends AnchorFriendlyRuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    /** Default constructor. */
    public AnchorImpossibleSituationException() {
        super(
                "This situation should never occur in properly functioning code, as it is should be logically impossible to reach.");

        // As a further warning, and assert is triggered.
        assert (false);
    }

    /**
     * Creates with a specific message explaining the exception.
     *
     * @param message the message.
     */
    public AnchorImpossibleSituationException(String message) {
        super(message);

        // As a further warning, and assert is triggered.
        assert (false);
    }
}
