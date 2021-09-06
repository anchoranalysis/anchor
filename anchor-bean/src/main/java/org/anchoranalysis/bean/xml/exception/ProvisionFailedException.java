package org.anchoranalysis.bean.xml.exception;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;

/**
 * An {@link Exception} thrown when the operation of retrieving an object fails.
 *
 * @author Owen Feehan
 */
public class ProvisionFailedException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public ProvisionFailedException(String message) {
        super(message);
    }

    public ProvisionFailedException(Throwable cause) {
        super(cause);
    }

    public ProvisionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
