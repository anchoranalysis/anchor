/* (C)2020 */
package org.anchoranalysis.core.unit;

/**
 * Converting angles between degrees and radians
 *
 * @author Owen Feehan
 */
public class AngleConversionUtilities {

    private AngleConversionUtilities() {}

    public static double convertDegreesToRadians(double degrees) {
        return (Math.PI / 180) * degrees;
    }

    public static double convertRadiansToDegrees(double radians) {
        return (radians / Math.PI) * 180;
    }
}
