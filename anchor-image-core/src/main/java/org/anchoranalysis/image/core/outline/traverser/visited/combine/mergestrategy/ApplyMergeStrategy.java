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

package org.anchoranalysis.image.core.outline.traverser.visited.combine.mergestrategy;

import java.util.List;
import org.anchoranalysis.image.core.outline.traverser.path.ContiguousVoxelPath;
import org.anchoranalysis.image.core.outline.traverser.visited.ReverseUtilities;
import org.anchoranalysis.spatial.point.Point3i;

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
     * @param reverseFlag iff true, the points are reversed.
     * @return either the existing list (if not reversed) or a newly created reversed list
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
     * @param insertLeft if true toCombine is added at the start toKeep, if false rather at the end
     */
    private static void appendListToPath(
            ContiguousVoxelPath toKeep, List<Point3i> toCombine, boolean insertLeft) {
        if (insertLeft) {
            toKeep.insertBefore(toCombine);
        } else {
            toKeep.insertAfter(toCombine);
        }
    }
}
