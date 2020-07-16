/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FindMergeStrategy {

    /**
     * Determines how we merge paths together
     *
     * <p>If we cannot merge one-end to another-end, we need to chop off bits. And we wish to
     * minimize the bits we chop off
     *
     * @return strategy or NULL if we simply discard toMerge
     */
    public static MergeStrategy apply(PathWithClosest keep, PathWithClosest merge) {

        int lostLeftKeep = keep.distanceFromLeft();
        int lostRightKeep = keep.distanceFromRight();

        int lostLeftMerge = merge.distanceFromLeft();
        int lostRightMerge = merge.distanceFromRight();

        // We consider every combination of merging, minimizing the total lost pixels
        int[] distances =
                new int[] {
                    lostLeftKeep + lostLeftMerge, // left, left
                    lostLeftKeep + lostRightMerge, // left, right
                    lostRightKeep + lostLeftMerge, // right, left
                    lostRightKeep + lostRightMerge, // right, right
                    merge.size() // we throw away merge altogether
                };

        int minIndex = findIndexMinimum(distances);
        int cost = distances[minIndex];

        return createStrategy(minIndex, cost);
    }

    // Finds the index of the minimum value
    private static int findIndexMinimum(int[] arr) {
        int index = -1;
        int minVal = Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            int val = arr[i];
            if (val < minVal) {
                index = i;
                minVal = val;
            }
        }
        return index;
    }

    private static MergeStrategy createStrategy(int index, int cost) {
        switch (index) {
            case 0:
                return new ApplyMergeStrategy(true, true, cost);
            case 1:
                return new ApplyMergeStrategy(true, false, cost);
            case 2:
                return new ApplyMergeStrategy(false, true, cost);
            case 3:
                return new ApplyMergeStrategy(false, false, cost);
            default:
                return new DiscardMergeStrategy(cost);
        }
    }
}
