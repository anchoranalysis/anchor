/* (C)2020 */
package org.anchoranalysis.image.feature.bean.object.single;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.axis.AxisTypeConverter;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

@NoArgsConstructor
public class CenterOfGravity extends FeatureSingleObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String axis = "x";

    @BeanField @Getter @Setter private double emptyValue = 0;
    // END BEAN PROPERTIES

    /**
     * Constructor - create for a specific axis
     *
     * @param axis axis
     */
    public CenterOfGravity(AxisType axis) {
        this.axis = axis.toString().toLowerCase();
    }

    @Override
    public double calc(SessionInput<FeatureInputSingleObject> input) {

        FeatureInputSingleObject params = input.get();

        double val = params.getObject().centerOfGravity(axisType());

        if (Double.isNaN(val)) {
            return emptyValue;
        }

        return val;
    }

    private AxisType axisType() {
        return AxisTypeConverter.createFromString(axis);
    }
}
