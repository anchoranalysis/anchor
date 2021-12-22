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

package org.anchoranalysis.image.core.dimensions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Units to describe spatial quantities.
 *
 * <p>These units can be specified by a string or an enum.
 *
 * <p>e.g. find a enum for <i>nm</i>.
 *
 * <p>e.g. find a string representation for <i>NANO</i>.
 * 
 * <p>Acceptable string representations are: {@code m, mm, mm^2, mm^3, nm, nm^2, nm^3, μm, μm^2, μm^3}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpatialUnits {

    private static final String STR_MICRO_METER = "μm";
    private static final String STR_MICRO_METER_SQUARED = "μm^2";
    private static final String STR_MICRO_METER_CUBED = "μm^3";

    /** Suffix that describes a particular quantity of units (micron, nano, square microns etc.) */
    public enum UnitSuffix {
        /** No suffix. */
        NONE,

        /** A unit amount. */
        BASE,

        /** 1e-9 cubed. */
        CUBIC_NANO,

        /** 1e-9 squared. */
        SQUARE_NANO,

        /** 1e-9 */
        NANO,

        /** 1e-6 cubed. */
        CUBIC_MICRO,

        /** 1e-6 squared. */
        SQUARE_MICRO,

        /** 1e-6. */
        MICRO,
        
        /** 1e-3 cubed. */
        CUBIC_MILLI,

        /** 1e-3 squared. */
        SQUARE_MILLI,

        /** 1e-3. */
        MILLI
    }

    /**
     * A string that describes the suffix for meters.
     *
     * <p>e.g. {@code m^2} or {@code nm^3} etc.
     *
     * @param suffix the suffix to describe.
     * @return a string describing the suffix as applied to meters.
     */
    public static String suffixStringForMeters(UnitSuffix suffix) { // NOSONAR
        switch (suffix) {
	        case CUBIC_MILLI:
	            return "mm^3";
	
	        case SQUARE_MILLI:
	            return "mm^2";
	
	        case MILLI:
	            return "m";        
        
            case CUBIC_NANO:
                return "nm^3";

            case SQUARE_NANO:
                return "nm^2";

            case NANO:
                return "nm";

            case CUBIC_MICRO:
                return "μm^3";

            case SQUARE_MICRO:
                return "μm^2";

            case MICRO:
                return "μm";

            default:
                throw new IllegalArgumentException(unsupportedUnitType(suffix));
        }
    }

    /**
     * Converts a value in base-units <b>to</b> another unit-type.
     *
     * @param valueBaseUnits the value in base units.
     * @param unitSuffix the suffix describing the desired unit.
     * @return the value in units of type @{code unitSuffix}.
     */
    public static double convertToUnits(double valueBaseUnits, String unitSuffix) { // NOSONAR
        SpatialUnits.UnitSuffix suffix = SpatialUnits.suffixFromMeterString(unitSuffix);
        return convertToUnits(valueBaseUnits, suffix);
    }

    /**
     * Converts a value in base-units <b>to</b> another unit-type.
     *
     * @param valueBaseUnits the value in base units.
     * @param unitSuffix the suffix describing the desired unit.
     * @return the value in units of type @{code unitSuffix}.
     */
    public static double convertToUnits(double valueBaseUnits, UnitSuffix unitSuffix) { // NOSONAR

        switch (unitSuffix) {
            case NONE:
                return valueBaseUnits;

            case BASE:
                return valueBaseUnits;
                
            // START MILLI                
            case CUBIC_MILLI:
                return valueBaseUnits / 1e-9;

            case SQUARE_MILLI:
                return valueBaseUnits / 1e-6;

            case MILLI:
                return valueBaseUnits / 1e-3;
            // END MILLI
                
            // START MICRO
            case CUBIC_MICRO:
                return valueBaseUnits / 1e-18;

            case SQUARE_MICRO:
                return valueBaseUnits / 1e-12;

            case MICRO:
                return valueBaseUnits / 1e-6;
            // END MICRO

            // START NANO
            case CUBIC_NANO:
                return valueBaseUnits / 1e-27;

            case SQUARE_NANO:
                return valueBaseUnits / 1e-18;

            case NANO:
                return valueBaseUnits / 1e-9;
            // END NANO

            default:
                throw new IllegalArgumentException(unsupportedUnitType(unitSuffix));
        }
    }

    /**
     * Converts a value <b>from</b> another unit-type to base units.
     *
     * @param valueUnits the value in base units.
     * @param unitSuffix the suffix associated with {@code valueUnits}.
     * @return the value in base units.
     */
    public static double convertFromUnits(double valueUnits, String unitSuffix) { // NOSONAR
        SpatialUnits.UnitSuffix suffix = SpatialUnits.suffixFromMeterString(unitSuffix);
        return convertFromUnits(valueUnits, suffix);
    }

    /**
     * Converts a value <b>from</b> another unit-type to base units.
     *
     * @param valueUnits the value in base units.
     * @param unitSuffix the suffix associated with {@code valueUnits}.
     * @return the value in base units.
     */
    public static double convertFromUnits(double valueUnits, UnitSuffix unitSuffix) { // NOSONAR

        switch (unitSuffix) {
            case NONE:
                return valueUnits;
            case BASE:
                return valueUnits;
            case CUBIC_NANO:
                return valueUnits * 1e-27;
            case SQUARE_NANO:
                return valueUnits * 1e-18;
            case NANO:
                return valueUnits * 1e-9;
            case CUBIC_MICRO:
                return valueUnits * 1e-18;
            case SQUARE_MICRO:
                return valueUnits * 1e-12;
            case MICRO:
                return valueUnits * 1e-6;
            case CUBIC_MILLI:
                return valueUnits * 1e-9;
            case SQUARE_MILLI:
                return valueUnits * 1e-6;
            case MILLI:
                return valueUnits * 1e-3;                
            default:
                throw new IllegalArgumentException(unsupportedUnitType(unitSuffix));
        }
    }

    private static UnitSuffix suffixFromMeterString(String suffixStr) { // NOSONAR

        // If no units
        if (suffixStr == null || suffixStr.isEmpty()) {
            return UnitSuffix.NONE;
        }

        // Metres
        if ("m".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.BASE;
        }
        
        // Milli-metres
        if ("mm^3".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.CUBIC_MILLI;

        } else if ("mm^2".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.SQUARE_MILLI;

        } else if ("mm".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.MILLI;
        }

        // Micro-metres
        if (STR_MICRO_METER_CUBED.equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.CUBIC_MICRO;
        } else if (STR_MICRO_METER_SQUARED.equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.SQUARE_MICRO;
        } else if (STR_MICRO_METER.equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.MICRO;
        }
        
        // Nano-metres
        if ("nm^3".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.CUBIC_NANO;

        } else if ("nm^2".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.SQUARE_NANO;

        } else if ("nm".equalsIgnoreCase(suffixStr)) {
            return UnitSuffix.NANO;
        }

        throw new IllegalArgumentException(unsupportedString(suffixStr));
    }

    private static String unsupportedString(String suffixStr) {
        return String.format("Suffix string: '%s' not supported", suffixStr);
    }

    private static String unsupportedUnitType(UnitSuffix unitType) {
        return String.format("Unit type: '%s' not supported", unitType);
    }
}
