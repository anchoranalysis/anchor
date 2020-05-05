package org.anchoranalysis.feature.session.strategy.child;

import java.util.Optional;
import java.util.Set;

import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * An existing cache that can be used as a source for child-caches elsewhere
 * 
 * @author Owen Feehan
 *
 * @param <T> input-type associated with cache
 */
public class CacheTransferSource<T extends FeatureInput> {
	
	private CacheSupplier<T,OperationFailedException> cacheToSearch;
	private Set<ChildCacheName> specificChildren;
	
	public CacheTransferSource(CacheSupplier<T,OperationFailedException> cacheToSearch, Set<ChildCacheName> specificChildren) {
		super();
		this.cacheToSearch = cacheToSearch;
		this.specificChildren = specificChildren;
	}

	public boolean containsChild(ChildCacheName name) {
		return specificChildren.contains(name);
	}
	
	public Optional<SessionInput<T>> getInputIfPresent( T input ) throws OperationFailedException {
		Optional<LRUCache<T,SessionInput<T>>> cache = cacheToSearch.get();
		return cache.flatMap( a->
			a.getIfPresent( (T) input)
		);
	}

	public Set<ChildCacheName> getCacheNames() {
		return specificChildren;
	}
}