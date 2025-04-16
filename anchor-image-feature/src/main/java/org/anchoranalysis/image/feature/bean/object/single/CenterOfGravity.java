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

package org.anchoranalysis.image.feature.bean.object.single;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.calculate.FeatureCalculationException;
import org.anchoranalysis.feature.calculate.FeatureCalculationInput;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.axis.AxisConversionException;
import org.anchoranalysis.spatial.axis.AxisConverter;

/**
 * Calculates the center of gravity of a single object along a specified axis.
 *
 * <p>This feature computes the center of gravity of an object along the X, Y, or Z axis.
 */
@NoArgsConstructor
public class CenterOfGravity extends FeatureSingleObject {

    // START BEAN PROPERTIES
    /** The axis along which to calculate the center of gravity. Can be "x", "y", or "z". */
    @BeanField @Getter @Setter private String axis = "x";

    /** The value to return if the center of gravity calculation results in NaN. */
    @BeanField @Getter @Setter private double emptyValue = 0;
    // END BEAN PROPERTIES

    /**
     * Creates a CenterOfGravity feature for a specific axis.
     *
     * @param axis the axis along which to calculate the center of gravity
     */
    public CenterOfGravity(Axis axis) {
        this.axis = axis.toString().toLowerCase();
    }

    @Override
    public double calculate(FeatureCalculationInput<FeatureInputSingleObject> input)
            throws FeatureCalculationException {

        double val = input.get().getObject().centerOfGravity(axis());

        if (Double.isNaN(val)) {
            return emptyValue;
        }

        return val;
    }

    /**
     * Converts the axis string to an Axis object.
     *
     * @return the Axis object corresponding to the axis string
     * @throws FeatureCalculationException if the axis string cannot be converted to a valid Axis
     */
    private Axis axis() throws FeatureCalculationException {
        try {
            return AxisConverter.createFromString(axis);
        } catch (AxisConversionException e) {
            throw new FeatureCalculationException(e.friendlyMessageHierarchy());
        }
    }
}
