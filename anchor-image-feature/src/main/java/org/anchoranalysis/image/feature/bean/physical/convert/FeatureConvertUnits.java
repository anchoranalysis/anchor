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

package org.anchoranalysis.image.feature.bean.physical.convert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.input.FeatureInputWithResolution;
import org.anchoranalysis.image.dimensions.Resolution;
import org.anchoranalysis.image.dimensions.SpatialUnits;
import org.anchoranalysis.image.dimensions.UnitConverter;
import org.anchoranalysis.image.dimensions.SpatialUnits.UnitSuffix;
import org.anchoranalysis.image.feature.bean.physical.WithResolutionBase;

@NoArgsConstructor
public abstract class FeatureConvertUnits<T extends FeatureInputWithResolution>
        extends WithResolutionBase<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String unitType;
    // END BEAN PROPERTIES

    public FeatureConvertUnits(Feature<T> feature, UnitSuffix unitType) {
        super(feature);
        this.unitType = SpatialUnits.suffixStringForMeters(unitType);
    }

    @Override
    protected double calculateWithResolution(double value, Resolution resolution)
            throws FeatureCalculationException {
        double valuePhysical = convertToPhysical(value, resolution.unitConvert());
        return convertToUnits(valuePhysical);
    }

    protected abstract double convertToPhysical(double value, UnitConverter unitConverter)
            throws FeatureCalculationException;

    private double convertToUnits(double valuePhysical) {
        return SpatialUnits.convertToUnits(valuePhysical, unitType);
    }
}
