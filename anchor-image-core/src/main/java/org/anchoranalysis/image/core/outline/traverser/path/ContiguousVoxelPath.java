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

package org.anchoranalysis.image.core.outline.traverser.path;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * A list of visited pixels which forms one contiguous path where each voxel neighbors each other.
 */
public class ContiguousVoxelPath {

    private List<Point3i> list;

    @Getter private Optional<Point3i> initialPoint;

    @Getter private Optional<Point3i> connPoint;

    /** With a single initial-point, and maybe a connection point */
    public ContiguousVoxelPath(Point3i initialPoint, Point3i connectionPoint) {
        this(Optional.of(connectionPoint));
        this.initialPoint = Optional.of(initialPoint);
        maybeAddPointToClosestEnd(initialPoint);
    }

    /** Without any connection-point */
    public ContiguousVoxelPath() {
        this(Optional.empty());
        initialPoint = Optional.empty();
    }

    private ContiguousVoxelPath(Optional<Point3i> connPoint) {
        this.connPoint = connPoint;
        initialPoint = Optional.empty();
        list = new ArrayList<>();
    }

    public ContiguousVoxelPath duplicate() {
        ContiguousVoxelPath out = new ContiguousVoxelPath(connPoint);
        out.list.addAll(list);
        return out;
    }

    /**
     * Adds the point to the closest end of the path.... but only if it neighbors the head or the
     * tail
     *
     * @param point the point to add
     * @return true if point was successfully added, false if the point could not be added
     */
    public boolean maybeAddPointToClosestEnd(Point3i point) {

        if (list.isEmpty()) {
            list.add(point);
            return true;
        }

        // In an effort to keep the outline as connected as possible, we consider the head and tail
        //  of the list, and add the point to the end which minimal distance

        int distanceHead = point.distanceMax(head());
        int distanceTail = point.distanceMax(tail());

        if (distanceHead == 1) {
            // If close to head than tail
            list.add(0, point);
            return true;
        } else if (distanceTail == 1) {
            // If close to tail than head
            list.add(point);
            return true;
        } else {
            // This point can't be fit to either the head or the tail
            return false;
        }
    }

    /**
     * Removes the first numToRemove pixels from the left
     *
     * @return Returns the pixels removed (or empty() if numToRemove==0)
     */
    public Optional<LoopablePoints> removeLeft(int numToRemove) {

        if (numToRemove == 0) {
            return Optional.empty();
        }

        List<Point3i> toRemove = copySubList(list, 0, numToRemove);

        list = list.subList(numToRemove, list.size());

        return Optional.of(new LoopablePoints(toRemove, list.get(0)));
    }

    /**
     * Removes the last numToRemove pixels from the right
     *
     * @return Returns the pixels removed (or empty() if numToRemove==0)
     */
    public Optional<LoopablePoints> removeRight(int numToRemove) {

        if (numToRemove == 0) {
            return Optional.empty();
        }

        int finalIndex = list.size() - numToRemove;
        List<Point3i> toRemove = copySubList(list, finalIndex, list.size());

        list = list.subList(0, finalIndex);

        return Optional.of(
                new LoopablePoints(
                        toRemove, // Looped
                        list.get(list.size() - 1)));
    }

    /** Inserts points before existing path. Does not check if they are neighbors. */
    public void insertBefore(List<Point3i> points) {
        list.addAll(0, points);
    }

    /** Inserts points at end of existing path. Does not check if they are neighbors. */
    public void insertAfter(List<Point3i> points) {
        list.addAll(points);
    }

    public Point3i get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    /**
     * Adds a shift to each point (modifying the existing points in memory), and returns them as a
     * list
     */
    public List<Point3i> addShift(ReadableTuple3i shift) {
        for (Point3i relPoint : list) {
            relPoint.add(shift);
        }
        return list;
    }

    /** The points associated with the path */
    public List<Point3i> points() {
        return list;
    }

    @Override
    public String toString() {
        return String.format(
                "Path ( head=%s\ttail=%s\tsize=%d initialPoint=%s connPoint=%s )",
                head(), tail(), list.size(), pointOrNull(initialPoint), pointOrNull(connPoint));
    }

    private static String pointOrNull(Optional<Point3i> point) {
        return point.map(Point3i::toString).orElse("null");
    }

    public Point3i tail() {
        return list.get(list.size() - 1);
    }

    public Point3i head() {
        return list.get(0);
    }

    private static List<Point3i> copySubList(List<Point3i> list, int from, int to) {
        return new ArrayList<>(list.subList(from, to));
    }

    public Optional<Integer> indexInitialPoint() {
        return initialPoint.map(point -> list.indexOf(point));
    }
}
