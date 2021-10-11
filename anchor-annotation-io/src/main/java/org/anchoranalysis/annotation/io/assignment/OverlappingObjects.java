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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.DoubleStream;
import lombok.Getter;
import org.anchoranalysis.annotation.io.comparer.StatisticsToExport;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;

/**
 * Pairs an {@link ObjectMask} in one set with an {@link ObjectMask} in another, if deemed to
 * overlap sufficiently.
 *
 * <p>The sets are termed <i>left</i> and <i>right</i> as arbitrary names.
 *
 * <p>Several statistics based upon overlap, the number of pairs, the number of unassigned objects
 * are derived.
 *
 * @author Owen Feehan
 */
public class OverlappingObjects implements Assignment<ObjectMask> {

    /** The unassigned objects in the <i>left</i> set. */
    @Getter private List<ObjectMask> leftUnassigned = new ArrayList<>();

    /** The unassigned objects in the <i>right</i> set. */
    @Getter private List<ObjectMask> rightUnassigned = new ArrayList<>();

    /** The objects which have been paired. */
    private List<ObjectMaskPair> pairs = new ArrayList<>();

    /**
     * Creates with only <i>left unassigned</i> objects.
     *
     * @param objects the objects.
     * @return a newly created {@link OverlappingObjects}.
     */
    public static OverlappingObjects createWithLeftUnassigned(ObjectCollection objects) {
        OverlappingObjects out = new OverlappingObjects();
        out.addUnassignedLeft(objects);
        return out;
    }

    /**
     * Creates with only <i>right unassigned</i> objects.
     *
     * @param objects the objects.
     * @return a newly created {@link OverlappingObjects}.
     */
    public static OverlappingObjects createWithRight(ObjectCollection objects) {
        OverlappingObjects out = new OverlappingObjects();
        out.addUnassignedRight(objects);
        return out;
    }

    @Override
    public StatisticsToExport statistics() {
        StatisticsToExport out = new StatisticsToExport();

        out.addDouble("percentMatchesInAnnotation", percentLeftAssigned());
        out.addDouble("percentMatchesInResult", percentRightAssigned());
        out.addInt("matches", numberPaired());
        out.addInt("unmatchedAnnotation", numberUnassignedLeft());
        out.addInt("countItemsInAnnotation", leftSize());
        out.addInt("unmatchedResult", numberUnassignedRight());
        out.addInt("countItemsInResult", rightSize());
        out.addMeanExtrema(
                "overlapFromPaired",
                meanOverlapFromPaired(),
                minOverlapFromPaired(),
                maxOverlapFromPaired());

        return out;
    }

    /**
     * Removes any objects from the assignment if they touch the X or Y scene border.
     *
     * @param extent the size of the scene, which determines its borders.
     */
    public void removeTouchingBorderXY(Extent extent) {
        removeTouchingBorderXYObjects(extent, leftUnassigned);
        removeTouchingBorderXYObjects(extent, rightUnassigned);
        removeTouchingBorderXYPairObjects(extent, pairs);
    }

    @Override
    public List<ObjectMask> paired(boolean left) {
        return FunctionalList.mapToList(pairs, object -> object.getMultiplex(left));
    }

    @Override
    public List<ObjectMask> unassigned(boolean left) {
        if (left) {
            return leftUnassigned;
        } else {
            return rightUnassigned;
        }
    }

    /**
     * Sums the overlap-ratio across all assigned pairs.
     *
     * @return the sum.
     */
    public double sumOverlapFromPaired() {
        double sum = 0.0;
        for (ObjectMaskPair pair : pairs) {
            sum += pair.getOverlapRatio();
        }
        return sum;
    }

    /**
     * Add an unassigned object to the <i>left</i> set.
     *
     * @param object the object to add.
     */
    public void addUnassignedLeft(ObjectMask object) {
        leftUnassigned.add(object);
    }

    /**
     * Add unassigned objects to the <i>left</i> set.
     *
     * @param objects the object to add.
     */
    public void addUnassignedLeft(ObjectCollection objects) {
        leftUnassigned.addAll(objects.asList());
    }

    /**
     * Add an unassigned object to the <i>right</i> set.
     *
     * @param object the object to add.
     */
    public void addUnassignedRight(ObjectMask object) {
        rightUnassigned.add(object);
    }

    /**
     * Add unassigned objects to the <i>right</i> set.
     *
     * @param objects the object to add.
     */
    public void addUnassignedRight(ObjectCollection objects) {
        rightUnassigned.addAll(objects.asList());
    }

    /**
     * Adds an assigned pair of objects with a particular overlap-ratio.
     *
     * @param object1 the <i>left</i> object.
     * @param object2 the <i>right</i> object.
     * @param overlapRatio the ratio of overlap between the two objects.
     */
    public void addAssignedPair(ObjectMask object1, ObjectMask object2, double overlapRatio) {
        pairs.add(new ObjectMaskPair(object1, object2, overlapRatio));
    }

    /**
     * The percentage of objects in the <i>left</i> set that have been paired.
     *
     * @return the percentage.
     */
    public double percentLeftAssigned() {
        int size = leftSize();
        if (size == 0) {
            return Double.NaN;
        }
        return ((double) numberPaired()) * 100 / size;
    }

    /**
     * The percentage of objects in the <i>right</i> set that have been paired.
     *
     * @return the percentage.
     */
    public double percentRightAssigned() {
        int size = rightSize();
        if (size == 0) {
            return Double.NaN;
        }
        return ((double) numberPaired()) * 100 / size;
    }

    @Override
    public int numberPaired() {
        return pairs.size();
    }

    @Override
    public int numberUnassigned(boolean left) {
        if (left) {
            return numberUnassignedLeft();
        } else {
            return numberUnassignedRight();
        }
    }

    /**
     * The number of unassigned objects in the <i>left</i> set.
     *
     * @return the number of unassigned objects.
     */
    public int numberUnassignedLeft() {
        return leftUnassigned.size();
    }

    /**
     * The number of unassigned objects in the <i>right</i> set.
     *
     * @return the number of unassigned objects.
     */
    public int numberUnassignedRight() {
        return rightUnassigned.size();
    }

    /**
     * The total number of objects in the <i>left</i> set.
     *
     * @return the total number.
     */
    public int leftSize() {
        return pairs.size() + leftUnassigned.size();
    }

    /**
     * The total number of objects in the <i>right</i> set.
     *
     * @return the total number.
     */
    public int rightSize() {
        return pairs.size() + rightUnassigned.size();
    }

    private double meanOverlapFromPaired() {
        return sumOverlapFromPaired() / pairs.size();
    }

    private double minOverlapFromPaired() {
        return pairsOverlapStream().min().orElse(Double.NaN);
    }

    private double maxOverlapFromPaired() {
        return pairsOverlapStream().max().orElse(Double.NaN);
    }

    private DoubleStream pairsOverlapStream() {
        return pairs.stream().mapToDouble(ObjectMaskPair::getOverlapRatio);
    }

    private static void removeTouchingBorderXYObjects(Extent extent, List<ObjectMask> list) {
        Iterator<ObjectMask> itr = list.iterator();
        while (itr.hasNext()) {
            if (itr.next().boundingBox().atBorderXY(extent)) {
                itr.remove();
            }
        }
    }

    private static void removeTouchingBorderXYPairObjects(
            Extent extent, List<ObjectMaskPair> list) {
        Iterator<ObjectMaskPair> itr = list.iterator();
        while (itr.hasNext()) {
            if (itr.next().atBorderXY(extent)) {
                itr.remove();
            }
        }
    }
}
