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

package org.anchoranalysis.image.feature.calculator;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.initialization.FeatureInitialization;

/**
 * A factory for creating FeatureInitialization objects.
 *
 * <p>This class provides a static method to create FeatureInitialization instances from optional
 * SharedObjects and EnergyStack inputs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InitializationFactory {

    /**
     * Creates a FeatureInitialization object from optional SharedObjects and EnergyStack.
     *
     * <p>This method extracts the necessary components from the inputs to construct a
     * FeatureInitialization object.
     *
     * @param sharedObjects an optional SharedObjects instance
     * @param energyStack an optional EnergyStack instance
     * @return a new FeatureInitialization object
     */
    public static FeatureInitialization create(
            Optional<SharedObjects> sharedObjects, Optional<EnergyStack> energyStack) {

        Optional<Dictionary> dictionary = energyStack.map(EnergyStack::getParameters);

        return new FeatureInitialization(
                dictionary, energyStack.map(EnergyStack::withoutParameters), sharedObjects);
    }
}
