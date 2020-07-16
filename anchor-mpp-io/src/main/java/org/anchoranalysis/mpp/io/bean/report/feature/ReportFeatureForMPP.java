/* (C)2020 */
package org.anchoranalysis.mpp.io.bean.report.feature;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.bean.provider.ImageDimProvider;
import org.anchoranalysis.image.extent.ImageDimensions;

public abstract class ReportFeatureForMPP<T extends FeatureInput>
        extends ReportFeatureEvaluator<T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ImageDimProvider dim;
    // END BEAN PROPERTIES

    protected ImageDimensions createImageDim() throws CreateException {
        return dim.create();
    }
}
