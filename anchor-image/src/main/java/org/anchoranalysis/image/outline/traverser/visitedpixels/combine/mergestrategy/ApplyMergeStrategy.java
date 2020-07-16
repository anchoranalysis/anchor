/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy;

import java.util.List;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.ReverseUtilities;

class ApplyMergeStrategy extends MergeStrategy {

    private static final boolean REPLACE_WITH_LOOPS = true;

    // If true, we merge from the left-side on keep. If false, rather the right
    private boolean keepLeft;

    // If true, we merge from the left-side on merge. If false, rather the right
    private boolean mergeLeft;

    public ApplyMergeStrategy(boolean keepLeft, boolean mergeLeft, int cost) {
        super(cost);
        this.keepLeft = keepLeft;
        this.mergeLeft = mergeLeft;
    }

    @Override
    public void applyStrategy(MergeCandidate candidate) {

        candidate.removeOrReplace(keepLeft, mergeLeft, REPLACE_WITH_LOOPS);
        appendListToPath(
                candidate.getKeep().getPath(),
                maybeReverse(candidate.getMerge().points(), keepLeft == mergeLeft),
                keepLeft);
    }

    @Override
    public String toString() {
        return String.format(
                "ApplyMergeStrategy(keepLeft=%s,mergeLeft=%s,cost=%d)",
                Boolean.toString(keepLeft), Boolean.toString(mergeLeft), getCost());
    }

    /**
     * Maybe reverses a list of points
     *
     * @param points
     * @param reverseFlag iff TRUE, the points are reversed.
     * @return
     */
    private static List<Point3i> maybeReverse(List<Point3i> points, boolean reverseFlag) {
        if (reverseFlag) {
            return ReverseUtilities.reversedList(points);
        } else {
            return points;
        }
    }

    /**
     * @param toKeep the path which will be kept
     * @param toCombine the path which will be combined with toKeep
     * @param insertLeft if TRUE toCombine is added at the start toKeep, if FALSE rather at the end
     * @param loopInsert1 first set of additional points that are inserted between the appending,
     *     but looped to make sure it starts and ends in the same place
     * @param loopInsert2 second set of additional points that are inserted between the appending,
     *     but looped to make sure it starts and ends in the same place
     */
    private static void appendListToPath(
            ContiguousPixelPath toKeep, List<Point3i> toCombine, boolean insertLeft) {
        if (insertLeft) {
            toKeep.insertBefore(toCombine);
        } else {
            toKeep.insertAfter(toCombine);
        }
    }
}
