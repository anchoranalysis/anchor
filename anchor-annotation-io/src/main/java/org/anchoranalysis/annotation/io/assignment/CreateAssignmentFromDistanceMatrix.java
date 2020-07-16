/* (C)2020 */
package org.anchoranalysis.annotation.io.assignment;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;

/** Creates overlap assignment from a distance matrix */
@AllArgsConstructor
class CreateAssignmentFromDistanceMatrix {

    private ObjectCollectionDistanceMatrix costMatrix;
    private int[] assign;
    private double maxAcceptedCost;

    public AssignmentOverlapFromPairs createAssignment() {
        AssignmentOverlapFromPairs assignment = new AssignmentOverlapFromPairs();

        ObjectCollection left = costMatrix.getObjects1();
        ObjectCollection right = costMatrix.getObjects2();

        Set<Integer> setAnnotationObjects = setForRange(left.size());
        Set<Integer> setResultObjects = setForRange(right.size());

        addPairsToAssignment(assignment, left, right, setAnnotationObjects, setResultObjects);

        addObjectsFromIndices(left, setAnnotationObjects, assignment::addLeftObject);
        addObjectsFromIndices(right, setResultObjects, assignment::addRightObject);

        return assignment;
    }

    private void addPairsToAssignment(
            AssignmentOverlapFromPairs assignment,
            ObjectCollection left,
            ObjectCollection right,
            Set<Integer> setLeft,
            Set<Integer> setRight) {
        for (int i = 0; i < assign.length; i++) {
            int ref = assign[i];
            if (ref != -1) {
                double cost = costMatrix.getDistance(i, ref);
                if (cost < maxAcceptedCost) {
                    assignment.addPair(left.get(i), right.get(ref), 1.0 - cost);
                    setLeft.remove(i);
                    setRight.remove(ref);
                }
            }
        }
    }

    /** A hashset of a sequence of indices from 0 until an upper value (exclusive) */
    private static Set<Integer> setForRange(int endValueExclusive) {
        return IntStream.range(0, endValueExclusive).boxed().collect(Collectors.toSet());
    }

    /** Calls a function with every object from a collection whose index is in a set */
    private static void addObjectsFromIndices(
            ObjectCollection objects, Set<Integer> indices, Consumer<ObjectMask> functionToAdd) {
        indices.forEach(index -> functionToAdd.accept(objects.get(index)));
    }
}
