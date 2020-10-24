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

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.spatial.point.Point3d;

/**
 * A <a href="https://en.wikipedia.org/wiki/Rotation_matrix">matrix</a> that performs a rotation in
 * Euclidean space.
 *
 * @author Owen Feehan
 */
public class RotationMatrix implements Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * The underlying matrix implementing the rotation.
     * 
     * <p>This name is deliberately kept as 'delegate' to avoid breaking serialized objects.
     */
    private DoubleMatrix2D delegate;

    public RotationMatrix(int numberDimensions) {
        delegate = DoubleFactory2D.dense.make(numberDimensions, numberDimensions);
    }

    public RotationMatrix(DoubleMatrix2D matrix) {
        this.delegate = matrix;
    }

    public Point3d rotatedPoint(Point3d point) {

        if (delegate.rows() == 3) {
            double[] rotatedPoint = rotatePoint(point.toArray());
            return new Point3d(rotatedPoint[0], rotatedPoint[1], rotatedPoint[2]);
        } else if (delegate.rows() == 2) {
            double[] rotatedPoint = rotatePoint(point.toArray());
            return new Point3d(rotatedPoint[0], rotatedPoint[1], 0);
        } else {
            throw new AnchorImpossibleSituationException();
        }
    }

    public double[] rotatePoint(double[] point) {
        Preconditions.checkArgument(point.length == delegate.rows());

        int numberDimensions = point.length;

        DoubleFactory2D factory = DoubleFactory2D.dense;
        DoubleMatrix2D matrixIn = factory.make(numberDimensions, 1);

        for (int i = 0; i < numberDimensions; i++) {
            matrixIn.set(i, 0, point[i]);
        }

        DoubleMatrix2D matrixOut = delegate.zMult(matrixIn, null);

        double[] pointOut = new double[numberDimensions];
        for (int i = 0; i < numberDimensions; i++) {
            pointOut[i] = matrixOut.get(i, 0);
        }
        return pointOut;
    }

    public static RotationMatrix createFromThreeVectors(
            DoubleMatrix1D vector1, DoubleMatrix1D vector2, DoubleMatrix1D vector3) {

        DoubleFactory2D factory = DoubleFactory2D.dense;
        DoubleMatrix2D matrix = factory.make(3, 3);

        assignMatrixColumnFromVector(matrix, 0, vector1);
        assignMatrixColumnFromVector(matrix, 1, vector2);
        assignMatrixColumnFromVector(matrix, 2, vector3);

        return new RotationMatrix(matrix);
    }

    public RotationMatrix multiply(RotationMatrix other) {
        return new RotationMatrix(delegate.zMult(other.delegate, null));
    }

    public Point3d column(int columnIndex) {
        DoubleMatrix1D vector = delegate.viewColumn(columnIndex);
        return new Point3d(vector.get(0), vector.get(1), vector.get(2));
    }

    public int getNumberDimensions() {
        return delegate.columns();
    }

    /**
     * The underlying matrix used internally in the rotation-matrix.
     * 
     * @return the internal matrix structure used within the {@link RotationMatrix}.
     */
    public DoubleMatrix2D getMatrix() {
        return delegate;
    }

    public void multiplyByConstant(double value) {
        delegate.assign(Functions.mult(value));
    }

    public RotationMatrix transpose() {
        return new RotationMatrix(delegate.viewDice().copy());
    }

    /**
     * Deep-copy of the current rotation-matrix.
     * 
     * @return a newly created deep copy of the current object.
     */
    public RotationMatrix duplicate() {
        return new RotationMatrix(delegate.copy());
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
        
    /** Copies a vector into a particular column in the matrix. */
    private static void assignMatrixColumnFromVector(DoubleMatrix2D matrix, int columnIndex, DoubleMatrix1D vector) {
        for( int i=0; i<3; i++ ) {
            matrix.set(i, columnIndex, vector.get(i));
        }
    }
}
