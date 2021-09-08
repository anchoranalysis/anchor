/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.mpp.mark.conic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CheckedUnsupportedOperationException;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * Utilities to help scale {@link Mark}s.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ScaleChecker {

    /**
     * Throws an exception of {@code scaleFactor} does not have identical X and Y components.
     *
     * @param scaleFactor the scaleFactor to check
     * @throws CheckedUnsupportedOperationException if the X and Y values of {@code scaleFactor}
     *     differ.
     */
    public static void checkIdenticalXY(ScaleFactor scaleFactor)
            throws CheckedUnsupportedOperationException {
        if (!scaleFactor.hasIdenticalXY()) {
            throw new CheckedUnsupportedOperationException(
                    "This operation is only supported if the scaleFactor is identical in X and Y dimensions.");
        }
    }
}
