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
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RotateAngle3D extends RotationMatrixFactory {

    /**
    * Rotation about X-axis (in radians).
    */
    private double angleX;
    
    /**
    * Rotation about Y-axis (in radians).
    */
    private double angleY;
    
    /**
    * Rotation about Z-axis (in radians).
    */
    private double angleZ;

    @Override
    public void populate(RotationMatrix rotMatrix) {

        DoubleMatrix2D matrix = rotMatrix.getMatrix();

        final int matNumDim = 3;

        DoubleFactory2D f = DoubleFactory2D.dense;

        DoubleMatrix2D matTemporary = f.make(matNumDim, matNumDim);
        DoubleMatrix2D matX = f.make(matNumDim, matNumDim);
        DoubleMatrix2D matY = f.make(matNumDim, matNumDim);
        DoubleMatrix2D matZ = f.make(matNumDim, matNumDim);

        assgnRotationMatrix(matX, this.angleX, 0);
        assgnRotationMatrix(matY, this.angleY, 1);
        assgnRotationMatrix(matZ, this.angleZ, 2);

        matX.zMult(matY, matTemporary);
        matTemporary.zMult(matZ, matrix);
    }

    @Override
    public int numberDimensions() {
        return 3;
    }

    /**
     * Assigns values for a rotation about a particular axis (in radians)
     * 
     * @param matrix
     * @param angleRadians
     * @param axisShift 0 for x-axis, 1 for y-axis, 2 for z-axis
     */
    private static void assgnRotationMatrix(DoubleMatrix2D matrix, double angleRadians, int axisShift) {

        IndexCalculator index = new IndexCalculator(axisShift, 3);

        matrix.assign(0);
        matrix.set(index.calculate(1), index.calculate(1), Math.cos(angleRadians));
        matrix.set(index.calculate(2), index.calculate(1), Math.sin(angleRadians));
        matrix.set(index.calculate(1), index.calculate(2), -1 * Math.sin(angleRadians));
        matrix.set(index.calculate(2), index.calculate(2), Math.cos(angleRadians));
        matrix.set(index.calculate(0), index.calculate(0), 1);
    }

}
