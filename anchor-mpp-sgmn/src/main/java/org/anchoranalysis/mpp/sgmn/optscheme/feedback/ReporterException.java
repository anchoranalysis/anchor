/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

public class ReporterException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = -943590820981212617L;

    public ReporterException(String string) {
        super(string);
    }

    public ReporterException(Throwable exc) {
        super(exc);
    }
}
