package org.anchoranalysis.feature.session.strategy.child;

import java.util.Optional;

import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInput;

@FunctionalInterface
public interface CacheSupplier<T extends FeatureInput, E extends Throwable> {
	
	Optional<LRUCache<T,SessionInput<T>>> get() throws E;
}