package org.anchoranalysis.core.cache;

import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.function.CheckedSupplier;

/**
 * Base class for functions that memoize (cache) a call to an interface
 *
 * @author Owen Feehan
 * @param <T> result-type
 */
@NoArgsConstructor
public abstract class CachedSupplierBase<T> {

    private T result;
    private boolean evaluated = false;

    public synchronized void assignFrom(CachedSupplierBase<T> source) {
        this.result = source.result;
        this.evaluated = source.evaluated;
    }

    public synchronized void reset() {
        evaluated = false;
        result = null;
    }

    public synchronized boolean isEvaluated() {
        return evaluated;
    }

    protected synchronized <E extends Exception> T call(CheckedSupplier<T, E> supplier)
            throws E {

        if (!evaluated) {
            result = supplier.get();
            evaluated = true;
        }
        return result;
    }
}
