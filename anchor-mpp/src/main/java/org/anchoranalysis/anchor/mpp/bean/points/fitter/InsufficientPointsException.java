/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.points.fitter;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class InsufficientPointsException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public InsufficientPointsException() {
        super("There were insufficient number of points for a good ellipse-fit");
    }

    public InsufficientPointsException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public InsufficientPointsException(String message) {
        super(message);
    }

    public InsufficientPointsException(Throwable cause) {
        super(cause);
    }
}
