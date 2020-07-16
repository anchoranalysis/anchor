/* (C)2020 */
package org.anchoranalysis.math.rotation;

public abstract class RotationMatrixCreator { // NOSONAR

    public RotationMatrix createRotationMatrix() {
        RotationMatrix matrix = new RotationMatrix(getNumDim());
        createRotationMatrix(matrix);
        return matrix;
    }

    public abstract void createRotationMatrix(RotationMatrix matrix);

    public abstract int getNumDim();
}
