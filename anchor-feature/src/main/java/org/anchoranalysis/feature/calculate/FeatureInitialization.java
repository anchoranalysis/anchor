/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.calculate;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.bean.initializable.parameters.BeanInitialization;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParameters;

/**
 * Parameters used to initialize a {@link Feature} before any calculation occurs.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class FeatureInitialization implements BeanInitialization {

    /** A dictionary of key-value pairs. */
    private final Optional<Dictionary> dictionary;

    /** An energy-stack, which may form an input to the feature for calculation. */
    private final Optional<EnergyStackWithoutParameters> energyStack;

    /** Shared-objects, which can be referenced by the feature to influence calculation. */
    private final Optional<SharedObjects> sharedObjects;

    /** Create without any dictionary, energy-stack or shared-objects. */
    public FeatureInitialization() {
        this.dictionary = Optional.empty();
        this.energyStack = Optional.empty();
        this.sharedObjects = Optional.empty();
    }

    /**
     * Create only with shared-objects.
     *
     * @param sharedObjects the shared objects.
     */
    public FeatureInitialization(SharedObjects sharedObjects) {
        this.dictionary = Optional.empty();
        this.energyStack = Optional.empty();
        this.sharedObjects = Optional.of(sharedObjects);
    }

    /**
     * Create only with a dictionary.
     *
     * @param dictionary the dictionary.
     */
    public FeatureInitialization(Dictionary dictionary) {
        this.dictionary = Optional.of(dictionary);
        this.energyStack = Optional.empty();
        this.sharedObjects = Optional.empty();
    }

    /**
     * Create only with an energy-stack.
     *
     * @param energyStack the energy-stack.
     */
    public FeatureInitialization(EnergyStack energyStack) {
        this.energyStack = Optional.of(energyStack.withoutParameters());
        this.dictionary = Optional.of(energyStack.getParameters());
        this.sharedObjects = Optional.empty();
    }

    /**
     * A shallow-copy of the current initialization.
     *
     * @return a new {@link FeatureInitialization} which reuses the existing state.
     */
    public FeatureInitialization duplicateShallow() {
        return new FeatureInitialization(dictionary, energyStack, sharedObjects);
    }

    /**
     * Retrieves the shared-objects associated with the initialization, or throws an an exception if
     * they do not exist.
     *
     * @return the shared-objects.
     * @throws InitializeException if no shared-objects exist.
     */
    public SharedObjects sharedObjectsRequired() throws InitializeException {
        return sharedObjects.orElseThrow(
                () ->
                        new InitializeException(
                                "Shared-objects are required for this bean, but are not available"));
    }
}
