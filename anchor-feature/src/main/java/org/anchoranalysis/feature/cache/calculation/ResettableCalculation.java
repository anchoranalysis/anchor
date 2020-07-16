/* (C)2020 */
package org.anchoranalysis.feature.cache.calculation;

/**
 * A calculation that be invalidated (resetted), removing any existing cached value.
 *
 * <p>All resettable calculations must have a hashCode and equals implementation that checks that
 * relevant parameters are equal. They will be used in sets
 *
 * @author Owen Feehan
 */
public interface ResettableCalculation {

    /**
     * Resets the cached-calculation, so the next call to doOperationWithParams() is guaranteed to
     * calculation the operation, and store the value in the cache.
     */
    abstract void invalidate();
}
