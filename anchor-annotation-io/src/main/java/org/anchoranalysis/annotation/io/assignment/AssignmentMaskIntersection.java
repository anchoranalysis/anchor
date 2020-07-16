/* (C)2020 */
package org.anchoranalysis.annotation.io.assignment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.anchoranalysis.core.text.TypedValue;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ops.ObjectMaskMerger;

/**
 * Calculates statistics (DICE, Jaccard etc.) based upon corresponding two object-masks
 *
 * @author Owen Feehan
 */
public class AssignmentMaskIntersection implements Assignment {

    private final ObjectMask objectLeft;
    private final ObjectMask objectRight;

    private final int numberIntersectingVoxels;
    private final int numberUnionVoxels;
    private final int sizeLeft;
    private final int sizeRight;

    public AssignmentMaskIntersection(ObjectMask left, ObjectMask right) {
        super();
        this.objectLeft = left;
        this.objectRight = right;

        numberIntersectingVoxels = left.countIntersectingVoxels(right);
        numberUnionVoxels = ObjectMaskMerger.merge(left, right).numberVoxelsOn();

        sizeLeft = left.numberVoxelsOn();
        sizeRight = right.numberVoxelsOn();
    }

    @Override
    public int numPaired() {
        return isIntersectionPresent() ? 1 : 0;
    }

    @Override
    public int numUnassigned(boolean left) {
        return isIntersectionPresent() ? 0 : 1;
    }

    @Override
    public List<ObjectMask> getListPaired(boolean left) {
        return multiplexObjectIf(isIntersectionPresent(), left);
    }

    @Override
    public List<ObjectMask> getListUnassigned(boolean left) {
        return multiplexObjectIf(!isIntersectionPresent(), left);
    }

    @Override
    public List<String> createStatisticsHeaderNames() {
        return Arrays.asList(
                "dice",
                "jaccard",
                "numIntersectingVoxels",
                "numUnionVoxels",
                "sizeLeft",
                "sizeRight");
    }

    @Override
    public List<TypedValue> createStatistics() {
        WrappedTypeValueList out = new WrappedTypeValueList(4);
        out.add(calcDice(), calcJaccard());
        out.add(numberIntersectingVoxels, numberUnionVoxels, sizeLeft, sizeRight);
        return out.asList();
    }

    private List<ObjectMask> multiplexObjectIf(boolean condition, boolean left) {
        if (condition) {
            return multiplexObject(left);
        } else {
            return Collections.emptyList();
        }
    }

    private List<ObjectMask> multiplexObject(boolean left) {
        return Arrays.asList(left ? objectLeft : objectRight);
    }

    private boolean isIntersectionPresent() {
        return numberIntersectingVoxels > 0;
    }

    private double calcDice() {
        int num = 2 * numberIntersectingVoxels;
        int dem = sizeLeft + sizeRight;
        return ((double) num) / dem;
    }

    private double calcJaccard() {
        return ((double) numberIntersectingVoxels) / numberUnionVoxels;
    }
}
