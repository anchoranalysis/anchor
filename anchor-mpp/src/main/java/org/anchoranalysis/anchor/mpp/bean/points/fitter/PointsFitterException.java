/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.points.fitter;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class PointsFitterException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 6066348459108781534L;

    public PointsFitterException(String string) {
        super(string);
    }

    public PointsFitterException(Exception exc) {
        super(exc);
    }
}
