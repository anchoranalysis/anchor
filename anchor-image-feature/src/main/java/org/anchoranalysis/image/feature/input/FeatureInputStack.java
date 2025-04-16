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
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;
import org.anchoranalysis.feature.input.FeatureInputEnergy;

/**
 * A feature input that represents an energy stack.
 *
 * <p>This class extends FeatureInputEnergy to provide specific functionality for working with
 * energy stacks in feature calculations.
 */
@EqualsAndHashCode(callSuper = true)
public class FeatureInputStack extends FeatureInputEnergy {

    /**
     * Constructs a FeatureInputStack with an empty energy stack.
     *
     * <p>Should only be used if it's guaranteed the energy stack will be added later, as this is
     * required for proper functionality.
     */
    public FeatureInputStack() {
        this.setEnergyStack(Optional.empty());
    }

    /**
     * Constructs a FeatureInputStack with the given energy stack.
     *
     * @param energyStack The energy stack to be used in this feature input.
     */
    public FeatureInputStack(EnergyStackWithoutParameters energyStack) {
        this.setEnergyStack(new EnergyStack(energyStack));
    }

    /**
     * Constructs a FeatureInputStack with an optional energy stack.
     *
     * @param energyStack An optional energy stack. If present, it will be used to initialize this
     *     feature input. If not present, the energy stack will be empty.
     */
    public FeatureInputStack(Optional<EnergyStackWithoutParameters> energyStack) {
        if (energyStack.isPresent()) {
            this.setEnergyStack(new EnergyStack(energyStack.get()));
        }
    }
}
