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

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.store.SharedObjects;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.bean.provider.FeatureProvider;
import org.anchoranalysis.feature.calculate.bound.FeatureCalculatorMulti;
import org.anchoranalysis.feature.initialization.FeatureInitialization;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.image.feature.calculator.NamedFeatureCalculatorMulti;

/**
 * Defines a list of features and provides a means to calculate inputs for it, a session.
 *
 * @param <T> feature input-type
 * @author Owen Feehan
 */
public class FeatureListEvaluator<T extends FeatureInput>
        extends FeatureRelatedBean<FeatureListEvaluator<T>> {

    /**
     * A list of providers, combined together to form a list of features, to be calculated in a
     * session.
     */
    @BeanField @Getter @Setter private List<FeatureProvider<T>> features = Arrays.asList();

    /**
     * Creates session for evaluating {@code features}.
     *
     * @param addFeatures a function to potentially add additional features to the list
     * @param sharedObjects shared objects to be used in feature initialization
     * @return the calculator for a newly created session
     * @throws OperationFailedException if the session creation fails
     */
    public NamedFeatureCalculatorMulti<T> createFeatureSession(
            UnaryOperator<FeatureList<T>> addFeatures, SharedObjects sharedObjects)
            throws OperationFailedException {

        try {
            FeatureList<T> featuresCreated =
                    addFeatures.apply(FeatureListFactory.fromProviders(features));

            if (featuresCreated.size() == 0) {
                throw new OperationFailedException("No features are set");
            }

            FeatureCalculatorMulti<T> calculator =
                    FeatureSession.with(
                            featuresCreated,
                            new FeatureInitialization(sharedObjects),
                            getInitialization().getSharedFeatures(),
                            getLogger());

            return new NamedFeatureCalculatorMulti<>(calculator, featuresCreated.deriveNames());

        } catch (ProvisionFailedException | InitializeException e) {
            throw new OperationFailedException(e);
        }
    }
}
