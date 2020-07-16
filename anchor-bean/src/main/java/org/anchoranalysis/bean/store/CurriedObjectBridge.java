/* (C)2020 */
package org.anchoranalysis.bean.store;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.functional.function.FunctionWithException;

/**
 * @author Owen Feehan
 * @param <S> source-type
 * @param <D> destination-type
 * @param <E> exception-type if something goes wrong
 */
@AllArgsConstructor
class CurriedObjectBridge<S, D, E extends Exception> implements Operation<D, E> {

    private FunctionWithException<S, D, E> bridge;
    private S sourceObject;

    @Override
    public D doOperation() throws E {
        return bridge.apply(sourceObject);
    }
}
