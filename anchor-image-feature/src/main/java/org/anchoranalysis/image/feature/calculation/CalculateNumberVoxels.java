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

package org.anchoranalysis.image.feature.calculation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.feature.calculate.part.CalculationPart;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Calculates the number of voxels in a single object, optionally flattening in the Z-dimension.
 *
 * <p>This calculation part can either count the voxels in the 3D object or in its 2D projection
 * (flattened in the Z-dimension), depending on the {@code flattenZ} parameter.
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CalculateNumberVoxels extends CalculationPart<Double, FeatureInputSingleObject> {

    /** Whether to flatten the object in the Z-dimension before counting voxels. */
    private final boolean flattenZ;

    /**
     * Executes the calculation to count the number of voxels in the object.
     *
     * @param input The input single object.
     * @return The number of voxels in the object (or its 2D projection if {@code flattenZ} is
     *     true).
     */
    @Override
    public Double execute(FeatureInputSingleObject input) {
        return (double) maybeFlatten(input.getObject()).numberVoxelsOn();
    }

    /**
     * Flattens the object in the Z-dimension if {@code flattenZ} is true.
     *
     * @param object The input object mask.
     * @return The flattened object if {@code flattenZ} is true, otherwise the original object.
     */
    private ObjectMask maybeFlatten(ObjectMask object) {
        if (flattenZ) {
            return object.flattenZ();
        } else {
            return object;
        }
    }
}
