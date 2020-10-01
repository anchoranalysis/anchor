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
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.bean.provider.Provider;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingleChangeInput;
import org.anchoranalysis.image.stack.Stack;

/**
 * Defines a feature and provides a means to calculate inputs for it, a session.
 *
 * <p>Optionally, an energy stack can be associated with these inputs.
 *
 * @author Owen Feehan
 * @param <T> feature input-type
 */
public class FeatureEvaluator<T extends FeatureInput>
        extends FeatureRelatedBean<FeatureEvaluator<T>> {

    // START BEAN PROPERTIES
    /** The single feature that will be calculated (possibly repeatedly) in the session */
    @BeanField @Getter @Setter @SkipInit private Feature<T> feature;

    /** Optionally specifies an energy-stack to be associated with every calculation input. */
    @BeanField @OptionalBean @Getter @Setter private Provider<Stack> stackEnergy;

    /**
     * Parameters to optionally associated with {@code stackEnergy}, and meaningless if {@code
     * stackEnergy} is not specified.
     */
    @BeanField @OptionalBean @Getter @Setter private KeyValueParamsProvider params;
    // END BEAN PROPERTIES

    /**
     * Creates session for evaluating {@code feature} optionally adding an energy-stack.
     *
     * @return the calculator for a newly created session.
     * @throws OperationFailedException
     */
    public FeatureCalculatorSingle<T> createFeatureSession() throws OperationFailedException {
        return maybeAddEnergyStack(createCalculator());
    }

    /**
     * The specified energy stack.
     *
     * @return the energy stack if it is specified.
     * @throws OperationFailedException if the energy-stack is specified but cannot be created.
     */
    public Optional<EnergyStack> energyStack() throws OperationFailedException {
        return EnergyStackHelper.energyStack(stackEnergy, params);
    }

    private FeatureCalculatorSingle<T> createCalculator() throws OperationFailedException {

        try {
            return FeatureSession.with(
                    feature, getInitializationParameters().getSharedFeatureSet(), getLogger());

        } catch (InitException e) {
            throw new OperationFailedException(e);
        }
    }

    private FeatureCalculatorSingle<T> maybeAddEnergyStack(FeatureCalculatorSingle<T> calculator)
            throws OperationFailedException {
        if (stackEnergy != null) {
            final Optional<EnergyStack> energyStack = energyStack();

            return new FeatureCalculatorSingleChangeInput<>(
                    calculator,
                    input -> EnergyStackHelper.maybeSetEnergyStackOnInput(input, energyStack));
        } else {
            return calculator;
        }
    }
}
