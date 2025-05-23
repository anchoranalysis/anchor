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
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Calculates a single-input from a pair of objects.
 *
 * <p>Three extraction modes are possible:
 *
 * <ul>
 *   <li>First object
 *   <li>Second object
 *   <li>Merged objects
 * </ul>
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CalculateInputFromPair
        extends CalculationPart<FeatureInputSingleObject, FeatureInputPairObjects> {

    /** Defines the extraction mode for the calculation. */
    public enum Extract {
        /** Extract the first object from the pair. */
        FIRST,
        /** Extract the second object from the pair. */
        SECOND,
        /** Extract the merged object from the pair. */
        MERGED
    }

    /** The extraction mode to use. */
    private final Extract extract;

    /**
     * Executes the calculation to extract a single object from the pair.
     *
     * @param input The input pair of objects.
     * @return A new FeatureInputSingleObject containing the extracted object and energy stack.
     */
    @Override
    protected FeatureInputSingleObject execute(FeatureInputPairObjects input) {
        FeatureInputSingleObject inputNew = new FeatureInputSingleObject(extractObject(input));
        inputNew.setEnergyStack(input.getEnergyStackOptional());
        return inputNew;
    }

    /**
     * Extracts the appropriate object from the input pair based on the extraction mode.
     *
     * @param input The input pair of objects.
     * @return The extracted ObjectMask.
     */
    private ObjectMask extractObject(FeatureInputPairObjects input) {
        if (extract == Extract.MERGED) {
            return input.getMerged();
        }
        return extract == Extract.FIRST ? input.getFirst() : input.getSecond();
    }
}
