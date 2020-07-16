/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class AggregatorException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 896694665077742955L;

    public AggregatorException() {
        super();
    }

    public AggregatorException(String arg0) {
        super(arg0);
    }

    public AggregatorException(Exception e) {
        super(e);
    }
}
