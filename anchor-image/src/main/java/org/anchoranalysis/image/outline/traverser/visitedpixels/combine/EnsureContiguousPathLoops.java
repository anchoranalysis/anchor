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

package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.contiguouspath.PointsListNeighborUtilities;

/** Makes sure the head() and tail() of a ContiguousPath are neighbors */
class EnsureContiguousPathLoops {

    private EnsureContiguousPathLoops() {}

    /** Ensures the head() and tail() of a path are neighbors, chopping off points if appropriate */
    public static boolean apply(ContiguousPixelPath path) {

        // Look for all points on the path which neighbor each other
        // Consider all possibilities, and implement the cut that removes the minimal pixels
        IndexOffsets offsets = new FindMinimum(path).apply();

        path.removeLeft(offsets.left());
        path.removeRight(offsets.rightReversed());

        return true;
    }

    /**
     * A left and right index
     *
     * @author feehano
     */
    @Accessors(fluent = true)
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class IndexOffsets {

        // START REQUIRED ARGUMENTS
        private final int size;
        // END REQUIRED ARGUMENTS

        @Getter private int left = -1;

        @Getter private int right = -1;

        /**
         * Explictly updates the left and right index
         *
         * @param left
         * @param right
         */
        public void update(int left, int right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Updates left and right indexes to be a particular distance from both ends of the path
         *
         * @param distance
         */
        public void updateDistanceFromSides(int distance) {
            update(Math.min(size, distance), Math.max(size - distance, 0));
        }

        /** The index relative to the right-side of the paht */
        public int rightReversed() {
            return size - right - 1;
        }
    }

    private static class FindMinimum {

        private int minCost = Integer.MAX_VALUE;
        private IndexOffsets minCostIndexes;
        private IndexOffsets boundIndexes;
        private ContiguousPixelPath path;

        public FindMinimum(ContiguousPixelPath path) {

            this.path = path;

            minCostIndexes = new IndexOffsets(path.size());

            // PLACES BOUNDS ON WHERE WE SEARCH
            // We will never search beyond this index, as a cost will
            //  always be greater than one we already found
            boundIndexes = new IndexOffsets(path.size(), path.size(), 0);
        }

        public IndexOffsets apply() {

            for (int i = 0; i < boundIndexes.left(); i++) {
                Point3i pointLeft = path.get(i);

                for (int j = boundIndexes.right(); j < path.size(); j++) {

                    if (i == j) {
                        continue;
                    }

                    maybeUpdate(i, j, pointLeft, path.get(j));
                }
            }

            if (minCostIndexes.left() == -1) {
                // We failed to find any neighboring points to cut with
                // This should never happen, as long as there are at least two neighboring points
                throw new AnchorImpossibleSituationException();
            }

            return minCostIndexes;
        }

        private void maybeUpdate(int i, int j, Point3i pointLeft, Point3i pointRight) {

            if (PointsListNeighborUtilities.arePointsNeighbors(pointLeft, pointRight)) {

                // Then it's possible we make these two points the head and tail of
                //   the list. This would cost us a certain number of pixels, that
                //  we would need to remove.
                int cost = i + (path.size() - j - 1);

                if (cost < minCost) {
                    minCostIndexes.update(i, j);
                    minCost = cost;
                    boundIndexes.updateDistanceFromSides(cost);
                }
            }
        }
    }
}
