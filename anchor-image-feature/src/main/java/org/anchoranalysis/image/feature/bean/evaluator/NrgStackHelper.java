package org.anchoranalysis.image.feature.bean.evaluator;

import java.util.Optional;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.stack.Stack;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class NrgStackHelper {

    public static Optional<NRGStackWithParams> nrgStack(Provider<Stack> stackNRG, KeyValueParamsProvider keyValueParamsProvider) throws OperationFailedException {
        try {
            if (stackNRG != null) {

                NRGStackWithParams nrgStack = new NRGStackWithParams(stackNRG.create());
                nrgStack.setParams(createKeyValueParams(keyValueParamsProvider));
                return Optional.of(nrgStack);
            } else {
                return Optional.empty();
            }
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    public static KeyValueParams createKeyValueParams(KeyValueParamsProvider keyValueParamsProvider) throws CreateException {
        if (keyValueParamsProvider != null) {
            return keyValueParamsProvider.create();
        } else {
            return new KeyValueParams();
        }
    }
}
