/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bound;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class BoundCalculatorException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public BoundCalculatorException(String s) {
        super(s);
    }

    public BoundCalculatorException(Exception e) {
        super(e);
    }
}
