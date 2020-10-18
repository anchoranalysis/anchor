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

package org.anchoranalysis.spatial.rotation;

import org.anchoranalysis.spatial.point.Vector3d;
import cern.colt.matrix.DoubleMatrix2D;

public class RotationMatrixFromAxisAngleCreator extends RotationMatrixCreator {

    private Vector3d point;
    private double angle;

    // assumes point is a unit-vector
    public RotationMatrixFromAxisAngleCreator(Vector3d point, double angle) {
        super();
        this.point = point;
        this.angle = angle;
    }

    @Override
    public void createRotationMatrix(RotationMatrix matrix) {
        // http://en.wikipedia.org/wiki/Rotation_matrix

        DoubleMatrix2D mat = matrix.getMatrix();

        double oneMinusCos = 1 - Math.cos(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        mat.set(0, 0, cos + point.x() * point.x() * oneMinusCos);
        mat.set(1, 0, point.y() * point.x() * oneMinusCos + point.z() * sin);
        mat.set(2, 0, point.z() * point.x() * oneMinusCos - point.y() * sin);

        mat.set(0, 1, point.x() * point.y() * oneMinusCos - point.z() * sin);
        mat.set(1, 1, cos + point.y() * point.y() * oneMinusCos);
        mat.set(2, 1, point.z() * point.y() * oneMinusCos + point.x() * sin);

        mat.set(0, 2, point.x() * point.z() * oneMinusCos + point.y() * sin);
        mat.set(1, 2, point.y() * point.z() * oneMinusCos - point.x() * sin);
        mat.set(2, 2, cos + point.z() * point.z() * oneMinusCos);
    }

    @Override
    public int getNumDim() {
        return 3;
    }
}
