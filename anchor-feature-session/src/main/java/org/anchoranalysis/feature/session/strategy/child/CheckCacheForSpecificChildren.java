package org.anchoranalysis.feature.session.strategy.child;

import java.util.Collection;

/*-
 * #%L
 * anchor-feature-session
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * For particular child-caches, check if a session-input is available from another LRU-cache and reuse.
 * 
 * <p>This is used to "redirect" certain child-caches to reuse sessions elsewhere</p>.
 * 
 * @author Owen Feehan
 *
 * @param <T> feature input-type
 */
public class CheckCacheForSpecificChildren<T extends FeatureInput> extends FindChildStrategy {
	
	private Class<?> cacheInputType;
	private Collection<Source<? extends FeatureInput>> sources;
	
	public static class Source<T extends FeatureInput> {
		
		private Supplier<LRUCache<T, SessionInput<T>>> cacheToSearch;
		private Set<ChildCacheName> specificChildren;
		
		public Source(Supplier<LRUCache<T, SessionInput<T>>> cacheToSearch, Set<ChildCacheName> specificChildren) {
			super();
			this.cacheToSearch = cacheToSearch;
			this.specificChildren = specificChildren;
		}

		public boolean containsChild(ChildCacheName name) {
			return specificChildren.contains(name);
		}
		
		public Optional<SessionInput<T>> getInputIfPresent( T input ) {
			return cacheToSearch.get().getIfPresent( (T) input);
		}
	}
		
	public CheckCacheForSpecificChildren(
		Class<?> cacheInputType,
		Collection<Source<? extends FeatureInput>> sources
	) {
		super();
		this.sources = sources;
		this.cacheInputType = cacheInputType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(FeatureSessionCache<?> parentCache,
			CacheCreator factory, ChildCacheName childName, V input) {
		
		if (cacheInputType.isAssignableFrom(input.getClass())) {
			
			for (Source<?> src : sources) {
			
				if (src.containsChild(childName)) {
					// Check cache if has a session input to match, and if so, reuse it
					Optional<SessionInput<V>> opt = ((Source<V>) src).getInputIfPresent(input);
					if (opt.isPresent()) {
						return (FeatureSessionCache<V>) opt.get().getCache();
					}
				}
			}
		}
		
		return parentCache.childCacheFor(childName, input.getClass(), factory);
	}

	@Override
	public FindChildStrategy strategyForGrandchild() {
		return DefaultFindChildStrategy.instance;
	}	
}
