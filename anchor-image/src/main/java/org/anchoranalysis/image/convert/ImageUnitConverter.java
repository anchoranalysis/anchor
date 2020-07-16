/* (C)2020 */
package org.anchoranalysis.image.convert;

import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.DirectionVector;

public class ImageUnitConverter {

    private ImageUnitConverter() {}

    public static double convertToPhysicalVolume(double value, ImageResolution res) {
        return value * res.unitVolume();
    }

    public static double convertToPhysicalArea(double value, ImageResolution res) {
        return value * res.unitArea();
    }

    public static double convertFromPhysicalVolume(double value, ImageResolution res) {
        return value / res.unitVolume();
    }

    public static double convertFromPhysicalArea(double value, ImageResolution res) {
        return value / res.unitArea();
    }

    private static double unitDistanceForDirection(ImageResolution res, DirectionVector dirVector) {
        double x = (dirVector.getX()) * res.getX();
        double y = (dirVector.getY()) * res.getY();
        double z = (dirVector.getZ()) * res.getZ();
        return Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
    }

    public static double convertToPhysicalDistance(
            double value, ImageResolution res, DirectionVector dirVector) {
        return value * unitDistanceForDirection(res, dirVector);
    }

    public static double convertFromPhysicalDistance(
            double value, ImageResolution res, DirectionVector dirVector) {
        return value / unitDistanceForDirection(res, dirVector);
    }

    public static double convertFromMeters(double unitMeters, ImageResolution res) {
        DirectionVector unitX = new DirectionVector(AxisType.X);
        return ImageUnitConverter.convertFromPhysicalDistance(unitMeters, res, unitX);
    }
}
