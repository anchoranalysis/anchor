/*-
 * #%L
 * anchor-spatial
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
package org.anchoranalysis.spatial.orientation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cern.colt.matrix.DoubleMatrix2D;
import lombok.AllArgsConstructor;

/**
 * Shortcut means of asserting a particular value in the matrix.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class MatrixAsserter {

    private static final double DELTA_ROTATION_MATRIX = 1e-3;

    private final DoubleMatrix2D matrix;

    public void assertValue(double expectedValue, int indexRow, int indexColumn) {
        assertEquals(expectedValue, matrix.get(indexRow, indexColumn), DELTA_ROTATION_MATRIX);
    }
}
