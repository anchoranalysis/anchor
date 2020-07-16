/* (C)2020 */
package org.anchoranalysis.math.rotation;

import cern.colt.matrix.DoubleMatrix2D;

public class RotationMatrix2DFromRadianCreator extends RotationMatrixCreator {

    private double rad;

    public RotationMatrix2DFromRadianCreator(double rad) {
        super();
        this.rad = rad;
    }

    @Override
    public void createRotationMatrix(RotationMatrix rotMatrix) {

        DoubleMatrix2D matrix = rotMatrix.getMatrix();

        RotMatrixIndxCalc r = new RotMatrixIndxCalc(0, 2);

        matrix.set(r.calc(0), r.calc(0), Math.cos(rad));
        matrix.set(r.calc(0), r.calc(1), -1 * Math.sin(rad));
        matrix.set(r.calc(1), r.calc(0), Math.sin(rad));
        matrix.set(r.calc(1), r.calc(1), Math.cos(rad));
    }

    @Override
    public int getNumDim() {
        return 2;
    }
}
