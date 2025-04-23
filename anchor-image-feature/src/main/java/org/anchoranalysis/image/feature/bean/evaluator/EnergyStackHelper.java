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

/** Helper class for creating and managing {@link EnergyStack} objects. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class EnergyStackHelper {

    /**
     * Creates an {@link EnergyStack} from a stack provider and dictionary provider.
     *
     * @param stackEnergy the provider for the {@link Stack} to be used in the {@link EnergyStack}.
     * @param dictionary the provider for the {@link Dictionary} to be used in the {@link
     *     EnergyStack}.
     * @return an {@link Optional} containing the created {@link EnergyStack}, or empty if
     *     stackEnergy is null.
     * @throws OperationFailedException if there's an error during the creation process.
     */
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

    /**
     * Sets the {@link EnergyStack} on the input object if it's an instance of {@link
     * FeatureInputEnergy}.
     *
     * @param <T> the type of the input object.
     * @param input the input object to potentially set the {@link EnergyStack} on.
     * @param energyStack the {@link Optional} {@link EnergyStack} to set.
     */
    public static <T> void maybeSetEnergyStackOnInput(T input, Optional<EnergyStack> energyStack) {
        // Use reflection, to only set the energyStack on inputs that support them.
        if (input instanceof FeatureInputEnergy inputCast && energyStack.isPresent()) {
            inputCast.setEnergyStack(energyStack.get());
        }
    }
}
