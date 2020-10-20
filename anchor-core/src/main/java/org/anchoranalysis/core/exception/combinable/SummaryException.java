package org.anchoranalysis.core.exception.combinable;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;

/**
 * A special kind of exception that represents a summary of an existing set of exceptions
 *
 * @author Owen Feehan
 */
public class SummaryException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public SummaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
