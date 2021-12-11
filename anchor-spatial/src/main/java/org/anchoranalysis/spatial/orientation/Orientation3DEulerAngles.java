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

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import java.util.function.BiConsumer;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * An orientation using three angle parameters for the rotation.
 *
 * <p>This in some form of <a href="https://en.wikipedia.org/wiki/Davenport_chained_rotations">Euler
 * rotations</a> representation.
 *
 * @see <a href="http://mathworld.wolfram.com/EulerAngles.html">Euler Angles</a>
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Orientation3DEulerAngles extends Orientation {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Rotation around X-dimension (in radians) anti-clockwise.
     *
     * <p>This is the <b>alpha</b> parameter.
     */
    private final double rotationX;

    /**
     * Rotation around Y-dimension (in radians) anti-clockwise.
     *
     * <p>This is the <b>beta</b> parameter.
     */
    private final double rotationY;

    /**
     * Rotation around Z-dimension (in radians) anti-clockwise.
     *
     * <p>This is the <b>gamma</b> parameter.
     */
    private final double rotationZ;

    @Override
    public String toString() {
        return String.format("%3.3f, %3.3f, %3.3f", rotationX, rotationY, rotationZ);
    }

    @Override
    protected RotationMatrix deriveRotationMatrix() {

        RotationMatrix rotationMatrix = new RotationMatrix(numberDimensions());

        DoubleMatrix2D matrix = rotationMatrix.getMatrix();

        final int matNumDim = 3;

        DoubleFactory2D f = DoubleFactory2D.dense;

        DoubleMatrix2D matTemporary = f.make(matNumDim, matNumDim);
        DoubleMatrix2D matX = f.make(matNumDim, matNumDim);
        DoubleMatrix2D matY = f.make(matNumDim, matNumDim);
        DoubleMatrix2D matZ = f.make(matNumDim, matNumDim);

        assignRotationMatrix(matX, this.rotationX, 0);
        assignRotationMatrix(matY, this.rotationY, 1);
        assignRotationMatrix(matZ, this.rotationZ, 2);

        matX.zMult(matY, matTemporary);
        matTemporary.zMult(matZ, matrix);

        return rotationMatrix;
    }

    @Override
    public Orientation negative() {
        return new Orientation3DEulerAngles(
                rotationX, rotationY, (rotationZ + Math.PI) % (2 * Math.PI));
    }

    @Override
    public void describeOrientation(BiConsumer<String, String> consumer) {
        addProperty(consumer, "X", rotationX);
        addProperty(consumer, "Y", rotationY);
        addProperty(consumer, "Z", rotationZ);
    }

    @Override
    public int numberDimensions() {
        return 3;
    }

    private static void addProperty(
            BiConsumer<String, String> consumer, String dimension, double radians) {
        consumer.accept(
                String.format("Orientation Angle %s (radians)", dimension),
                String.format("%1.2f", radians));
    }

    /**
     * Assigns values for a rotation about a particular axis (in radians).
     *
     * @param matrix the matrix to assign to.
     * @param angleRadians the angle in radians to assign.
     * @param axisShift 0 for x-axis, 1 for y-axis, 2 for z-axis
     */
    private static void assignRotationMatrix(
            DoubleMatrix2D matrix, double angleRadians, int axisShift) {

        IndexCalculator index = new IndexCalculator(axisShift, 3);

        matrix.assign(0);
        matrix.set(index.calculate(1), index.calculate(1), Math.cos(angleRadians));
        matrix.set(index.calculate(2), index.calculate(1), Math.sin(angleRadians));
        matrix.set(index.calculate(1), index.calculate(2), -1 * Math.sin(angleRadians));
        matrix.set(index.calculate(2), index.calculate(2), Math.cos(angleRadians));
        matrix.set(index.calculate(0), index.calculate(0), 1);
    }
}
