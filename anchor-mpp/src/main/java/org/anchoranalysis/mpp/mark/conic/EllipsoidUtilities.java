/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.mpp.mark.conic;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.spatial.orientation.RotationMatrix;
import org.anchoranalysis.spatial.point.Point3d;

/** Utility class for operations related to Ellipsoid marks. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EllipsoidUtilities {

    /**
     * Calculates the normalized radii of an Ellipsoid mark, taking into account rotation and
     * optional resolution.
     *
     * @param mark the Ellipsoid mark
     * @param resolution optional Resolution to adjust for z-axis scaling
     * @return an array of three doubles representing the normalized radii in x, y, and z directions
     */
    public static double[] normalisedRadii(Ellipsoid mark, Optional<Resolution> resolution) {
        RotationMatrix rotMatrix = mark.getOrientation().getRotationMatrix();

        Point3d xRot = new Point3d(mark.getRadii().x(), 0, 0);
        Point3d yRot = new Point3d(0, mark.getRadii().y(), 0);
        Point3d zRot = new Point3d(0, 0, mark.getRadii().z());

        rotMatrix.rotatePointInplace(xRot);
        rotMatrix.rotatePointInplace(yRot);
        rotMatrix.rotatePointInplace(zRot);

        adjustForZ(xRot, yRot, zRot, resolution);

        Point3d zero = new Point3d(0, 0, 0);

        double xNorm = xRot.distance(zero);
        double yNorm = yRot.distance(zero);
        double zNorm = zRot.distance(zero);

        return new double[] {xNorm, yNorm, zNorm};
    }

    /**
     * Adjusts the z-coordinates of the rotated points based on the provided resolution.
     *
     * @param xRot rotated point representing x-axis
     * @param yRot rotated point representing y-axis
     * @param zRot rotated point representing z-axis
     * @param resolution optional Resolution to adjust for z-axis scaling
     */
    private static void adjustForZ(
            Point3d xRot, Point3d yRot, Point3d zRot, Optional<Resolution> resolution) {
        if (resolution.isPresent()) {
            double zRel = resolution.get().zRelative();
            xRot.setZ(xRot.z() * zRel);
            yRot.setZ(yRot.z() * zRel);
            zRot.setZ(zRot.z() * zRel);
        }
    }
}
