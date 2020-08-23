package org.anchoranalysis.image.feature.bean.evaluator;

import java.util.Optional;
import org.anchoranalysis.bean.OptionalFactory;
import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.stack.Stack;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class EnergyStackHelper {

    public static Optional<EnergyStack> energyStack(Provider<Stack> stackEnergy, KeyValueParamsProvider keyValueParamsProvider) throws OperationFailedException {
        try {
            if (stackEnergy != null) {

                EnergyStack energyStack = new EnergyStack(stackEnergy.create());
                energyStack.setParams( OptionalFactory.create(keyValueParamsProvider).orElseGet(KeyValueParams::new));
                return Optional.of(energyStack);
            } else {
                return Optional.empty();
            }
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
