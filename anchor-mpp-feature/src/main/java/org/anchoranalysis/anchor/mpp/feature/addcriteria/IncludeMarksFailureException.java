/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class IncludeMarksFailureException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -4475636830799035389L;

    public IncludeMarksFailureException(String string) {
        super(string);
    }

    public IncludeMarksFailureException(Exception exc) {
        super(exc);
    }
}
