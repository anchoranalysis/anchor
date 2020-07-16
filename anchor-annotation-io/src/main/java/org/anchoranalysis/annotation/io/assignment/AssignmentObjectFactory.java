/* (C)2020 */
package org.anchoranalysis.annotation.io.assignment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureEvaluator;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.math.optimization.HungarianAlgorithm;

@RequiredArgsConstructor
public class AssignmentObjectFactory {

    // START REQUIRED ARGUMENTS
    private final FeatureEvaluator<FeatureInputPairObjects> featureEvaluator;
    private final boolean useMIP;
    // END REQUIRED ARGUMENTS

    // Remember the cost matrix, in case we need it later
    @Getter private ObjectCollectionDistanceMatrix cost;

    public AssignmentOverlapFromPairs createAssignment(
            ObjectCollection left,
            ObjectCollection right,
            double maxAcceptedCost,
            ImageDimensions dim)
            throws FeatureCalcException {

        // Empty annotations
        if (left.isEmpty()) {
            // N.B. Also sensibly handles the case where annotation-objects and result-objects are
            // both empty
            AssignmentOverlapFromPairs out = new AssignmentOverlapFromPairs();
            out.addRightObjects(right);
            return out;
        }

        // Empty result objects
        if (right.isEmpty()) {
            AssignmentOverlapFromPairs out = new AssignmentOverlapFromPairs();
            out.addLeftObjects(left);
            return out;
        }

        cost = createCostMatrix(maybeProject(left), maybeProject(right), dim);

        // Non empty both

        HungarianAlgorithm ha = new HungarianAlgorithm(cost.getDistanceMatrix());
        int[] assign = ha.execute();

        AssignmentOverlapFromPairs assignment =
                new CreateAssignmentFromDistanceMatrix(cost, assign, maxAcceptedCost)
                        .createAssignment();
        if (assignment.rightSize() != right.size()) {
            throw new FeatureCalcException(
                    "assignment.rightSize() does not equal the number of the right objects. This should never happen!");
        }
        return assignment;
    }

    private ObjectCollection maybeProject(ObjectCollection objects) {
        if (useMIP) {
            return objects.stream().map(ObjectMask::maxIntensityProjection);
        } else {
            return objects;
        }
    }

    private ObjectCollectionDistanceMatrix createCostMatrix(
            ObjectCollection annotation, ObjectCollection result, ImageDimensions dim)
            throws FeatureCalcException {

        FeatureCalculatorSingle<FeatureInputPairObjects> session;
        try {
            session = featureEvaluator.createAndStartSession();
        } catch (OperationFailedException e) {
            throw new FeatureCalcException(e);
        }

        NRGStackWithParams nrgStack = new NRGStackWithParams(dim);

        double[][] outArr = new double[annotation.size()][result.size()];

        for (int i = 0; i < annotation.size(); i++) {
            ObjectMask objA = annotation.get(i);
            for (int j = 0; j < result.size(); j++) {

                ObjectMask objR = result.get(j);

                double costObjects =
                        session.calc(new FeatureInputPairObjects(objA, objR, nrgStack));
                outArr[i][j] = costObjects;

                if (Double.isNaN(costObjects)) {
                    throw new FeatureCalcException("Cost is NaN");
                }
            }
        }

        try {
            return new ObjectCollectionDistanceMatrix(annotation, result, outArr);
        } catch (CreateException e) {
            throw new FeatureCalcException(e);
        }
    }
}
