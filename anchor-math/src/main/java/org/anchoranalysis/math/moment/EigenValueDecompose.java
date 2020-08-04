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

package org.anchoranalysis.math.moment;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class EigenValueDecompose {

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
