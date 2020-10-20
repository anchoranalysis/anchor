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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.anchoranalysis.core.value.TypedValue;
import org.anchoranalysis.image.core.merge.ObjectMaskMerger;
import org.anchoranalysis.image.voxel.object.ObjectMask;

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
    public int numberPaired() {
        return isIntersectionPresent() ? 1 : 0;
    }

    @Override
    public int numberUnassigned(boolean left) {
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
        out.add(calculateDice(), calculateJaccard());
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

    private double calculateDice() {
        int num = 2 * numberIntersectingVoxels;
        int dem = sizeLeft + sizeRight;
        return ((double) num) / dem;
    }

    private double calculateJaccard() {
        return ((double) numberIntersectingVoxels) / numberUnionVoxels;
    }
}
