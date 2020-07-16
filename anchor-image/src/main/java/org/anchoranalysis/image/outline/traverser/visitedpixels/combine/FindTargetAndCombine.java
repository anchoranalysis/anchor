/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

import java.util.List;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;

class FindTargetAndCombine {

    private List<ContiguousPixelPath> paths;

    public FindTargetAndCombine(List<ContiguousPixelPath> paths) {
        super();
        this.paths = paths;
    }

    /**
     * Tries to combine the first path from the list with another (closest by distance).
     *
     * <p>Returns TRUE when a successful combination occurs, FALSE otherwise
     */
    public boolean combineAnyTwoPaths() {

        if (paths.size() == 1) {
            // Nothing to do
            return false;
        }

        // We merge this path with another
        return mergePath(new MergeTarget(paths.get(0), 0, 0));
    }

    private boolean mergePath(MergeTarget mergeSrc) {

        MergeTarget mergeTarget = findTarget(mergeSrc);

        // Find insertion point into the current list
        int removedPixels = CombineWithTarget.combineAndRemove(paths, mergeSrc, mergeTarget);

        // Check that we never removed more than a certain number of pixels in order to merge
        assert (removedPixels < 10);

        return true;
    }

    private MergeTarget findTarget(MergeTarget src) {

        // Find the closest segment to the tail() of p1. The choice of tail (as opposed to head) is
        // arbitrary.
        DistanceIndexTwice indexCombine =
                FindMinimumDistance.indexDistanceMaxToClosestPoint(
                        src.getPath().tail(), paths, src.getIndexPath());

        ContiguousPixelPath p2 = paths.get(indexCombine.getIndexHighest());

        // Figure out whether to combine from the left-side, or combine from the right-side
        return new MergeTarget(p2, indexCombine.getIndexHighest(), indexCombine.getIndexLowest());
    }
}
