/* (C)2020 */
package org.anchoranalysis.image.feature.bean.evaluator;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;
import org.anchoranalysis.feature.bean.provider.FeatureProvider;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;

public abstract class FeatureEvaluator<T extends FeatureInput>
        extends FeatureRelatedBean<FeatureEvaluator<T>> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private FeatureProvider<T> featureProvider;
    // END BEAN PROPERTIES

    public FeatureCalculatorSingle<T> createAndStartSession() throws OperationFailedException {

        try {
            Feature<T> feature = featureProvider.create();

            if (feature == null) {
                throw new OperationFailedException(
                        "FeatureProvider returns null. A feature is required.");
            }

            return FeatureSession.with(
                    feature, getInitializationParameters().getSharedFeatureSet(), getLogger());

        } catch (CreateException | FeatureCalcException e) {
            throw new OperationFailedException(e);
        }
    }
}
