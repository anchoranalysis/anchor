/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

import java.util.List;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.contiguouspath.PointsListNeighborUtilities;

public class CombineToOnePath {

    private CombineToOnePath() {}

    /**
     * Combines all the contiguous paths to a single-path
     *
     * @throws OperationFailedException
     */
    public static ContiguousPixelPath combineToOnePath(List<ContiguousPixelPath> paths)
            throws OperationFailedException {

        FindTargetAndCombine findCombine = new FindTargetAndCombine(paths);

        while (findCombine.combineAnyTwoPaths()) {
            // DO NOTHING
        }

        chopIfNeccessaryToMakeNeighbors(paths.get(0));
        return paths.get(0);
    }

    private static void chopIfNeccessaryToMakeNeighbors(ContiguousPixelPath singlePath)
            throws OperationFailedException {

        // If the start and end of the path aren't neighbors we should chop
        //  off whatever is necessary to make them neighbors
        if (!areHeadTailConnected(singlePath) && !EnsureContiguousPathLoops.apply(singlePath)) {
            throw new OperationFailedException("head() and tail() of outline are not neigbours");
        }
    }

    private static boolean areHeadTailConnected(ContiguousPixelPath path) {
        return PointsListNeighborUtilities.arePointsNeighbors(path.head(), path.tail());
    }
}
