package org.anchoranalysis.core.serialize;

import org.anchoranalysis.core.exception.AnchorCheckedException;

public class DeserializationFailedException extends AnchorCheckedException {

    /** */
    private static final long serialVersionUID = 7824941062757840315L;

    public DeserializationFailedException(String string) {
        super(string);
    }

    public DeserializationFailedException(Exception exc) {
        super(exc);
    }

    public DeserializationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
