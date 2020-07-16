/* (C)2020 */
package org.anchoranalysis.image.orientation;

import org.anchoranalysis.math.rotation.RotationMatrix;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class OrientationRotationMatrix extends Orientation {

    /** */
    private static final long serialVersionUID = -496736778234811706L;

    private RotationMatrix rotationMatrix;

    public OrientationRotationMatrix() {}

    public OrientationRotationMatrix(RotationMatrix rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }

    @Override
    public Orientation duplicate() {
        OrientationRotationMatrix out = new OrientationRotationMatrix();
        out.rotationMatrix = rotationMatrix.duplicate();
        return out;
    }

    @Override
    public RotationMatrix createRotationMatrix() {
        return rotationMatrix;
    }

    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (!(other instanceof OrientationRotationMatrix)) {
            return false;
        }

        OrientationRotationMatrix otherC = (OrientationRotationMatrix) other;
        return rotationMatrix.equals(otherC.rotationMatrix);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(rotationMatrix).toHashCode();
    }

    @Override
    public Orientation negative() {
        // The inverse of a rotation matrix is equal to it's transpose because it's an orthogonal
        // matrix
        RotationMatrix mat = rotationMatrix.duplicate();
        mat.multConstant(-1);
        return new OrientationRotationMatrix(mat);
    }

    public RotationMatrix getRotationMatrix() {
        return rotationMatrix;
    }

    public void setRotationMatrix(RotationMatrix rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }

    @Override
    public int getNumDims() {
        return rotationMatrix.getNumDim();
    }
}
