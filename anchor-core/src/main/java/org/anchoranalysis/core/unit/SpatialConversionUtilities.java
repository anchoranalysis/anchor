/*-
 * #%L
 * anchor-core
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
/* (C)2020 */
package org.anchoranalysis.core.unit;

/**
 * Utilities for converting between units (represented as enums) and units (represented as strings)
 *
 * <p>e.g. find a enum for "nm" e.g. find a string representation for NANO
 *
 * @author Owen Feehan
 */
public class SpatialConversionUtilities {

    private static final String STR_MICRO_CHAR = "\u00B5";

    private static final String STR_MICRO_METER = STR_MICRO_CHAR + "m";
    private static final String STR_MICRO_METER_SQUARED = STR_MICRO_CHAR + "m^2";
    private static final String STR_MICRO_METER_CUBED = STR_MICRO_CHAR + "m^3";

    public enum UnitSuffix {
        NONE,
        BASE,
        CUBIC_NANO,
        SQUARE_NANO,
        NANO,
        CUBIC_MICRO,
        SQUARE_MICRO,
        MICRO
    }

    private SpatialConversionUtilities() {
        // Class exists only its static methods
    }

    private static String unsupportedString(String suffixStr) {
        return String.format("Suffix string: '%s' not supported", suffixStr);
    }

    private static String unsupportedUnitType(UnitSuffix unitType) {
        return String.format("Unit type: '%s' not supported", unitType);
    }

    public static UnitSuffix suffixFromMeterString(String suffixStr) { // NOSONAR

        // If no units
        if (suffixStr == null || suffixStr.isEmpty()) {
            return UnitSuffix.NONE;
        }

        // Metres
        if ("m".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.BASE;
        }

        // Nano-metres
        if ("nm^3".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.CUBIC_NANO;

        } else if ("nm^2".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.SQUARE_NANO;

        } else if ("nm".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.NANO;
        }

        // Micro metres
        if (STR_MICRO_METER_CUBED.equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.CUBIC_MICRO;
        } else if (STR_MICRO_METER_SQUARED.equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.SQUARE_MICRO;
        } else if (STR_MICRO_METER.equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.MICRO;
        }

        throw new IllegalArgumentException(unsupportedString(suffixStr));
    }

    public static String unitMeterStringDisplay(UnitSuffix unitType) { // NOSONAR
        switch (unitType) {
            case CUBIC_NANO:
                return "nm^3";

            case SQUARE_NANO:
                return "nm^2";

            case NANO:
                return "nm";

            case CUBIC_MICRO:
                return STR_MICRO_CHAR + "m^3";

            case SQUARE_MICRO:
                return STR_MICRO_CHAR + "m^2";

            case MICRO:
                return STR_MICRO_CHAR + "m";

            default:
                throw new IllegalArgumentException(unsupportedUnitType(unitType));
        }
    }

    public static double convertToUnits(double base, UnitSuffix unitType) { // NOSONAR

        switch (unitType) {
            case NONE:
                return base;

            case BASE:
                return base;

            case CUBIC_NANO:
                return base / 1e-27;

            case SQUARE_NANO:
                return base / 1e-18;

            case NANO:
                return base / 1e-9;

            case CUBIC_MICRO:
                return base / 1e-18;

            case SQUARE_MICRO:
                return base / 1e-12;

            case MICRO:
                return base / 1e-6;

            default:
                throw new IllegalArgumentException(unsupportedUnitType(unitType));
        }
    }

    public static double convertFromUnits(double base, UnitSuffix unitType) { // NOSONAR

        switch (unitType) {
            case NONE:
                return base;
            case BASE:
                return base;
            case CUBIC_NANO:
                return base * 1e-27;
            case SQUARE_NANO:
                return base * 1e-18;
            case NANO:
                return base * 1e-9;
            case CUBIC_MICRO:
                return base * 1e-18;
            case SQUARE_MICRO:
                return base * 1e-12;
            case MICRO:
                return base * 1e-6;
            default:
                throw new IllegalArgumentException(unsupportedUnitType(unitType));
        }
    }
}
