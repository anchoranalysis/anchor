/* (C)2020 */
package org.anchoranalysis.image.orientation;

import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.Tuple3d;
import org.anchoranalysis.core.geometry.Vector3d;

public class DirectionVector {

    private Tuple3d vector;

    public DirectionVector() {
        vector = new Vector3d();
    }

    public DirectionVector(Tuple3d vector) {
        this.vector = vector;
    }

    public DirectionVector(double x, double y, double z) {
        this();
        vector.setX(x);
        vector.setY(y);
        vector.setZ(z);
    }

    /**
     * Constructor - creates a direction-vector aligned to a particular axis
     *
     * @param axisName
     */
    public DirectionVector(AxisType axisType) {
        switch (axisType) {
            case X:
                this.vector = new Point3d(1, 0, 0);
                break;
            case Y:
                this.vector = new Point3d(0, 1, 0);
                break;
            case Z:
                this.vector = new Point3d(0, 0, 1);
                break;
            default:
                throw new AnchorImpossibleSituationException();
        }
    }

    public final double getX() {
        return vector.getX();
    }

    public final double getY() {
        return vector.getY();
    }

    public final double getZ() {
        return vector.getZ();
    }

    public final void setX(double arg0) {
        vector.setX(arg0);
    }

    public final void setY(double arg0) {
        vector.setY(arg0);
    }

    public final void setZ(double arg0) {
        vector.setZ(arg0);
    }

    /**
     * Sets an element of the vector by the index of its position, index=0 is the X-element, index=1
     * is the Y-element etc.
     */
    public void setIndex(int index, double value) {
        if (index == 0) {
            vector.setX(value);
        } else if (index == 1) {
            vector.setY(value);
        } else if (index == 2) {
            vector.setZ(value);
        } else {
            throw new AnchorFriendlyRuntimeException("Index must be >= 0 and < 3");
        }
    }

    public static DirectionVector createBetweenTwoPoints(Point3d point1, Point3d point2) {

        double sx = point2.getX() - point1.getX();
        double sy = point2.getY() - point1.getY();
        double sz = point2.getZ() - point1.getZ();

        double norm = Math.sqrt(Math.pow(sx, 2.0) + Math.pow(sy, 2.0) + Math.pow(sz, 2.0));

        DirectionVector out = new DirectionVector();
        out.setX(sx / norm);
        out.setY(sy / norm);
        out.setZ(sz / norm);
        return out;
    }

    public static DirectionVector createBetweenTwoPoints(Point3i point1, Point3i point2) {

        double sx = (double) point2.getX() - point1.getX();
        double sy = (double) point2.getY() - point1.getY();
        double sz = (double) point2.getZ() - point1.getZ();

        double norm = Math.sqrt(Math.pow(sx, 2.0) + Math.pow(sy, 2.0) + Math.pow(sz, 2.0));

        DirectionVector out = new DirectionVector();
        out.setX(sx / norm);
        out.setY(sy / norm);
        out.setZ(sz / norm);
        return out;
    }

    public Vector3d createVector3d() {
        return new Vector3d(vector);
    }
}
