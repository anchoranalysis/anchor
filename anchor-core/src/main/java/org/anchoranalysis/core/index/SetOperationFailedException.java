package org.anchoranalysis.core.index;

import org.anchoranalysis.core.exception.AnchorCheckedException;

public class SetOperationFailedException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = -3323720426335397452L;

    public SetOperationFailedException(String string) {
        super(string);
    }

    public SetOperationFailedException(Throwable exc) {
        super(exc);
    }
}
