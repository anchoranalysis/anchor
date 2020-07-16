/* (C)2020 */
package org.anchoranalysis.io.bean.report.feature;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;

public abstract class ReportFeature<T> extends AnchorBean<ReportFeature<T>> {

    public abstract boolean isNumeric();

    public abstract String genTitleStr() throws OperationFailedException;

    public abstract String genFeatureStringFor(T obj, Logger logger)
            throws OperationFailedException;
}
