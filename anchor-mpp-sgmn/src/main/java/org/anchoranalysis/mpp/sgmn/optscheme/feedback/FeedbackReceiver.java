/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback;

import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public interface FeedbackReceiver<T> {

    void reportBegin(OptimizationFeedbackInitParams<T> optInit) throws ReporterException;

    void reportItr(Reporting<T> reporting) throws ReporterException;

    void reportNewBest(Reporting<T> reporting) throws ReporterException;

    void reportEnd(OptimizationFeedbackEndParams<T> optStep) throws ReporterException;

    // ! Checks that a mark's initial parameters are correct
    void checkMisconfigured(BeanInstanceMap defaultInstances) throws BeanMisconfiguredException;
}
