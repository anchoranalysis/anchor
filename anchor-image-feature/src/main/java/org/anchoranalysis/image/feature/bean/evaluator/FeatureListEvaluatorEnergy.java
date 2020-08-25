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
import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.provider.Provider;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputEnergy;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMultiChangeInput;
import org.anchoranalysis.image.feature.evaluator.NamedFeatureCalculatorMulti;
import org.anchoranalysis.image.stack.Stack;

/**
 * @author Owen Feehan
 * @param <T> feature-calculation parameters
 */
public class FeatureListEvaluatorEnergy<T extends FeatureInput> extends FeatureListEvaluator<T> {

    // START BEAN PROPERTIES
    @BeanField @OptionalBean @Getter @Setter private Provider<Stack> stackEnergy;

    @BeanField @OptionalBean @Getter @Setter private KeyValueParamsProvider params;
    // END BEAN PROPERTIES

    @Override
    public NamedFeatureCalculatorMulti<T> createAndStartSession(
            UnaryOperator<FeatureList<T>> addFeatures, SharedObjects sharedObjects)
            throws OperationFailedException {

        NamedFeatureCalculatorMulti<T> namedCalculator =
                super.createAndStartSession(addFeatures, sharedObjects);

        Optional<EnergyStack> energyStack = EnergyStackHelper.energyStack(stackEnergy, params);
        return namedCalculator.mapCalculator(
                calculator ->
                        new FeatureCalculatorMultiChangeInput<>(
                                calculator,
                                params -> {
                                    // Use reflection, to only set the energyStack on params that
                                    // supports them
                                    if (params instanceof FeatureInputEnergy
                                            && energyStack.isPresent()) {
                                        ((FeatureInputEnergy) params)
                                                .setEnergyStack(energyStack.get());
                                    }
                                }));
    }
}
