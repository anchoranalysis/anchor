/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TensorUtilities {

    public static final double squared(double val) {
        return Math.pow(val, 2);
    }

    /** A two element array, where the second value is 0.0 */
    public static final double[] twoElementArray(double firstVal) {
        return twoElementArray(firstVal, 0);
    }

    public static final double[] twoElementArray(double firstVal, double secondVal) {
        return new double[] {firstVal, secondVal};
    }

    public static final double[] threeElementArray(
            double firstVal, double secondVal, double thirdVal) {
        return new double[] {firstVal, secondVal, thirdVal};
    }

    public static final DoubleMatrix1D twoElementMatrix(double x, double y) {
        DoubleMatrix1D out = DoubleFactory1D.dense.make(2);
        out.set(0, x);
        out.set(1, y);
        return out;
    }

    public static final DoubleMatrix1D threeElementMatrix(double x, double y, double z) {
        DoubleMatrix1D out = DoubleFactory1D.dense.make(3);
        out.set(0, x);
        out.set(1, y);
        out.set(2, z);
        return out;
    }
}
