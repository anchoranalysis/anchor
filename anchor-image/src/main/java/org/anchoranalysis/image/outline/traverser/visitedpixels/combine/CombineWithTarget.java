/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy.MergeCandidate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CombineWithTarget {

    /**
     * @param paths
     * @param p1
     * @param p2
     * @param mergePoint
     * @param indexHighest
     * @return the number of pixels deleted through the combination
     */
    public static int combineAndRemove(
            List<ContiguousPixelPath> paths, MergeTarget mergeSrc, MergeTarget mergeTarget) {
        return combineDetermineOrder(paths, mergeSrc, mergeTarget);
    }

    /**
     * @param paths
     * @param mergeSrc
     * @param mergeTarget
     * @return the number of removed pixels
     */
    private static int combineDetermineOrder(
            List<ContiguousPixelPath> paths, MergeTarget mergeSrc, MergeTarget mergeTarget) {
        ContiguousPixelPath p1 = mergeSrc.getPath();
        ContiguousPixelPath p2 = mergeTarget.getPath();
        Point3i mergePoint = mergeTarget.mergePoint();

        MergeCandidate cand = new MergeCandidate(p1, p2, mergePoint, mergePoint);

        // We always keep the larger one
        if (mergeSrc.getPath().size() > mergeTarget.getPath().size()) {
            return combine(paths, cand, mergeTarget.getIndexPath());
        } else {
            return combine(paths, cand.reverse(), mergeSrc.getIndexPath());
        }
    }

    private static int combine(
            List<ContiguousPixelPath> paths, MergeCandidate candidate, int removeIndex) {
        int numPixelsLost = candidate.merge();
        paths.remove(removeIndex);
        return numPixelsLost;
    }

    public static int cnt(List<ContiguousPixelPath> paths) {
        int sum = 0;
        for (ContiguousPixelPath cpp : paths) {
            sum += cpp.size();
        }
        return sum;
    }
}
