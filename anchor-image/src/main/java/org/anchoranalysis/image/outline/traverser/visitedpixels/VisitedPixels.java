/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.combine.CombineToOnePath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.combine.FindMinimumDistance;

/** All pixels that have been visited so far */
public class VisitedPixels {

    private List<ContiguousPixelPath> paths = new ArrayList<>();

    /** Adds a new path with a point */
    public void addNewPath(Point3i point, Point3i connPoint) {
        ContiguousPixelPath path = new ContiguousPixelPath(point, connPoint);
        paths.add(path);
    }

    /** Adds a visited-point to an existing path, or else makes a new one */
    public void addVisitedPoint(Point3i point) {
        for (ContiguousPixelPath path : paths) {
            if (path.maybeAddPointToClosestEnd(point)) {
                return;
            }
        }

        // No existing path can handle the point, so we make a new one
        //  we are not aware of any connection point in this case
        addNewPath(point, null);
    }

    /** The maximum-distance of a point to the closest contiguous-path */
    public int distanceMaxToHeadTail(Point3i point) {
        return FindMinimumDistance.minDistanceMaxToHeadTail(point, paths);
    }

    /**
     * Combines all the contiguous paths to a single-path
     *
     * @throws OperationFailedException
     */
    public ContiguousPixelPath combineToOnePath() throws OperationFailedException {
        return CombineToOnePath.combineToOnePath(paths);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ContiguousPixelPath path : paths) {
            sb.append(path.toString());
        }
        return sb.toString();
    }
}
