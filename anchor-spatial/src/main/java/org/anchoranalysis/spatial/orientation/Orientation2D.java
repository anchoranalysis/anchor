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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A simple angular orientation the 2D plane, relative to the x-axis.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Orientation2D extends Orientation {

    /** */
    private static final long serialVersionUID = 1528190376087281572L;

    /** The angle of the rotation in the 2D plane anti-clockwise, in <b>radians</b>. */
    private double angleRadians = 0;

    /**
     * The angle of the rotation in the 2D plane anti-clockwise, in <b>degrees</b>.
     *
     * @return the angle in degrees.
     */
    public double getAngleDegrees() {
        return angleRadians * 180 / Math.PI;
    }

    @Override
    public Orientation2D negative() {
        return new Orientation2D((this.angleRadians + Math.PI) % (2 * Math.PI));
    }

    @Override
    protected RotationMatrix deriveRotationMatrix() {

        RotationMatrix rotationMatrix = new RotationMatrix(numberDimensions());

        IndexCalculator index = new IndexCalculator(0, 2);

        DoubleMatrix2D matrix = rotationMatrix.getMatrix();
        matrix.set(index.calculate(0), index.calculate(0), Math.cos(angleRadians));
        matrix.set(index.calculate(0), index.calculate(1), -1 * Math.sin(angleRadians));
        matrix.set(index.calculate(1), index.calculate(0), Math.sin(angleRadians));
        matrix.set(index.calculate(1), index.calculate(1), Math.cos(angleRadians));

        return rotationMatrix;
    }

    @Override
    public String toString() {
        return String.format("[rad=%f]", angleRadians);
    }

    @Override
    public int numberDimensions() {
        return 2;
    }
}
