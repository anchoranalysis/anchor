package org.anchoranalysis.inference;

import org.anchoranalysis.core.exception.OperationFailedException;

/**
 * A model used for inference.
 *
 * @author Owen Feehan
 */
public interface InferenceModel extends AutoCloseable {

    /**
     * Indicates that the model will no longer be used, and does appropriate tidying up and freeing
     * of resources.
     */
    void close() throws OperationFailedException;
}
