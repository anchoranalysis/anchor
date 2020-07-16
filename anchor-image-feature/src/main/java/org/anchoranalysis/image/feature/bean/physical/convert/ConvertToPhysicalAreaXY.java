/* (C)2020 */
package org.anchoranalysis.image.feature.bean.physical.convert;

import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInputWithRes;
import org.anchoranalysis.image.convert.ImageUnitConverter;
import org.anchoranalysis.image.extent.ImageResolution;

// converts a feature to a physical distance in a XY place that is isometric
public class ConvertToPhysicalAreaXY<T extends FeatureInputWithRes> extends FeatureConvertRes<T> {

    @Override
    protected double convertToPhysical(double value, ImageResolution res)
            throws FeatureCalcException {
        // We use arbitrary direction as everything should be the same in a isometric XY plane
        return ImageUnitConverter.convertToPhysicalArea(value, res);
    }
}
