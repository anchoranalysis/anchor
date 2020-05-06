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
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

public abstract class FindChildStrategy {

	/**
	 * Selects a child-cache given a parent and a child-name
	 * 
	 * <p>This may involve reusing an existing cache, or creating a new one, depending on the strategy.</p>.
	 * 
	 * @param <V> type-of-input to be used to calculate on the child-cache
	 * @param parentCache the existing parent-cache
	 * @param factory how to create new caches
	 * @param childCacheName name of child-cache
	 * @param input input to be used for calculations on the child-cache
	 * @return an existing or newly created child-cache depending on the strategy.
	 */
	public abstract <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(
		FeatureSessionCache<?> parentCache,
		CacheCreator factory,
		ChildCacheName childCacheName,
		V input
	) throws FeatureCalcException;
	
	/**
	 * What strategy to use for children-of-children?
	 * 
	 * @return the strategy
	 */
	public abstract FindChildStrategy strategyForGrandchild();
	
	/** If set, these particular-caches are exceptionall NOT invalidated during the typical invalidation operation on their parent. If not-set, there are no exceptions. */
	public abstract Optional<Set<ChildCacheName>> cachesToAvoidInvalidating();
}
