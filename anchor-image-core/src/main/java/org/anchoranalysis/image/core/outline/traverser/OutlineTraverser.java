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
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.image.core.outline.traverser.path.ContiguousVoxelPath;
import org.anchoranalysis.image.core.outline.traverser.visited.VisitedVoxels;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.extent.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/** @author Owen Feehan */
@AllArgsConstructor
public class OutlineTraverser {

    /**
     * The object-mask whose outline is traversed (this object-mask is modified to remove visited
     * pixels)
     *
     * <p>Note this object is actively MODIFIED during execution. Each voxel is removed after being
     * visited.
     */
    private final ObjectMask outline;

    /** Predicate determining whether to visit a particular pixel or not */
    private final ConsiderVisit visitCondition;

    /** Whether to traverse in Z? */
    private final boolean useZ;

    /** Whether to use bigger neighborhoods (8 instead of 4, 12 instead of 6 etc.) */
    private final boolean bigNeighborhood;

    /**
     * Root point is arbitrarily chosen from object
     *
     * @param listOut
     * @throws OperationFailedException
     */
    public void applyGlobal(List<Point3i> listOut) throws OperationFailedException {
        OptionalUtilities.ifPresent(
                outline.findArbitraryOnVoxel(), rootPoint -> applyGlobal(rootPoint, listOut));
    }

    /**
     * @param root this root point should exist on the omOutline (in absolute scene coordinates)
     * @param listOut
     * @throws OperationFailedException
     */
    public void applyGlobal(Point3i root, List<Point3i> listOut) throws OperationFailedException {

        ReadableTuple3i cornerMin = outline.boundingBox().cornerMin();
        Point3i rootRel = BoundingBox.relativePositionTo(root, cornerMin);

        listOut.addAll(applyLocal(rootRel).addShift(cornerMin));
    }

    private ContiguousVoxelPath applyLocal(Point3i rootRel) throws OperationFailedException {

        VisitedVoxels visitedPixels = new VisitedVoxels();
        PriorityQueueVisit queue = new PriorityQueueVisit();

        if (ConsiderNeighbors.considerVisitMarkRaster(visitCondition, rootRel, 0, outline)) {
            queue.add(new Point3iWithDistance(rootRel, 0));
        }

        processQueue(queue, visitedPixels);

        return visitedPixels.combineToOnePath();
    }

    // process FIFO
    private void processQueue(PriorityQueueVisit queue, VisitedVoxels visitedPixels) {

        while (true) {

            Point3iWithDistance point = queue.pop(visitedPixels);

            visit(point, queue, visitedPixels);

            // We go again, assuming next is not empty
            if (queue.isEmpty()) {
                break;
            }
        }
    }

    private void visit(
            Point3iWithDistance pointWithDistance,
            PriorityQueueVisit queue,
            VisitedVoxels visitedPixels) {

        Point3i point = pointWithDistance.getPoint();

        List<Point3iWithDistance> localQueue =
                considerNeighbors(point, pointWithDistance.getDistance());

        if (pointWithDistance.isForceNewPath()) {
            visitedPixels.addNewPath(point, pointWithDistance.getConnPoint());
        } else {
            visitedPixels.addVisitedPoint(point);
        }

        // If there is more than one neighbor, we make sure that a new path is created when it is
        // visited
        if (localQueue.size() > 1) {
            queue.addAllWithConn(localQueue, point);
        } else {
            queue.addAll(localQueue);
        }
    }

    private List<Point3iWithDistance> considerNeighbors(Point3i point, int distance) {

        List<Point3iWithDistance> out = new ArrayList<>();

        ConsiderNeighbors consider =
                new ConsiderNeighbors(outline, distance, out, visitCondition, point);
        consider.considerNeighbors(useZ, bigNeighborhood);
        return out;
    }
}
