/*-
 * #%L
 * anchor-image-feature
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

package org.anchoranalysis.image.feature.bean.physical;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.input.FeatureInputWithResolution;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.dimensions.SpatialUnits;
import org.anchoranalysis.image.core.dimensions.SpatialUnits.UnitSuffix;
import org.anchoranalysis.image.core.dimensions.UnitConverter;

/**
 * An abstract base class for features that convert units between physical and voxel space.
 *
 * <p>This class provides a framework for converting feature values between different unit systems,
 * typically between voxel-based measurements and physical measurements (e.g., micrometers).
 *
 * @param <T> the type of feature input, which must include resolution information
 */
@NoArgsConstructor
public abstract class FeatureConvertUnits<T extends FeatureInputWithResolution>
        extends WithResolutionBase<T> {

    // START BEAN PROPERTIES
    /** The type of unit to convert to, represented as a string (e.g., "um" for micrometers). */
    @BeanField @Getter @Setter private String unitType;

    // END BEAN PROPERTIES

    /**
     * Constructs a FeatureConvertUnits with a specified feature and unit type.
     *
     * @param feature the feature to be converted
     * @param unitType the type of unit to convert to
     */
    protected FeatureConvertUnits(Feature<T> feature, UnitSuffix unitType) {
        super(feature);
        this.unitType = SpatialUnits.suffixStringForMeters(unitType);
    }

    /**
     * Calculates the feature value with resolution information.
     *
     * @param value the input value to be converted
     * @param resolution the resolution information for the conversion
     * @return the converted value
     * @throws FeatureCalculationException if an error occurs during the calculation
     */
    @Override
    protected double calculateWithResolution(double value, Resolution resolution)
            throws FeatureCalculationException {
        double valuePhysical = convertToPhysical(value, resolution.unitConvert());
        return convertToUnits(valuePhysical);
    }

    /**
     * Converts the input value to physical units.
     *
     * @param value the input value to be converted
     * @param unitConverter the unit converter to use for the conversion
     * @return the converted value in physical units
     * @throws FeatureCalculationException if an error occurs during the conversion
     */
    protected abstract double convertToPhysical(double value, UnitConverter unitConverter)
            throws FeatureCalculationException;

    /**
     * Converts a physical value to the specified unit type.
     *
     * @param valuePhysical the physical value to be converted
     * @return the converted value in the specified unit type
     */
    private double convertToUnits(double valuePhysical) {
        return SpatialUnits.convertToUnits(valuePhysical, unitType);
    }
}
