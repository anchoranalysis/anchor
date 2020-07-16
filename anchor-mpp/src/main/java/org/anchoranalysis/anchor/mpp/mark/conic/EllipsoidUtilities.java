/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.conic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.math.rotation.RotationMatrix;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EllipsoidUtilities {

    public static double[] normalisedRadii(MarkEllipsoid mark, ImageResolution res) {
        // We re-calculate all the bounds to take account of the different z-resolution

        // We get the rotated points of (1,0,0)*getRadii().getX() and (0,1,0)*getRadii().getY() and
        // (0,1,0)*getRadii().getZ()
        RotationMatrix rotMatrix = mark.getOrientation().createRotationMatrix();

        Point3d xRot = rotMatrix.calcRotatedPoint(new Point3d(mark.getRadii().getX(), 0, 0));
        Point3d yRot = rotMatrix.calcRotatedPoint(new Point3d(0, mark.getRadii().getY(), 0));
        Point3d zRot = rotMatrix.calcRotatedPoint(new Point3d(0, 0, mark.getRadii().getZ()));

        double zRel = res.getZRelativeResolution();
        // We adjust each point for the z contribution
        xRot.setZ(xRot.getZ() * zRel);
        yRot.setZ(yRot.getZ() * zRel);
        zRot.setZ(zRot.getZ() * zRel);

        Point3d zero = new Point3d(0, 0, 0);

        double xNorm = xRot.distance(zero);
        double yNorm = yRot.distance(zero);
        double zNorm = zRot.distance(zero);

        return new double[] {xNorm, yNorm, zNorm};
    }
}
