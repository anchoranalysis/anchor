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

import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.input.FeatureInputWithResolution;
import org.anchoranalysis.image.core.dimensions.UnitConverter;

/**
 * Converts a feature value representing area in pixel units to physical area units in an isometric XY plane.
 *
 * <p>This feature converter takes a measurement in pixel units and converts it
 * to physical area units (e.g., square micrometers). It assumes an isometric XY plane
 * in the image space, meaning that the X and Y dimensions have the same physical scale.</p>
 *
 * @param <T> the type of feature input, which must include resolution information
 */
public class ConvertToPhysicalAreaXY<T extends FeatureInputWithResolution>
        extends FeatureConvertUnits<T> {

    /**
     * Converts the input value from pixel units to physical area units in an isometric XY plane.
     *
     * @param value the input area value in pixel units
     * @param unitConverter the unit converter to use for the conversion
     * @return the converted area in physical units
     * @throws FeatureCalculationException if the conversion fails
     */
    @Override
    protected double convertToPhysical(double value, UnitConverter unitConverter)
            throws FeatureCalculationException {
        // We use arbitrary direction as everything should be the same in an isometric XY plane
        return unitConverter.toPhysicalArea(value);
    }
}