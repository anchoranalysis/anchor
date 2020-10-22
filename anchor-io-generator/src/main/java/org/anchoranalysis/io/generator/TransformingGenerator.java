package org.anchoranalysis.io.generator;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Performs preprocessing to transform the element into another type before being written to the filesystem.
 * @author Owen Feehan
 *
 * @param <T> iteration-type
 * @param <S> type after any necessary preprocessing
 */
public interface TransformingGenerator<T,S> extends Generator<T> {
    
    /**
     * Applies any necessary preprocessing to create an element suitable for writing to the
     * filesystem.
     *
     * @param element element to be assigned and then transformed
     * @return the transformed element after necessary preprocessing.
     * @throws OutputWriteFailedException if anything goes wrong
     */
    S transform(T element) throws OutputWriteFailedException;
}
