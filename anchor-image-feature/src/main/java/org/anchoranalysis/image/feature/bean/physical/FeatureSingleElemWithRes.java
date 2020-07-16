/* (C)2020 */
package org.anchoranalysis.image.feature.bean.physical;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.operator.FeatureGenericSingleElem;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInputWithRes;
import org.anchoranalysis.image.extent.ImageResolution;

public abstract class FeatureSingleElemWithRes<T extends FeatureInputWithRes>
        extends FeatureGenericSingleElem<T> {

    public FeatureSingleElemWithRes() {}

    public FeatureSingleElemWithRes(Feature<T> feature) {
        super(feature);
    }

    @Override
    public final double calc(SessionInput<T> input) throws FeatureCalcException {

        double value = input.calc(getItem());

        return calcWithRes(value, input.get().getResRequired());
    }

    protected abstract double calcWithRes(double value, ImageResolution res)
            throws FeatureCalcException;
}
