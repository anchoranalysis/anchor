/* (C)2020 */
package org.anchoranalysis.math.rotation;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

public class RotationMatrix3DFromRadianCreator extends RotationMatrixCreator {

    private double rotX;
    private double rotY;
    private double rotZ;

    public RotationMatrix3DFromRadianCreator(double rotX, double rotY, double rotZ) {
        super();
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    // Assigns values for a rotation about a particular axis (in radians)
    //   axisShift= 0 for x-axis, 1 for y-axis, 2 for z-axis
    private static void assgnRotMatrix(DoubleMatrix2D matrix, double rad, int axisShift) {

        RotMatrixIndxCalc r = new RotMatrixIndxCalc(axisShift, 3);

        matrix.assign(0);
        matrix.set(r.calc(1), r.calc(1), Math.cos(rad));
        matrix.set(r.calc(2), r.calc(1), Math.sin(rad));
        matrix.set(r.calc(1), r.calc(2), -1 * Math.sin(rad));
        matrix.set(r.calc(2), r.calc(2), Math.cos(rad));
        matrix.set(r.calc(0), r.calc(0), 1);
    }

    @Override
    public void createRotationMatrix(RotationMatrix rotMatrix) {

        DoubleMatrix2D matrix = rotMatrix.getMatrix();

        final int matNumDim = 3;

        DoubleFactory2D f = DoubleFactory2D.dense;

        DoubleMatrix2D matRotTmp = f.make(matNumDim, matNumDim);
        DoubleMatrix2D matRotX = f.make(matNumDim, matNumDim);
        DoubleMatrix2D matRotY = f.make(matNumDim, matNumDim);
        DoubleMatrix2D matRotZ = f.make(matNumDim, matNumDim);

        assgnRotMatrix(matRotX, this.rotX, 0);
        assgnRotMatrix(matRotY, this.rotY, 1);
        assgnRotMatrix(matRotZ, this.rotZ, 2);

        matRotX.zMult(matRotY, matRotTmp);
        matRotTmp.zMult(matRotZ, matrix);
    }

    @Override
    public int getNumDim() {
        return 3;
    }
}
