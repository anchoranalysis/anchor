/* (C)2020 */
package org.anchoranalysis.image.orientation;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.anchoranalysis.core.geometry.Vector3d;
import org.anchoranalysis.math.rotation.RotationMatrix;
import org.anchoranalysis.math.rotation.RotationMatrixFromAxisAngleCreator;

/**
 * An orientation in axis-angle representation.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Axis%E2%80%93angle_representation>Wikipedia</a>
 * @author Owen Feehan
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class OrientationAxisAngle extends Orientation {

    /** */
    private static final long serialVersionUID = -2592680414423106545L;

    /** Axis part of axis-angle orientation (should be normalized) */
    private Vector3d axis;

    /** Angle part of axis-angle orientation (in radians) */
    private double angle;

    @Override
    public String toString() {
        return String.format("angle=%f axis=%s", angle, axis.toString());
    }

    @Override
    public OrientationAxisAngle duplicate() {
        return new OrientationAxisAngle(new Vector3d(axis), angle);
    }

    @Override
    public RotationMatrix createRotationMatrix() {
        return new RotationMatrixFromAxisAngleCreator(axis, angle).createRotationMatrix();
    }

    @Override
    public Orientation negative() {
        return new OrientationAxisAngle(new Vector3d(axis), angle + Math.PI);
    }

    public static Orientation rotateOneVectorOntoAnother(Vector3d vecSrc, Vector3d vecOnto) {

        final double ep = 10e-12;
        // See http://www.gamedev.net/topic/591937-rotate-one-vector-onto-another/

        // Dot product
        double dotProd = vecSrc.dot(vecOnto);

        Vector3d crossProd = Vector3d.cross(vecSrc, vecOnto);

        double mag = crossProd.length();

        // Also useful as reference
        // http://math.stackexchange.com/questions/293116/rotating-one-3-vector-to-another

        if (mag > ep) {
            crossProd.scale(1 / mag);
            return new OrientationAxisAngle(crossProd, Math.atan2(mag, dotProd));

        } else {

            if (dotProd > 0) {

                // Nearly positively aligned; skip rotation, or compute
                // axis and angle using other means
                return new OrientationIdentity(3);

            } else {

                // negatively aligned we set an angle of PI

                // Nearly negatively aligned; axis is any vector perpendicular
                // to either vector, and angle is 180 degrees
                return new OrientationAxisAngle(findPerpVector(vecSrc), Math.PI);
            }
        }
    }

    //
    private static Vector3d findPerpVector(Vector3d vec) {
        // We needto find any vector whose dot product is 0
        if (vec.getX() > 0 || vec.getY() > 0) {
            return new Vector3d(vec.getY() * -1, vec.getX(), 0);
        } else {
            // This handle's the case where both X and Y are 0
            return new Vector3d(0, vec.getZ() * -1, vec.getY());
        }
    }

    @Override
    public int getNumDims() {
        return 3;
    }
}
