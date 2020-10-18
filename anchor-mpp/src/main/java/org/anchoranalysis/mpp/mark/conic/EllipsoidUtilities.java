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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Optional;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.rotation.RotationMatrix;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EllipsoidUtilities {

    public static double[] normalisedRadii(Ellipsoid mark, Optional<Resolution> resolution) {
        // We re-calculate all the bounds to take account of the different z-resolution

        // We get the rotated points of (1,0,0)*getRadii().x() and (0,1,0)*getRadii().y() and
        // (0,1,0)*getRadii().z()
        RotationMatrix rotMatrix = mark.getOrientation().createRotationMatrix();

        Point3d xRot = rotMatrix.rotatedPoint(new Point3d(mark.getRadii().x(), 0, 0));
        Point3d yRot = rotMatrix.rotatedPoint(new Point3d(0, mark.getRadii().y(), 0));
        Point3d zRot = rotMatrix.rotatedPoint(new Point3d(0, 0, mark.getRadii().z()));

        adjustForZ(xRot, yRot, zRot, resolution);

        Point3d zero = new Point3d(0, 0, 0);

        double xNorm = xRot.distance(zero);
        double yNorm = yRot.distance(zero);
        double zNorm = zRot.distance(zero);

        return new double[] {xNorm, yNorm, zNorm};
    }
    
    private static void adjustForZ(Point3d xRot, Point3d yRot, Point3d zRot, Optional<Resolution> resolution) {
        // We adjust each point for the z contribution
        if (resolution.isPresent()) {
            double zRel = resolution.get().zRelative();
            xRot.setZ(xRot.z() * zRel);
            yRot.setZ(yRot.z() * zRel);
            zRot.setZ(zRot.z() * zRel);
        }
    }
}
