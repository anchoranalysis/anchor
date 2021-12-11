/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.spatial.orientation;

import cern.colt.matrix.DoubleMatrix2D;
import java.util.function.BiConsumer;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.anchoranalysis.spatial.point.Vector3d;

/**
 * An orientation in axis-angle representation.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Axis%E2%80%93angle_representation">Wikipedia</a>
 * @author Owen Feehan
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class OrientationAxisAngle extends Orientation {

    /** */
    private static final long serialVersionUID = -2592680414423106545L;

    /**
     * <b>Axis</b> part of axis-angle orientation (should be normalized). Once this is passed here,
     * it is consumed, and must not be changed elsewhere.
     */
    private Vector3d axis;

    /** <b>Angle</b> part of axis-angle orientation (anti-clock in radians). */
    private double angle;

    @Override
    public String toString() {
        return String.format("angle=%f axis=%s", angle, axis.toString());
    }

    @Override
    protected RotationMatrix deriveRotationMatrix() {

        RotationMatrix rotationMatrix = new RotationMatrix(numberDimensions());

        DoubleMatrix2D matrix = rotationMatrix.getMatrix();

        double oneMinusCos = 1 - Math.cos(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        matrix.set(0, 0, cos + axis.x() * axis.x() * oneMinusCos);
        matrix.set(1, 0, axis.y() * axis.x() * oneMinusCos + axis.z() * sin);
        matrix.set(2, 0, axis.z() * axis.x() * oneMinusCos - axis.y() * sin);

        matrix.set(0, 1, axis.x() * axis.y() * oneMinusCos - axis.z() * sin);
        matrix.set(1, 1, cos + axis.y() * axis.y() * oneMinusCos);
        matrix.set(2, 1, axis.z() * axis.y() * oneMinusCos + axis.x() * sin);

        matrix.set(0, 2, axis.x() * axis.z() * oneMinusCos + axis.y() * sin);
        matrix.set(1, 2, axis.y() * axis.z() * oneMinusCos - axis.x() * sin);
        matrix.set(2, 2, cos + axis.z() * axis.z() * oneMinusCos);

        return rotationMatrix;
    }

    @Override
    public Orientation negative() {
        return new OrientationAxisAngle(new Vector3d(axis), angle + Math.PI);
    }

    @Override
    public int numberDimensions() {
        return 3;
    }

    @Override
    public void describeOrientation(BiConsumer<String, String> consumer) {
        // NOTHING TO DO
    }
}
