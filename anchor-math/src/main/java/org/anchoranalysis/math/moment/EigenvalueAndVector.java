/* (C)2020 */
package org.anchoranalysis.math.moment;

import cern.colt.matrix.DoubleMatrix1D;
import lombok.Value;

/**
 * An eigen-value and its corresponding eigen-vector.
 *
 * @author Owen Feehan
 */
@Value
public class EigenvalueAndVector implements Comparable<EigenvalueAndVector> {

    private final double eigenvalue;
    private final DoubleMatrix1D eigenvector;

    @Override
    public int compareTo(EigenvalueAndVector o) {
        return Double.compare(this.eigenvalue, o.eigenvalue);
    }

    /**
     * A normalization of an eigen-value to represent axis-length.
     *
     * <p>This normalization procedure is designed to return the same result as Matlab's
     * "MajorAxisLength" feature, as per <a
     * href="http://stackoverflow.com/questions/1711784/computing-object-statistics-from-the-second-central-moments">Stackoverflow
     * post</a>
     *
     * @return
     */
    public double eigenvalueNormalizedAsAxisLength() {
        return (4 * Math.sqrt(eigenvalue));
    }

    public EigenvalueAndVector duplicate() {
        return new EigenvalueAndVector(eigenvalue, eigenvector.copy());
    }
}
