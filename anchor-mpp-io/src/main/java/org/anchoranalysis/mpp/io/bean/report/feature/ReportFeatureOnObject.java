/* (C)2020 */
package org.anchoranalysis.mpp.io.bean.report.feature;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;

public class ReportFeatureOnObject extends ReportFeatureOnObjectsBase<FeatureInputSingleObject> {

    @Override
    protected double calcFeatureOn(
            ObjectCollection objects, FeatureCalculatorSingle<FeatureInputSingleObject> session)
            throws FeatureCalcException {
        return session.calc(new FeatureInputSingleObject(extractObjFromCollection(objects)));
    }

    private ObjectMask extractObjFromCollection(ObjectCollection objects)
            throws FeatureCalcException {
        if (objects.size() == 0) {
            throw new FeatureCalcException("No object found");
        }
        if (objects.size() > 1) {
            throw new FeatureCalcException("More than one object found");
        }
        return objects.get(0);
    }
}
