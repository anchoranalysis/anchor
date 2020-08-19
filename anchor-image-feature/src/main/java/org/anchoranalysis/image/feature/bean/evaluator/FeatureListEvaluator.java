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

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.bean.provider.FeatureProvider;
import org.anchoranalysis.feature.calculate.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.feature.evaluator.NamedFeatureCalculatorMulti;

public abstract class FeatureListEvaluator<T extends FeatureInput>
        extends FeatureRelatedBean<FeatureListEvaluator<T>> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter
    private List<FeatureProvider<T>> listFeatureProvider = new ArrayList<>();
    // END BEAN PROPERTIES

    public NamedFeatureCalculatorMulti<T> createAndStartSession( UnaryOperator<FeatureList<T>> addFeatures, SharedObjects sharedObjects ) throws OperationFailedException {

        try {
            FeatureList<T> features =
                    addFeatures.apply( FeatureListFactory.fromProviders(listFeatureProvider) );
            
            if (features.size() == 0) {
                throw new OperationFailedException("No features are set");
            }
            
            FeatureInitParams paramsInit = new FeatureInitParams(sharedObjects);

            FeatureCalculatorMulti<T> calculator = FeatureSession.with(
                    features, paramsInit, getInitializationParameters().getSharedFeatureSet(), getLogger());
            
            return new NamedFeatureCalculatorMulti<>(calculator, features.createNames());

        } catch (CreateException | InitException e) {
            throw new OperationFailedException(e);
        }
    }
}
