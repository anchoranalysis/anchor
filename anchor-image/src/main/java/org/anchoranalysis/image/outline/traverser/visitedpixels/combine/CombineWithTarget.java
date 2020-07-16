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
