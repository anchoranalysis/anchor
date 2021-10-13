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

import cern.colt.matrix.DoubleMatrix2D;
import lombok.AllArgsConstructor;
import org.anchoranalysis.spatial.rotation.RotationMatrix;

/**
 * Creates a {@link RotationMatrix} that performs a 2D rotation in a plane.
 *  
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class RotateAngle2D extends RotationMatrixFactory {

    /** The angle in radians that defines the rotation. */
    private double angleRadians;

    @Override
    public void populate(RotationMatrix rotMatrix) {

        DoubleMatrix2D matrix = rotMatrix.getMatrix();

        IndexCalculator index = new IndexCalculator(0, 2);

        matrix.set(index.calculate(0), index.calculate(0), Math.cos(angleRadians));
        matrix.set(index.calculate(0), index.calculate(1), -1 * Math.sin(angleRadians));
        matrix.set(index.calculate(1), index.calculate(0), Math.sin(angleRadians));
        matrix.set(index.calculate(1), index.calculate(1), Math.cos(angleRadians));
    }

    @Override
    public int numberDimensions() {
        return 2;
    }
}
