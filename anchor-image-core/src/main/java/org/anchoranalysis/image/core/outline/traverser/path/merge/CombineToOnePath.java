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

package org.anchoranalysis.image.core.outline.traverser.path.merge;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.outline.traverser.path.ContiguousVoxelPath;
import org.anchoranalysis.image.core.points.PointsNeighborChecker;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CombineToOnePath {

    /**
     * Combines all the contiguous paths to a single-path
     *
     * @throws OperationFailedException
     */
    public static ContiguousVoxelPath combineToOnePath(List<ContiguousVoxelPath> paths)
            throws OperationFailedException {

        FindTargetAndCombine findCombine = new FindTargetAndCombine(paths);

        while (findCombine.combineAnyTwoPaths()) {
            // DO NOTHING
        }

        chopIfNeccessaryToMakeNeighbors(paths.get(0));
        return paths.get(0);
    }

    private static void chopIfNeccessaryToMakeNeighbors(ContiguousVoxelPath singlePath)
            throws OperationFailedException {

        // If the start and end of the path aren't neighbors we should chop
        //  off whatever is necessary to make them neighbors
        if (!areHeadTailConnected(singlePath) && !EnsureContiguousPathLoops.apply(singlePath)) {
            throw new OperationFailedException("head() and tail() of outline are not neigbours");
        }
    }

    private static boolean areHeadTailConnected(ContiguousVoxelPath path) {
        return PointsNeighborChecker.arePointsNeighbors(path.head(), path.tail());
    }
}
