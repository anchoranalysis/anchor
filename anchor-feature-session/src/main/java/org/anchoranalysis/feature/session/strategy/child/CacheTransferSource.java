package org.anchoranalysis.feature.session.strategy.child;

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

import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.input.FeatureInput;

import lombok.AllArgsConstructor;

/**
 * An existing cache that can be used as a source for child-caches elsewhere
 * 
 * @author Owen Feehan
 *
 * @param <T> input-type associated with cache
 */
@AllArgsConstructor
public class CacheTransferSource<T extends FeatureInput> {

	private final CacheSupplier<T,OperationFailedException> cacheToSearch;
	private final Set<ChildCacheName> specificChildren;

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
