/* (C)2020 */
package org.anchoranalysis.mpp.io.bean.report.feature;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.bean.report.feature.ReportFeature;

public class ReportFeatureWrapError extends ReportFeatureForSharedObjects {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ReportFeature<MPPInitParams> item;

    @BeanField @Getter @Setter private String message;
    // END BEAN PROPERTIES

    @Override
    public boolean isNumeric() {
        return item.isNumeric();
    }

    @Override
    public String genTitleStr() throws OperationFailedException {
        return item.genTitleStr();
    }

    @Override
    public String genFeatureStringFor(MPPInitParams obj, Logger logger)
            throws OperationFailedException {
        try {
            return item.genFeatureStringFor(obj, logger);
        } catch (OperationFailedException e) {
            return message;
        }
    }
}
