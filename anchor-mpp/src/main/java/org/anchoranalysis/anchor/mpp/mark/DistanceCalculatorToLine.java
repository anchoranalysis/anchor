/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3d;

@NoArgsConstructor
class DistanceCalculatorToLine implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    @Getter private Point3d startPoint;

    @Getter private Point3d endPoint;

    @Getter private Point3d directionVector;

    /**
     * Copy constructor
     *
     * @param src
     */
    public DistanceCalculatorToLine(DistanceCalculatorToLine src) {
        this.startPoint = new Point3d(src.startPoint);
        this.endPoint = new Point3d(src.endPoint);
        this.directionVector = new Point3d(src.directionVector);
    }

    public void setPoints(Point3d startPoint, Point3d endPoint) {
        this.startPoint = new Point3d(startPoint);
        this.endPoint = new Point3d(endPoint);

        // Direction vector
        this.directionVector = new Point3d(this.endPoint);
        this.directionVector.subtract(startPoint);
    }

    public double distanceToLine(Point3d pt) {
        // http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html

        double distanceSquared2to1 = endPoint.distanceSquared(startPoint);
        double distanceSquared1to0 = startPoint.distanceSquared(pt);

        // Let's calculation the dot_product
        double firstX = startPoint.getX() - pt.getX();
        double firstY = startPoint.getY() - pt.getY();
        double firstZ = startPoint.getZ() - pt.getZ();

        double dotProduct =
                (firstX * directionVector.getX())
                        + (firstY * directionVector.getY())
                        + (firstZ * directionVector.getZ());

        double num = (distanceSquared2to1 * distanceSquared1to0) - Math.pow(dotProduct, 2);
        return num / distanceSquared2to1;
    }
}
