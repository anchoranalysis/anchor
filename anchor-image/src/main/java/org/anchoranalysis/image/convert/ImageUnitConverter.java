/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.convert;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.orientation.DirectionVector;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageUnitConverter {

    public static double convertToPhysicalVolume(double value, Resolution res) {
        return value * res.unitVolume();
    }

    public static double convertToPhysicalArea(double value, Resolution res) {
        return value * res.unitArea();
    }

    public static double convertFromPhysicalVolume(double value, Resolution res) {
        return value / res.unitVolume();
    }

    public static double convertFromPhysicalArea(double value, Resolution res) {
        return value / res.unitArea();
    }

    private static double unitDistanceForDirection(Resolution res, DirectionVector dirVector) {
        double x = (dirVector.x()) * res.x();
        double y = (dirVector.y()) * res.y();
        double z = (dirVector.z()) * res.z();
        return Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
    }

    public static double convertToPhysicalDistance(
            double value, Resolution res, DirectionVector dirVector) {
        return value * unitDistanceForDirection(res, dirVector);
    }

    public static double convertFromPhysicalDistance(
            double value, Resolution res, DirectionVector dirVector) {
        return value / unitDistanceForDirection(res, dirVector);
    }

    public static double convertFromMeters(double unitMeters, Resolution res) {
        DirectionVector unitX = new DirectionVector(AxisType.X);
        return ImageUnitConverter.convertFromPhysicalDistance(unitMeters, res, unitX);
    }
}
