/* (C)2020 */
package org.anchoranalysis.mpp.io.bean.report.feature;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.bean.provider.ObjectCollectionProvider;
import org.anchoranalysis.image.object.ObjectCollection;

public abstract class ReportFeatureOnObjectsBase<T extends FeatureInput>
        extends ReportFeatureEvaluator<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ObjectCollectionProvider objects;
    // END BEAN PROPERTIES

    @Override
    public String genFeatureStringFor(MPPInitParams so, Logger logger)
            throws OperationFailedException {
        try {
            objects.initRecursive(so.getImage(), logger);
            super.init(so, logger);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        try {
            FeatureCalculatorSingle<T> session = super.createAndStartSession();
            return Double.toString(calcFeatureOn(objects.create(), session));

        } catch (FeatureCalcException | CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    protected abstract double calcFeatureOn(
            ObjectCollection objects, FeatureCalculatorSingle<T> session)
            throws FeatureCalcException;

    @Override
    public boolean isNumeric() {
        return true;
    }
}
