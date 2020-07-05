package org.anchoranalysis.feature.session.strategy.child;

import java.util.Optional;
import java.util.Set;

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

import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.input.FeatureInput;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The default strategy for finding children by always directly taking (or creating a child-cache) as necessary.
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class DefaultFindChildStrategy extends FindChildStrategy {

	public static final FindChildStrategy instance = new DefaultFindChildStrategy();
	
	@Override
	public <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(
		FeatureSessionCache<?> parentCache,
		CacheCreator factory,
		ChildCacheName childName,
		V input
	) {
		return parentCache.childCacheFor(childName, input.getClass(), factory);
	}

	@Override
	public FindChildStrategy strategyForGrandchild() {
		return instance;
	}

	public static FindChildStrategy instance() {
		return instance;
	}

	@Override
	public Optional<Set<ChildCacheName>> cachesToAvoidInvalidating() {
		return Optional.empty();
	}
}
