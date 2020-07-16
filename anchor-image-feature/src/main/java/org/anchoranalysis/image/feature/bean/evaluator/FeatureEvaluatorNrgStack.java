/* (C)2020 */
package org.anchoranalysis.image.feature.bean.evaluator;

import java.util.Optional;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingleChangeInput;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;

/**
 * @author Owen Feehan
 * @param <T> feature-calculation parameters
 */
public class FeatureEvaluatorNrgStack<T extends FeatureInput> extends FeatureEvaluator<T> {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean private StackProvider stackProviderNRG;

    @BeanField @OptionalBean private KeyValueParamsProvider keyValueParamsProvider;
    // END BEAN PROPERTIES

    @Override
    public FeatureCalculatorSingle<T> createAndStartSession() throws OperationFailedException {

        FeatureCalculatorSingle<T> session = super.createAndStartSession();

        final Optional<NRGStackWithParams> nrgStack = nrgStack();

        return new FeatureCalculatorSingleChangeInput<>(
                session,
                params -> {
                    // Use reflection, to only set the nrgStack on params that supports them
                    if (params instanceof FeatureInputNRG && nrgStack.isPresent()) {
                        ((FeatureInputNRG) params).setNrgStack(nrgStack.get());
                    }
                });
    }

    public Optional<NRGStackWithParams> nrgStack() throws OperationFailedException {
        try {
            if (stackProviderNRG != null) {

                NRGStackWithParams nrgStack = new NRGStackWithParams(stackProviderNRG.create());
                nrgStack.setParams(createKeyValueParams());
                return Optional.of(nrgStack);
            } else {
                return Optional.empty();
            }
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    private KeyValueParams createKeyValueParams() throws OperationFailedException {
        if (keyValueParamsProvider != null) {
            try {
                return keyValueParamsProvider.create();
            } catch (CreateException e) {
                throw new OperationFailedException(e);
            }
        } else {
            return new KeyValueParams();
        }
    }

    public StackProvider getStackProviderNRG() {
        return stackProviderNRG;
    }

    public void setStackProviderNRG(StackProvider stackProviderNRG) {
        this.stackProviderNRG = stackProviderNRG;
    }

    public KeyValueParamsProvider getKeyValueParamsProvider() {
        return keyValueParamsProvider;
    }

    public void setKeyValueParamsProvider(KeyValueParamsProvider keyValueParamsProvider) {
        this.keyValueParamsProvider = keyValueParamsProvider;
    }
}
