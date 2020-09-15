/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.outline.traverser.visited.combine;

import java.util.List;
import org.anchoranalysis.image.outline.traverser.path.ContiguousVoxelPath;

class FindTargetAndCombine {

    private List<ContiguousVoxelPath> paths;

    public FindTargetAndCombine(List<ContiguousVoxelPath> paths) {
        super();
        this.paths = paths;
    }

    /**
     * Tries to combine the first path from the list with another (closest by distance).
     *
     * <p>Returns true when a successful combination occurs, false otherwise
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

        ContiguousVoxelPath p2 = paths.get(indexCombine.getIndexHighest());

        // Figure out whether to combine from the left-side, or combine from the right-side
        return new MergeTarget(p2, indexCombine.getIndexHighest(), indexCombine.getIndexLowest());
    }
}
