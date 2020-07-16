/* (C)2020 */
package org.anchoranalysis.feature.calc;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

/**
 * Parameters used to initialize a feature before any calculations
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class FeatureInitParams implements BeanInitParams {

    private final Optional<KeyValueParams> keyValueParams;

    private final Optional<NRGStack> nrgStack;

    private final Optional<SharedObjects> sharedObjects;

    public FeatureInitParams() {
        this.keyValueParams = Optional.empty();
        this.nrgStack = Optional.empty();
        this.sharedObjects = Optional.empty();
    }

    public FeatureInitParams(SharedObjects sharedObjects) {
        this.keyValueParams = Optional.empty();
        this.nrgStack = Optional.empty();
        this.sharedObjects = Optional.of(sharedObjects);
    }

    public FeatureInitParams(KeyValueParams keyValueParams) {
        this.keyValueParams = Optional.of(keyValueParams);
        this.nrgStack = Optional.empty();
        this.sharedObjects = Optional.empty();
    }

    public FeatureInitParams(NRGStackWithParams nrgStack) {
        this.nrgStack = Optional.of(nrgStack.getNrgStack());
        this.keyValueParams = Optional.of(nrgStack.getParams());
        this.sharedObjects = Optional.empty();
    }

    // Shallow-copy
    public FeatureInitParams duplicate() {
        return new FeatureInitParams(keyValueParams, nrgStack, sharedObjects);
    }

    public SharedObjects sharedObjectsRequired() throws InitException {
        return sharedObjects.orElseThrow(
                () ->
                        new InitException(
                                "Shared-objects are required for this bean, but are not available"));
    }
}
