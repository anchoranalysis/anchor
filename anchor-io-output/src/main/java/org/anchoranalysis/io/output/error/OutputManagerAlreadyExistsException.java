/* (C)2020 */
package org.anchoranalysis.io.output.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

/**
 * When an OutputManager already exists. This is thrown as a RuntimeException to cause the
 * application to immediately end.
 *
 * @author Owen Feehan
 */
public class OutputManagerAlreadyExistsException extends AnchorFriendlyRuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    public OutputManagerAlreadyExistsException(String msg) {
        super(msg);
    }
}
