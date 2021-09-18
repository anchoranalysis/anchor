/*-
 * #%L
 * anchor-annotation-io
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

package org.anchoranalysis.annotation.io.assignment;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.functional.checked.CheckedToDoubleBiFunction;

/**
 * Stores costs between all possible pairings between elements from two lists.
 *
 * <p>Note that costs are often distances (symmetric) but not necessarily.
 *
 * <p>Internally, a matrix-like structure is used.
 * 
 * <p>The two lists are referred to as <i>first</i> and <i>second</i>.
 *
 * @author Owen Feehan
 * @param <T> element-type in lists
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CostMatrix<T> {

    /** The <i>first</i> set of elements. */
    private List<T> first;

    /** The <i>second</i> set of elements. */
    private List<T> second;

    /** A two-dimensional array mapping the costs from {@code first} to {@code second} */
    private double[][] matrix;

    /**
     * Creates by calculating the cost between elements.
     *
     * <p>All possible pairings are calculated involving an element from the first list and an
     * element from the second list.
     *
     * @param <T> element-type in lists
     * @param first the collection of elements that forms the left-side of the cost calculation,
     *     indexed by {@code index1} in the matrix.
     * @param second the collection of elements that forms the right-side of the cost calculation,
     *     indexed by {@code index2} in the matrix.
     * @param symmetric if cost(a,b) = cost(b,a), then set this true, for quicker calculations.
     * @param costCalculator calculates cost(a,b)
     * @return a newly created matrix
     * @throws CreateException if either list is empty or a distance cannot be calculated between elements.
     */
    public static <T> CostMatrix<T> create(
            List<T> first,
            List<T> second,
            boolean symmetric,
            CheckedToDoubleBiFunction<T, T, CreateException> costCalculator)
            throws CreateException {

        if (first.isEmpty()) {
            throw new CreateException("first must be non-empty");
        }

        if (second.isEmpty()) {
            throw new CreateException("second must be non-empty");
        }

        double[][] costs = new double[first.size()][second.size()];

        for (int i = 0; i < first.size(); i++) {
            T firstElement = first.get(i);
            for (int j = 0; j < selectInnerUpperBound(i, second.size(), symmetric); j++) {

                T secondElement = second.get(j);

                double cost = costCalculator.applyAsDouble(firstElement, secondElement);
                putCostInMatrix(costs, i, j, cost, symmetric);

                if (Double.isNaN(cost)) {
                    throw new CreateException("Distance is NaN. This is not allowed.");
                }
            }
        }

        return new CostMatrix<>(first, second, costs);
    }

    /**
     * Gets the cost from an element from the first-list to an element from the second-list.
     *
     * @param index1 index of element in first list
     * @param index2 index of element in second list
     * @return the cost.
     */
    public double getCost(int index1, int index2) {
        return matrix[index1][index2];
    }

    /**
     * The number of elements in the first list.
     *
     * @return number of elements
     */
    public int sizeFirst() {
        return first.size();
    }

    /**
     * The number of elements in the second list.
     *
     * @return number of elements
     */
    public int sizeSecond() {
        return second.size();
    }

    private static void putCostInMatrix(
            double[][] costs, int index1, int index2, double cost, boolean symmetric) {
        costs[index1][index2] = cost;

        if (symmetric) {
            costs[index2][index1] = cost;
        }
    }

    private static int selectInnerUpperBound(
            int iterationFirst, int sizeSecond, boolean symmetric) {
        if (symmetric) {
            return iterationFirst;
        } else {
            return sizeSecond;
        }
    }
}
