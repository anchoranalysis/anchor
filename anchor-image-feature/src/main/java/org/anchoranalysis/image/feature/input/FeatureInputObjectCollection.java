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
import org.anchoranalysis.image.voxel.object.ObjectCollection;

/**
 * A feature input representing a collection of objects, optionally associated with an energy stack.
 *
 * <p>This class extends FeatureInputEnergy to provide functionality specific to object collections.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class FeatureInputObjectCollection extends FeatureInputEnergy {

    /** The collection of objects associated with this feature input. */
    @Getter private final ObjectCollection objects;

    /**
     * Constructs a FeatureInputObjectCollection with a collection of objects.
     *
     * @param objects the collection of objects to be associated with this input
     */
    public FeatureInputObjectCollection(ObjectCollection objects) {
        this.objects = objects;
    }

    /**
     * Constructs a FeatureInputObjectCollection with a collection of objects and an optional energy
     * stack.
     *
     * @param objects the collection of objects to be associated with this input
     * @param energyStack an optional energy stack to be associated with this input
     */
    public FeatureInputObjectCollection(
            ObjectCollection objects, Optional<EnergyStack> energyStack) {
        super(energyStack);
        this.objects = objects;
    }
}
