/* (C)2020 */
package org.anchoranalysis.image.contour;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;

/**
 * A path of successively-neighboring points along the edge of an object
 *
 * @author Owen Feehan
 */
public class Contour {

    @Getter private List<Point3f> points = new ArrayList<>();

    private static final double MAX_DISTANCE_TO_DEFINED_CONNECTED = 2;

    public List<Point3i> pointsDiscrete() {
        return PointConverter.convert3i(getPoints(), false);
    }

    public boolean isClosed() {
        return points.get(0).distance(points.get(points.size() - 1))
                < MAX_DISTANCE_TO_DEFINED_CONNECTED;
    }

    public boolean hasPoint(Point3f pointC) {
        for (Point3f point : getPoints()) {
            if (point.equals(pointC)) {
                return true;
            }
        }
        return false;
    }

    public Point3f getFirstPoint() {
        return points.get(0);
    }

    public Point3f getMiddlePoint() {
        return points.get(points.size() / 2);
    }

    public Point3f getLastPoint() {
        return points.get(points.size() - 1);
    }

    public boolean connectedTo(Contour contour) {

        if (connectedToFirstPointOf(contour)) {
            return true;
        }

        return connectedToLastPointOf(contour);
    }

    public boolean connectedToFirstPointOf(Contour contour) {
        return getLastPoint().distance(contour.getFirstPoint()) < MAX_DISTANCE_TO_DEFINED_CONNECTED;
    }

    public boolean connectedToLastPointOf(Contour contour) {
        return getFirstPoint().distance(contour.getLastPoint()) < MAX_DISTANCE_TO_DEFINED_CONNECTED;
    }

    public String summaryStr() {
        return String.format("[%s-%s]", points.get(0), points.get(points.size() - 1));
    }
}
