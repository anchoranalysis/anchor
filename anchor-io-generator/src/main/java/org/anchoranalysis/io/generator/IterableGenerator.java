/* (C)2020 */
package org.anchoranalysis.io.generator;

import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * A generator that can be iterated over by changing elements
 *
 * @author Owen Feehan
 * @param <T> iteration-type
 */
public interface IterableGenerator<T> {

    T getIterableElement();

    void setIterableElement(T element) throws SetOperationFailedException;

    default void start() throws OutputWriteFailedException {
        // NOTHING TO DO
    }

    default void end() throws OutputWriteFailedException {
        // NOTHING TO DO
    }

    Generator getGenerator();
}
