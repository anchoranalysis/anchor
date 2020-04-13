package org.anchoranalysis.feature.session.cache.creator;

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.feature.cache.creator.CacheCreator;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;

/**
 * Rememembers all caches created by a delegate CacheCreator and provides a means to invalidate them all
 * 
 * @author owen
 *
 */
public class CacheCreatorRemember implements CacheCreator {

	private CacheCreator delegate;

	private List<FeatureSessionCache<?>> list = new ArrayList<>();
	
	public CacheCreatorRemember(CacheCreator delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public <T extends FeatureCalcParams> FeatureSessionCache<T> create(Class<?> paramsType) {

		FeatureSessionCache<T> cache = delegate.create(paramsType);
		list.add(cache);
		return cache;
	}

	/** Invalidates all the rememembered caches */
	public void invalidateAll() {
		list.stream().forEach(
			item -> item.invalidate()
		);
	}
}
