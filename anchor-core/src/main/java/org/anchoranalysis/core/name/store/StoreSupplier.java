package org.anchoranalysis.core.name.store;

import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Supplier of an object for a store
 * 
 * @author Owen Feehan
 *
 * @param <T> type supplied to the store
 */
@FunctionalInterface
public interface StoreSupplier<T> {

    /**
     * Gets the object being supplied to the store
     * 
     * @return the supplied object
     * @throws OperationFailedException if anything goes wrong
     */
    T get() throws OperationFailedException;
    
    /**
     * Memoizes (caches) the supplied object, and returning it with an identical interface
     * 
     * @param <T> type to supply
     * @param supplier supplier to cache
     * @return a {@link StoreSupplier} interface that memoizes the supplied object
     */
    public static <T> StoreSupplier<T> cache( StoreSupplier<T> supplier ) {
        return cacheResettable(supplier)::get;
    }
    
    /**
     * Memoizes (caches) the supplied object, and returning it with a {@link CachedSupplier} interface
     * <p>
     * This interface can be used to reset and do other operations o the cache.
     * 
     * @param <T> type to supply
     * @param supplier supplier to cache
     * @return a {@link StoreSupplier} interface that memoizes the supplied object
     */
    public static <T> CachedSupplier<T,OperationFailedException> cacheResettable( StoreSupplier<T> supplier ) {
        return CachedSupplier.cache(supplier::get);
    }
}
