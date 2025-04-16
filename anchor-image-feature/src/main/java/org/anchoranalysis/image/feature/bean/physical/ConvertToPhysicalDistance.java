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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInputWithResolution;
import org.anchoranalysis.image.bean.spatial.direction.DirectionVectorBean;
import org.anchoranalysis.image.bean.spatial.direction.SpecifyVector;
import org.anchoranalysis.image.core.dimensions.SpatialUnits.UnitSuffix;
import org.anchoranalysis.image.core.dimensions.UnitConverter;
import org.anchoranalysis.spatial.orientation.DirectionVector;

/**
 * Converts a distance feature to physical units along a specified direction.
 *
 * <p>This feature converter takes a distance measurement and converts it to physical units
 * considering the direction specified by the {@code direction} property.</p>
 *
 * @param <T> the type of feature input, which must include resolution information
 */
@NoArgsConstructor
public class ConvertToPhysicalDistance<T extends FeatureInputWithResolution>
        extends FeatureConvertUnits<T> {

    // START BEAN PROPERTIES
    /** 
     * Direction of the distance being converted, defaults to a unit vector along the X-axis.
     */
    @BeanField @Getter @Setter private DirectionVectorBean direction = new SpecifyVector(1.0, 0, 0);
    // END BEAN PROPERTIES

    private DirectionVector vectorInDirection;

    /**
     * Creates a new instance with specified feature, unit type, and direction vector.
     *
     * @param feature the feature to convert
     * @param unitType the type of physical unit to convert to
     * @param directionVector the direction vector for the distance
     */
    public ConvertToPhysicalDistance(
            Feature<T> feature, UnitSuffix unitType, DirectionVector directionVector) {
        super(feature, unitType);
        this.direction = new SpecifyVector(directionVector);
    }

    /**
     * Initializes the direction vector before calculation.
     *
     * @param initialization the feature initialization context
     * @throws InitializeException if the direction vector cannot be created
     */
    @Override
    protected void beforeCalc(FeatureInitialization initialization) throws InitializeException {
        super.beforeCalc(initialization);
        try {
            vectorInDirection = direction.createVector();
        } catch (CreateException e) {
            throw new InitializeException(e);
        }
    }

    /**
     * Converts the input value to physical distance along the specified direction.
     *
     * @param value the input distance value
     * @param unitConverter the unit converter to use for the conversion
     * @return the converted physical distance
     * @throws FeatureCalculationException if the conversion fails
     */
    @Override
    protected double convertToPhysical(double value, UnitConverter unitConverter)
            throws FeatureCalculationException {
        // We use arbitrary direction as everything should be the same in a isometric XY plane
        return unitConverter.toPhysicalDistance(value, vectorInDirection);
    }
}