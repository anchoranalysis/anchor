/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.period;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class PeriodReceiverException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 8462423019988537940L;

    public PeriodReceiverException() {
        super();
    }

    public PeriodReceiverException(String arg0) {
        super(arg0);
    }

    public PeriodReceiverException(Exception e) {
        super(e);
    }
}
