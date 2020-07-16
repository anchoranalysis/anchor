/* (C)2020 */
package org.anchoranalysis.math.rotation;

import cern.colt.matrix.DoubleMatrix2D;
import org.anchoranalysis.core.geometry.Vector3d;

public class RotationMatrixFromAxisAngleCreator extends RotationMatrixCreator {

    private Vector3d point;
    private double angle;

    // assumes point is a unit-vector
    public RotationMatrixFromAxisAngleCreator(Vector3d point, double angle) {
        super();
        this.point = point;
        this.angle = angle;
    }

    @Override
    public void createRotationMatrix(RotationMatrix matrix) {
        // http://en.wikipedia.org/wiki/Rotation_matrix

        DoubleMatrix2D mat = matrix.getMatrix();

        double oneMinusCos = 1 - Math.cos(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        mat.set(0, 0, cos + point.getX() * point.getX() * oneMinusCos);
        mat.set(1, 0, point.getY() * point.getX() * oneMinusCos + point.getZ() * sin);
        mat.set(2, 0, point.getZ() * point.getX() * oneMinusCos - point.getY() * sin);

        mat.set(0, 1, point.getX() * point.getY() * oneMinusCos - point.getZ() * sin);
        mat.set(1, 1, cos + point.getY() * point.getY() * oneMinusCos);
        mat.set(2, 1, point.getZ() * point.getY() * oneMinusCos + point.getX() * sin);

        mat.set(0, 2, point.getX() * point.getZ() * oneMinusCos + point.getY() * sin);
        mat.set(1, 2, point.getY() * point.getZ() * oneMinusCos - point.getX() * sin);
        mat.set(2, 2, cos + point.getZ() * point.getZ() * oneMinusCos);
    }

    @Override
    public int getNumDim() {
        return 3;
    }
}
