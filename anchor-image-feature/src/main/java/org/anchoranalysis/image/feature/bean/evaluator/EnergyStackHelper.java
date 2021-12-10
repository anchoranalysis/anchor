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
package org.anchoranalysis.image.feature.bean.evaluator;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.OptionalProviderFactory;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.shared.dictionary.DictionaryProvider;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInputEnergy;
import org.anchoranalysis.image.core.stack.Stack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class EnergyStackHelper {

    public static Optional<EnergyStack> energyStack(
            Provider<Stack> stackEnergy, DictionaryProvider dictionary)
            throws OperationFailedException {
        try {
            if (stackEnergy != null) {

                EnergyStack energyStack = new EnergyStack(stackEnergy.get());
                energyStack.setParameters(
                        OptionalProviderFactory.create(dictionary).orElseGet(Dictionary::new));
                return Optional.of(energyStack);
            } else {
                return Optional.empty();
            }
        } catch (ProvisionFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    public static <T> void maybeSetEnergyStackOnInput(T input, Optional<EnergyStack> energyStack) {
        // Use reflection, to only set the energyStack on inputs that support them.
        if (input instanceof FeatureInputEnergy && energyStack.isPresent()) {
            ((FeatureInputEnergy) input).setEnergyStack(energyStack.get());
        }
    }
}
