/* (C)2020 */
package org.anchoranalysis.feature.session.strategy.replace;

import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Reuse (without needing to invalidate) an existing session-input as stored in a least-recently
 * used cache, otherwise create a new one.
 *
 * @author Owen Feehan
 * @param <T> feature-input
 */
public class CacheAndReuseStrategy<T extends FeatureInput> implements ReplaceStrategy<T> {

    private static final int CACHE_SIZE = 200;

    private LRUCache<T, SessionInput<T>> cache;

    public CacheAndReuseStrategy(CacheCreator cacheCreator) {
        ReplaceStrategy<T> delegate = new AlwaysNew<>(cacheCreator);
        cache = new LRUCache<>(CACHE_SIZE, delegate::createOrReuse);
    }

    @Override
    public SessionInput<T> createOrReuse(T input) throws FeatureCalcException {
        try {
            return cache.get(input);
        } catch (GetOperationFailedException e) {
            throw new FeatureCalcException(e);
        }
    }

    public LRUCache<T, SessionInput<T>> getCache() {
        return cache;
    }
}
