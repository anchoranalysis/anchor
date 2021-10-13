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
import org.anchoranalysis.spatial.point.Vector3d;
import org.anchoranalysis.spatial.rotation.RotationMatrix;

/**
 * Creates a {@link RotationMatrix} that performs a 3D rotation using an <a href="https://en.wikipedia.org/wiki/Axis%E2%80%93angle_representation">Axis-angle</a> representation.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class RotateAxisAngle extends RotationMatrixFactory {

    /** 
     * The axis unit-vector parameter.
     * 
     * <p>This should always be a unit-vector as a precondition. This is not checked.
     */
    private Vector3d axis;
    
    /**
     * The angle parameter.
     */
    private double angle;

    @Override
    public void populate(RotationMatrix matrix) {

        DoubleMatrix2D mat = matrix.getMatrix();

        double oneMinusCos = 1 - Math.cos(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        mat.set(0, 0, cos + axis.x() * axis.x() * oneMinusCos);
        mat.set(1, 0, axis.y() * axis.x() * oneMinusCos + axis.z() * sin);
        mat.set(2, 0, axis.z() * axis.x() * oneMinusCos - axis.y() * sin);

        mat.set(0, 1, axis.x() * axis.y() * oneMinusCos - axis.z() * sin);
        mat.set(1, 1, cos + axis.y() * axis.y() * oneMinusCos);
        mat.set(2, 1, axis.z() * axis.y() * oneMinusCos + axis.x() * sin);

        mat.set(0, 2, axis.x() * axis.z() * oneMinusCos + axis.y() * sin);
        mat.set(1, 2, axis.y() * axis.z() * oneMinusCos - axis.x() * sin);
        mat.set(2, 2, cos + axis.z() * axis.z() * oneMinusCos);
    }

    @Override
    public int numberDimensions() {
        return 3;
    }
}
