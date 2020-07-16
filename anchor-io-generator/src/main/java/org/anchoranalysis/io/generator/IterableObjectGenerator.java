/* (C)2020 */
package org.anchoranalysis.io.generator;

import org.anchoranalysis.core.index.SetOperationFailedException;

/**
 * @author Owen Feehan
 * @param <T> iteration-type
 * @param <S> generated-type
 */
public interface IterableObjectGenerator<T, S> extends IterableGenerator<T> {

    @Override
    T getIterableElement();

    @Override
    void setIterableElement(T element) throws SetOperationFailedException;

    @Override
    ObjectGenerator<S> getGenerator();
}
