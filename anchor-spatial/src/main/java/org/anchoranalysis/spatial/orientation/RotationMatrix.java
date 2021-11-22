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

package org.anchoranalysis.spatial.orientation;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import org.anchoranalysis.core.exception.OperationFailedException;
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
     * <p>This name is deliberately kept as {@code delegate} to avoid breaking serialized objects.
     */
    private DoubleMatrix2D delegate;

    /**
     * Creates a rotation-matrix populated only with zeros.
     *
     * @param numberDimensions the dimensionality of the matrix to create.
     */
    public RotationMatrix(int numberDimensions) {
        delegate = DoubleFactory2D.dense.make(numberDimensions, numberDimensions);
    }

    /**
     * Creates a rotation-matrix from an existing {@link DoubleMatrix2D}.
     *
     * @param matrix the matrix, which is then used internally in the structure.
     */
    public RotationMatrix(DoubleMatrix2D matrix) {
        this.delegate = matrix;
    }

    /**
     * Performs a rotation on a single point, encoded as a {@link Point3d}.
     *
     * @param point the point to rotate.
     * @return a newly-created rotated point.
     */
    public Point3d rotatedPoint(Point3d point) {

        if (delegate.rows() == 3) {
            double[] rotatedPoint = rotatePoint(point.toArray());
            return new Point3d(rotatedPoint[0], rotatedPoint[1], rotatedPoint[2]);
        } else if (delegate.rows() == 2) {
            double[] rotatedPoint = rotatePoint(point.toArrayXY());
            return new Point3d(rotatedPoint[0], rotatedPoint[1], 0);
        } else {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Performs a rotation on a single point, encoded as an array.
     *
     * @param point the point encoded as an array, where the length should match the number of
     *     dimensions of the rotation-matrix.
     * @return a newly-created array encoding the rotated {@code point}.
     */
    public double[] rotatePoint(double[] point) {
        Preconditions.checkArgument(point.length == delegate.rows());

        DoubleMatrix2D matrixIn = matrixFromPoint(point);

        DoubleMatrix2D matrixOut = delegate.zMult(matrixIn, null);
        return pointFromMatrix(matrixOut);
    }

    /**
     * Extracts a column from the rotation-matrix as a point.
     *
     * <p>This should only be called on a three-dimensional rotation matrix.
     *
     * @param columnIndex the index of the column.
     * @return a newly created point from the column.
     * @throws OperationFailedException if the rotation-matrix does not have three dimensions
     *     exactly.
     */
    public Point3d column(int columnIndex) throws OperationFailedException {
        if (delegate.columns() != 3) {
            throw new OperationFailedException("The rotation-matrix is not three-dimensional");
        }
        DoubleMatrix1D vector = delegate.viewColumn(columnIndex);
        return new Point3d(vector.get(0), vector.get(1), vector.get(2));
    }

    /**
     * The number of dimensions in the rotation-matrix.
     *
     * @return the number of dimensions.
     */
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

    /**
     * Multiplies each element in the rotation-matrix by a value.
     *
     * @param value the value to multiply each element by.
     */
    public void multiplyByConstant(double value) {
        delegate.assign(Functions.mult(value));
    }

    /**
     * Transposes the matrix immutably.
     *
     * @return a newly-created {@link RotationMatrix} that is a transposed copy.
     */
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

    /** Encodes a point into a matrix. */
    private static DoubleMatrix2D matrixFromPoint(double[] point) {
        int dimensions = point.length;

        DoubleFactory2D factory = DoubleFactory2D.dense;
        DoubleMatrix2D matrixIn = factory.make(dimensions, 1);

        for (int i = 0; i < dimensions; i++) {
            matrixIn.set(i, 0, point[i]);
        }
        return matrixIn;
    }

    /** Decodes a point from a matrix. */
    private static double[] pointFromMatrix(DoubleMatrix2D matrixOut) {
        int dimensions = matrixOut.rows();
        double[] pointOut = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            pointOut[i] = matrixOut.get(i, 0);
        }
        return pointOut;
    }
}
