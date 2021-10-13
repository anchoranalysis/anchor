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
import org.anchoranalysis.annotation.io.comparer.StatisticsToExport;
import org.anchoranalysis.image.core.merge.ObjectMaskMerger;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Creates an assignment between intersecting <i>voxels</i> in two objects.
 *
 * <p>Voxels are considered to be assigned, if the exact same voxel exists in both {@link
 * ObjectMask}s, and otherwise a voxel is considered unassigned.
 *
 * <p>Calculates statistics based upon corresponding two object-masks:
 *
 * <ul>
 *   <li><a href="https://en.wikipedia.org/wiki/S%C3%B8rensen%E2%80%93Dice_coefficient">DICE</a>.
 *   <li><a href="https://en.wikipedia.org/wiki/Jaccard_index">Jaccard</a>.
 * </ul>
 *
 * <p>The object-masks are arbitrarily termed <i>left</i> and <i>right</i>.
 *
 * @author Owen Feehan
 */
public class ObjectVoxelIntersection implements Assignment<ObjectMask> {

    private final ObjectMask objectLeft;
    private final ObjectMask objectRight;

    private final int numberIntersectingVoxels;
    private final int numberUnionVoxels;
    private final int sizeLeft;
    private final int sizeRight;

    /**
     * Determines a voxel-assignment between two objects.
     *
     * @param left the left-object.
     * @param right the right-object.
     */
    public ObjectVoxelIntersection(ObjectMask left, ObjectMask right) {
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
    public List<ObjectMask> paired(boolean left) {
        return multiplexObjectIf(isIntersectionPresent(), left);
    }

    @Override
    public List<ObjectMask> unassigned(boolean left) {
        return multiplexObjectIf(!isIntersectionPresent(), left);
    }

    @Override
    public StatisticsToExport statistics() {
        StatisticsToExport out = new StatisticsToExport();

        out.addDouble("dice", calculateDice());
        out.addDouble("jaccard", calculateJaccard());
        out.addInt("numberIntersectingVoxels", numberIntersectingVoxels);
        out.addInt("numberUnionVoxels", numberUnionVoxels);
        out.addInt("sizeLeft", sizeLeft);
        out.addInt("sizeRight", sizeRight);

        return out;
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
