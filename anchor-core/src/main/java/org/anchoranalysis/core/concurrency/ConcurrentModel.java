package org.anchoranalysis.core.concurrency;

import lombok.Value;

/**
 * An instance of model that can be used concurrently for inference.
 *
 * @author Owen Feehan
 * @param <T> the type of model
 */
@Value
public class ConcurrentModel<T> {

    /** The underlying model. */
    private T model;

    /** Whether model is using the GPU or not. */
    private boolean gpu;
}
