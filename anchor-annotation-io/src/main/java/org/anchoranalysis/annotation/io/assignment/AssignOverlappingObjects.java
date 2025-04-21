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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorSingle;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureEvaluator;
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.math.optimization.HungarianAlgorithm;

/**
 * Creates a one-to-one <i>assignment</i> between two sets of {@link ObjectMask}s.
 *
 * <p>As arbitrary naming, these sets are termed <i>left</i> and <i>right</i>.
 *
 * <p>An <i>assignment</i> is one-to-one mapping between the first set and the second, borrowing
 * terminology from the <a href="https://en.wikipedia.org/wiki/Assignment_problem">assignment
 * problem</a>.
 *
 * <p>Not all objects must be mapped. An object may only be mapped to maximally one object in the
 * other set.
 *
 * <p>The <a href="https://en.wikipedia.org/wiki/Hungarian_algorithm">Hungarian Algorithm</a> is
 * used to determine the assignment, based upon an overlap function between the corresponding
 * objects. The assignments are chosen to maximize total overlap.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class AssignOverlappingObjects {

    // START REQUIRED ARGUMENTS
    /**
     * A feature that gives a measure of <i>cost</i> between a pair of objects, which should be
     * maximally 1.0 (for no overlap) and 0.0 (for perfect overlap).
     */
    private final FeatureEvaluator<FeatureInputPairObjects> featureEvaluator;

    /**
     * If true, a maximum-intensity-projection is first applied to any 3D objects into a 2D plane,
     * before comparison.
     */
    private final boolean flatten;

    // END REQUIRED ARGUMENTS

    /**
     * A matrix linking the overlap between each {@link ObjectMask} to each other, respectively as
     * rows and columns.
     */
    @Getter private CostMatrix<ObjectMask> costs;

    /**
     * Creates an assignment from the objects in {@code left} to those in {@code right}.
     *
     * <p>See the class documentation for the algorithm involved.
     *
     * @param left the left objects.
     * @param right the right objects.
     * @param maxAcceptedCost an upper-maximum cost, above which a mapping will be disallowed
     *     between objects.
     * @param dimensions the dimensions of the scene in which all objects reside.
     * @return a newly created assignment.
     * @throws FeatureCalculationException if overlap cannot be calculated.
     */
    public OverlappingObjects createAssignment(
            ObjectCollection left,
            ObjectCollection right,
            double maxAcceptedCost,
            Dimensions dimensions)
            throws FeatureCalculationException {

        // Empty annotations
        if (left.isEmpty()) {
            // N.B. Also sensibly handles the case where annotation-objects and result-objects are
            // both empty
            return OverlappingObjects.createWithRight(right);
        }

        // Empty result objects
        if (right.isEmpty()) {
            return OverlappingObjects.createWithLeftUnassigned(left);
        }

        costs = createCostMatrix(maybeProject(left), maybeProject(right), dimensions);

        OverlappingObjects assignment = findMinimalCostMapping(costs, maxAcceptedCost);

        if (assignment.rightSize() != right.size()) {
            throw new AnchorImpossibleSituationException(
                    "assignment.rightSize() does not equal the number of the right objects. This should never happen!");
        }
        return assignment;
    }

    private ObjectCollection maybeProject(ObjectCollection objects) {
        if (flatten) {
            return objects.stream().map(ObjectMask::flattenZ);
        } else {
            return objects;
        }
    }

    private CostMatrix<ObjectMask> createCostMatrix(
            ObjectCollection annotation, ObjectCollection result, Dimensions dimensions)
            throws FeatureCalculationException {

        try {
            FeatureCalculatorSingle<FeatureInputPairObjects> calculator =
                    featureEvaluator.createFeatureSession();

            EnergyStack energyStack = new EnergyStack(dimensions);

            return CostMatrix.create(
                    annotation.asList(),
                    result.asList(),
                    false,
                    (first, second) -> {
                        try {
                            FeatureInputPairObjects input =
                                    new FeatureInputPairObjects(first, second, energyStack);
                            return calculator.calculate(input);
                        } catch (FeatureCalculationException e) {
                            throw new CreateException(e);
                        }
                    });
        } catch (CreateException | OperationFailedException e) {
            throw new FeatureCalculationException(e);
        }
    }

    /**
     * Finds the mapping that achieves minimal total cost, subject to the constraint that the cost
     * of no two mapped objects exceeds {@code maxAcceptedCost}.
     */
    private static OverlappingObjects findMinimalCostMapping(
            CostMatrix<ObjectMask> costs, double maxAcceptedCost) {
        HungarianAlgorithm hungarian = new HungarianAlgorithm(costs.getMatrix());
        int[] assign = hungarian.execute();

        return new CreateAssignmentFromCostMatrix(costs, assign, maxAcceptedCost)
                .createAssignment();
    }
}
