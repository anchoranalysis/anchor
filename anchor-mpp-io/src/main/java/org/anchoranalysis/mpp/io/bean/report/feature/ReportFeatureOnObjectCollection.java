/* (C)2020 */
package org.anchoranalysis.mpp.io.bean.report.feature;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.feature.object.input.FeatureInputObjectCollection;
import org.anchoranalysis.image.object.ObjectCollection;

public class ReportFeatureOnObjectCollection
        extends ReportFeatureOnObjectsBase<FeatureInputObjectCollection> {

    @Override
    protected double calcFeatureOn(
            ObjectCollection objects, FeatureCalculatorSingle<FeatureInputObjectCollection> session)
            throws FeatureCalcException {
        return session.calc(new FeatureInputObjectCollection(objects));
    }
}
