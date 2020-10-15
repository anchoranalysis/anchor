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
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.object.ObjectMask;

/** 
 * Creates overlap assignment from a distance matrix
 */
@AllArgsConstructor
class CreateAssignmentFromDistanceMatrix {

    private CostMatrix<ObjectMask> costMatrix;
    private int[] assign;
    private double maxAcceptedCost;

    public AssignmentOverlapFromPairs createAssignment() {
        AssignmentOverlapFromPairs assignment = new AssignmentOverlapFromPairs();

        List<ObjectMask> left = costMatrix.getFirst();
        List<ObjectMask> right = costMatrix.getSecond();

        Set<Integer> setAnnotationObjects = setForRange(left.size());
        Set<Integer> setResultObjects = setForRange(right.size());

        addPairsToAssignment(assignment, left, right, setAnnotationObjects, setResultObjects);

        addObjectsFromIndices(left, setAnnotationObjects, assignment::addLeftObject);
        addObjectsFromIndices(right, setResultObjects, assignment::addRightObject);

        return assignment;
    }

    private void addPairsToAssignment(
            AssignmentOverlapFromPairs assignment,
            List<ObjectMask> left,
            List<ObjectMask> right,
            Set<Integer> setLeft,
            Set<Integer> setRight) {
        for (int i = 0; i < assign.length; i++) {
            int ref = assign[i];
            if (ref != -1) {
                double cost = costMatrix.getCost(i, ref);
                if (cost < maxAcceptedCost) {
                    assignment.addPair(left.get(i), right.get(ref), 1.0 - cost);
                    setLeft.remove(i);
                    setRight.remove(ref);
                }
            }
        }
    }

    /** A {@link Set} of a sequence of indices from 0 until an upper value (exclusive) */
    private static Set<Integer> setForRange(int endValueExclusive) {
        return IntStream.range(0, endValueExclusive).boxed().collect(Collectors.toSet());
    }

    /** Calls a function with every object from a collection whose index is in a set */
    private static void addObjectsFromIndices(
            List<ObjectMask> objects, Set<Integer> indices, Consumer<ObjectMask> functionToAdd) {
        indices.forEach(index -> functionToAdd.accept(objects.get(index)));
    }
}
