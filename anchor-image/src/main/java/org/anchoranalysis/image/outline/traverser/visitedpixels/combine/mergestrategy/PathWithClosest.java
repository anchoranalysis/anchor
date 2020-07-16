/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine.mergestrategy;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.contiguouspath.DistanceToContiguousPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.LoopablePoints;

class PathWithClosest {

    @Getter private ContiguousPixelPath path;

    private int closest;

    public PathWithClosest(ContiguousPixelPath path, Point3i mergePoint) {
        this.path = path;
        closest = indexClosest(path, mergePoint);
    }

    public Optional<LoopablePoints> removeLeft() {
        return path.removeLeft(closest);
    }

    public Optional<LoopablePoints> removeRight() {
        return path.removeRight(size() - closest - 1);
    }

    public int distanceFromLeft() {
        return closest;
    }

    public int distanceFromRight() {
        return size() - closest - 1;
    }

    public int size() {
        return path.size();
    }

    private static int indexClosest(ContiguousPixelPath path, Point3i mergePoint) {
        return DistanceToContiguousPath.maxDistanceToClosestPoint(path, mergePoint).getIndex();
    }

    public List<Point3i> points() {
        return path.points();
    }

    public void insertBefore(List<Point3i> points) {
        path.insertBefore(points);
        closest += points.size();
    }

    public void insertAfter(List<Point3i> points) {
        path.insertAfter(points);
    }
}
