/*-
 * #%L
 * anchor-math
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

package org.anchoranalysis.spatial.rotation.factory;

import org.anchoranalysis.spatial.rotation.RotationMatrix;

/**
 * Base class for a factory that create a {@link RotationMatrix} in a particular way.
 *
 * <p>This is an implementation of the <a
 * href="https://en.wikipedia.org/wiki/Abstract_factory_pattern">abstract factory</a> pattern.
 *
 * @author Owen Feehan
 */
public abstract class RotationMatrixFactory { // NOSONAR

    /**
     * Creates a {@link RotationMatrix}.
     *
     * @return a newly created matrix.
     */
    public RotationMatrix create() {
        RotationMatrix matrix = new RotationMatrix(numberDimensions());
        populate(matrix);
        return matrix;
    }

    /**
     * The dimensionality of the rotation-matrix.
     *
     * @return 2 or 3
     */
    public abstract int numberDimensions();

    /**
     * Populates a newly created matrix.
     *
     * @param matrix a matrix where all values are 0.
     */
    protected abstract void populate(RotationMatrix matrix);
}
