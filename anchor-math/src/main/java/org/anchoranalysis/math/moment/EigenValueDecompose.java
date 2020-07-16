/* (C)2020 */
package org.anchoranalysis.math.moment;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class EigenValueDecompose {

    private EigenValueDecompose() {}

    public static List<EigenvalueAndVector> apply(
            DoubleMatrix2D covarianceMatrix, boolean sortAscending) {
        List<EigenvalueAndVector> list = eigenValueDecompose(covarianceMatrix);

        sortList(list, sortAscending);

        return list;
    }

    private static List<EigenvalueAndVector> eigenValueDecompose(DoubleMatrix2D secondMoments) {

        List<EigenvalueAndVector> list = new ArrayList<>();

        EigenvalueDecomposition ed = new EigenvalueDecomposition(secondMoments);

        DoubleMatrix1D evals = ed.getRealEigenvalues();

        for (int i = 0; i < 3; i++) {
            list.add(new EigenvalueAndVector(evals.get(i), ed.getV().viewColumn(i)));
        }

        return list;
    }

    private static <T extends Comparable<T>> void sortList(List<T> list, boolean sortOrder) {
        if (sortOrder) {
            Collections.sort(list);
        } else {
            Collections.sort(list, Collections.reverseOrder());
        }
    }
}
