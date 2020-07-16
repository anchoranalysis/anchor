/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.period;

import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public interface PeriodReceiver<T> {

    void periodStart(Reporting<T> reporting) throws PeriodReceiverException;

    default void periodEnd(Reporting<T> reporting) throws PeriodReceiverException {}
}
