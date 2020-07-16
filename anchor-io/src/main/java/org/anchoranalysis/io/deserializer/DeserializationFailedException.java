/* (C)2020 */
package org.anchoranalysis.io.deserializer;

import org.anchoranalysis.core.error.AnchorCheckedException;

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
