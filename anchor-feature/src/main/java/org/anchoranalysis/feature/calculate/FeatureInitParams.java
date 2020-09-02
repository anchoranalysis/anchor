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
import org.anchoranalysis.bean.initializable.params.BeanInitParams;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParams;

/**
 * Parameters used to initialize a feature before any calculations
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class FeatureInitParams implements BeanInitParams {

    private final Optional<KeyValueParams> keyValueParams;

    private final Optional<EnergyStackWithoutParams> energyStack;

    private final Optional<SharedObjects> sharedObjects;

    public FeatureInitParams() {
        this.keyValueParams = Optional.empty();
        this.energyStack = Optional.empty();
        this.sharedObjects = Optional.empty();
    }

    public FeatureInitParams(SharedObjects sharedObjects) {
        this.keyValueParams = Optional.empty();
        this.energyStack = Optional.empty();
        this.sharedObjects = Optional.of(sharedObjects);
    }

    public FeatureInitParams(KeyValueParams keyValueParams) {
        this.keyValueParams = Optional.of(keyValueParams);
        this.energyStack = Optional.empty();
        this.sharedObjects = Optional.empty();
    }

    public FeatureInitParams(EnergyStack energyStack) {
        this.energyStack = Optional.of(energyStack.withoutParams());
        this.keyValueParams = Optional.of(energyStack.getParams());
        this.sharedObjects = Optional.empty();
    }

    // Shallow-copy
    public FeatureInitParams duplicate() {
        return new FeatureInitParams(keyValueParams, energyStack, sharedObjects);
    }

    public SharedObjects sharedObjectsRequired() throws InitException {
        return sharedObjects.orElseThrow(
                () ->
                        new InitException(
                                "Shared-objects are required for this bean, but are not available"));
    }
}
