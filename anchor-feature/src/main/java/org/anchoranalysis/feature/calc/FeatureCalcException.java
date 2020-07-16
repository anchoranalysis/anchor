/* (C)2020 */
package org.anchoranalysis.feature.calc;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class FeatureCalcException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -907417952940489366L;

    public FeatureCalcException(String string) {
        super(string);
    }

    public FeatureCalcException(Throwable exc) {
        super(exc);
    }

    public FeatureCalcException(String string, Throwable exc) {
        super(string, exc);
    }
}
