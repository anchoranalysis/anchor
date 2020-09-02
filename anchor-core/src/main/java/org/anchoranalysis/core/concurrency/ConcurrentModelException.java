package org.anchoranalysis.core.concurrency;

import org.anchoranalysis.core.error.AnchorCheckedException;

/**
 * This exception indicates that an error occurred when performing inference from a model concurrently.
 * 
 * @author Owen Feehan
 */
public class ConcurrentModelException extends AnchorCheckedException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ConcurrentModelException(Throwable cause) {
        super(cause);
    }
}
