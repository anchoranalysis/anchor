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

package org.anchoranalysis.image.feature.input;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInputEnergy;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * An input representing a single object-mask (with maybe an energy-stack associated).
 *
 * <p>Equals and hash-code must be sensibly defined as these inputs can be used as keys in a cache.
 * The equals implementation assumes equals of {@link ObjectMask} is shallow and computationally
 * inexpensive.</p>
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class FeatureInputSingleObject extends FeatureInputEnergy {

    /** The object mask associated with this input. */
    @Getter private ObjectMask object;

    /**
     * Constructs a FeatureInputSingleObject with an ObjectMask.
     *
     * @param object the ObjectMask to be associated with this input
     */
    public FeatureInputSingleObject(ObjectMask object) {
        this(object, Optional.empty());
    }

    /**
     * Constructs a FeatureInputSingleObject with an ObjectMask and an EnergyStack.
     *
     * @param object the ObjectMask to be associated with this input
     * @param energyStack the EnergyStack to be associated with this input
     */
    public FeatureInputSingleObject(ObjectMask object, EnergyStack energyStack) {
        this(object, Optional.of(energyStack));
    }

    /**
     * Constructs a FeatureInputSingleObject with an ObjectMask and an optional EnergyStack.
     *
     * @param object the ObjectMask to be associated with this input
     * @param energyStack an optional EnergyStack to be associated with this input
     */
    public FeatureInputSingleObject(ObjectMask object, Optional<EnergyStack> energyStack) {
        super(energyStack);
        this.object = object;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the associated ObjectMask
     */
    @Override
    public String toString() {
        return object.toString();
    }
}