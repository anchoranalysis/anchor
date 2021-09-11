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

package org.anchoranalysis.math.statistics.moment;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Statistic;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The first moment (mean) and eigenvalues of the second moments (covariance) from a matrix of
 * points.
 *
 * <p>See <a href="https://en.wikipedia.org/wiki/Image_moment">Image Moment on Wikipedia</a>
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMoments {

    private List<EigenvalueAndVector> list;
    private double[] mean = new double[3];

    /**
     * Calculates the second-moments from the covariance of a matrix of points.
     *
     * <p>Steps: 1. Constructs first and the second-moments matrix of some input points 2.
     * Calculates an eigenvalue-decomposition of the second-moment matrix
     *
     * @param matrixPoints a matrix where each row represents a point (n x 3) and each column an
     *     axis
     * @param suppressZ iff true the z-dimension is ignored
     * @param sortAscending if true, eigenValues are sorted in ascendingOrder, if false in
     *     descending order
     */
    public ImageMoments(DoubleMatrix2D matrixPoints, boolean suppressZ, boolean sortAscending) {

        mean = calculateFirstMoments(matrixPoints);

        list =
                EigenValueDecompose.apply(
                        calculateSecondMoments(matrixPoints, suppressZ), sortAscending);
    }

    /**
     * Get an eigenvalue and corresponding eigenvector for a particular axis.
     * 
     * @param axis axis 0 for X, 1 for Y, 2 for Z.
     * @return the corresponding to {@link EigenvalueAndVector} as used internally.
     */
    public EigenvalueAndVector get(int axis) {
        return list.get(axis);
    }

    /**
     * Get the mean-value for a particular axis.
     *
     * @param axis 0 for X, 1 for Y, 2 for Z.
     * @return the mean for the corresponding axis.
     */
    public double getMean(int axis) {
        return mean[axis];
    }

    /** 
     * Removes the entry that is closest to having an eigenVector in direction (0,0,1).
     */
    public void removeClosestToUnitZ() {

        double zMax = Double.NEGATIVE_INFINITY;
        int index = -1;
        for (int i = 0; i < 3; i++) {

            // Dot product considers only the z value
            double zVal = list.get(i).getEigenvector().get(2);
            if (zVal > zMax) {
                zMax = zVal;
                index = i;
            }
        }

        assert (index != -1);

        // Remove the closest to z
        list.remove(index);
    }

    /**
     * Creates a deep-copy of the current object.
     * 
     * @return a deep copy.
     */
    public ImageMoments duplicate() {
        ImageMoments out = new ImageMoments();
        out.list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            out.list.add(list.get(i).duplicate());
        }
        return out;
    }

    /** Calculates the first moment (the mean) */
    private static double[] calculateFirstMoments(DoubleMatrix2D matrixPoints) {
        double[] mean = new double[3];
        for (int i = 0; i < 3; i++) {
            mean[i] = matrixPoints.viewColumn(i).zSum() / matrixPoints.rows();
        }
        return mean;
    }

    /** Calculates the second moment (the covariance) */
    private static DoubleMatrix2D calculateSecondMoments(
            DoubleMatrix2D matrixPoints, boolean suppressZ) {

        DoubleMatrix2D secondMoments = Statistic.covariance(matrixPoints);

        if (suppressZ) {
            secondMoments.set(2, 0, 0);
            secondMoments.set(2, 1, 0);
            secondMoments.set(0, 2, 0);
            secondMoments.set(1, 2, 0);
        }

        return secondMoments;
    }
}
