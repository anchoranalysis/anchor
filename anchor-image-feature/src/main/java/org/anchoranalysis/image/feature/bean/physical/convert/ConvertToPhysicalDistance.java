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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInputWithResolution;
import org.anchoranalysis.image.bean.spatial.direction.DirectionVectorBean;
import org.anchoranalysis.image.bean.spatial.direction.VectorInDirection;
import org.anchoranalysis.image.core.dimensions.SpatialUnits.UnitSuffix;
import org.anchoranalysis.image.core.dimensions.UnitConverter;
import org.anchoranalysis.image.core.orientation.DirectionVector;

//
@NoArgsConstructor
public class ConvertToPhysicalDistance<T extends FeatureInputWithResolution>
        extends FeatureConvertUnits<T> {

    // START BEAN PROPERTIES

    /** Direction of the distance being converted, defaults to a unit vector along the X-axis */
    @BeanField @Getter @Setter
    private DirectionVectorBean direction = new VectorInDirection(1.0, 0, 0);
    // END BEAN PROPERTIES

    private DirectionVector vectorInDirection;

    public ConvertToPhysicalDistance(
            Feature<T> feature, UnitSuffix unitType, DirectionVector directionVector) {
        super(feature, unitType);
        this.direction = new VectorInDirection(directionVector);
    }

    @Override
    protected void beforeCalc(FeatureInitParams paramsInit) throws InitException {
        super.beforeCalc(paramsInit);
        try {
            vectorInDirection = direction.createVector();
        } catch (CreateException e) {
            throw new InitException(e);
        }
    }

    @Override
    protected double convertToPhysical(double value, UnitConverter unitConverter)
            throws FeatureCalculationException {
        // We use arbitrary direction as everything should be the same in a isometric XY plane
        return unitConverter.toPhysicalDistance(value, vectorInDirection);
    }
}
