/* (C)2020 */
package org.anchoranalysis.feature.session.strategy.child;

import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * An existing cache that can be used as a source for child-caches elsewhere
 *
 * @author Owen Feehan
 * @param <T> input-type associated with cache
 */
@AllArgsConstructor
public class CacheTransferSource<T extends FeatureInput> {

    private final CacheSupplier<T, OperationFailedException> cacheToSearch;
    private final Set<ChildCacheName> specificChildren;

    public boolean containsChild(ChildCacheName name) {
        return specificChildren.contains(name);
    }

    public Optional<SessionInput<T>> getInputIfPresent(T input) throws OperationFailedException {
        Optional<LRUCache<T, SessionInput<T>>> cache = cacheToSearch.get();
        return cache.flatMap(a -> a.getIfPresent((T) input));
    }

    public Set<ChildCacheName> getCacheNames() {
        return specificChildren;
    }
}
