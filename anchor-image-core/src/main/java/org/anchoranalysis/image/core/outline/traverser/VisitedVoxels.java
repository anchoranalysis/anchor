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

package org.anchoranalysis.image.core.outline.traverser;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.outline.traverser.distance.FindMinimumDistance;
import org.anchoranalysis.image.core.outline.traverser.path.ContiguousVoxelPath;
import org.anchoranalysis.image.core.outline.traverser.path.merge.CombineToOnePath;
import org.anchoranalysis.spatial.point.Point3i;

/** All pixels that have been visited so far */
public class VisitedVoxels {

    private List<ContiguousVoxelPath> paths = new ArrayList<>();

    /** Adds a new path with a point */
    public void addNewPath(Point3i point, Point3i connPoint) {
        ContiguousVoxelPath path = new ContiguousVoxelPath(point, connPoint);
        paths.add(path);
    }

    /** Adds a visited-point to an existing path, or else makes a new one */
    public void addVisitedPoint(Point3i point) {
        for (ContiguousVoxelPath path : paths) {
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
    public ContiguousVoxelPath combineToOnePath() throws OperationFailedException {
        return CombineToOnePath.combineToOnePath(paths);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ContiguousVoxelPath path : paths) {
            sb.append(path.toString());
        }
        return sb.toString();
    }
}
