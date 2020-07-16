/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.geometry.Point3i;

/**
 * A set of points that can be looped, so as to form a path that ends with the same point that it
 * begins with.... without breaking continuity.
 *
 * @author feehano
 */
public class LoopablePoints {

    // This is a point that previously connected with the loop, but which we repeat
    //   after we do a loop, so that the end point is identical to the end of the previous
    //   points
    private Point3i connectingPoint;
    private List<Point3i> points;

    public LoopablePoints(List<Point3i> points, Point3i connectingPoint) {
        super();
        this.points = points;
        this.connectingPoint = connectingPoint;
    }

    public List<Point3i> loopPointsLeft() {
        List<Point3i> out = new ArrayList<>();

        out.add(connectingPoint);

        if (points.size() == 1) {
            // Special behaviour for a single-point as it doesn't need reversing
            out.addAll(points);
        } else {
            out.addAll(ReverseUtilities.reversedList(points));
            out.remove(out.size() - 1); // Remove last item to prevent duplication
            out.addAll(points);
        }

        return out;
    }

    public List<Point3i> loopPointsRight() {
        List<Point3i> out = new ArrayList<>(points);

        if (points.size() == 1) {
            // Special behaviour for a single-point as it doesn't need reversing
        } else {
            out.remove(out.size() - 1); // Remove last item to prevent duplication
            out.addAll(ReverseUtilities.reversedList(points));
        }

        out.add(connectingPoint);
        return out;
    }

    public int size() {
        return points.size();
    }

    public List<Point3i> points() {
        return points;
    }

    @Override
    public String toString() {
        return points.toString();
    }
}
