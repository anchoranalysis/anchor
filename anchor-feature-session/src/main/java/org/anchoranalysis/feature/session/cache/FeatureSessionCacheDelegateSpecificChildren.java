package org.anchoranalysis.feature.session.cache;

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

import java.util.Set;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCache;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCacheCalculator;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;


/**
 * Delegates to a FeatureSessionCache to the primary-delegate, except for specific children which are delegated elsewhere
 * @author Owen Feehan
 *
 * @param <T>
 */
public class FeatureSessionCacheDelegateSpecificChildren<T extends FeatureInput> extends FeatureSessionCache<T> {

	private FeatureSessionCache<T> delegatePrimary;
	private FeatureSessionCache<FeatureInput> cacheSpecificChildren;
	private Set<ChildCacheName> specificChildren;
	
	public FeatureSessionCacheDelegateSpecificChildren(FeatureSessionCache<T> delegatePrimary,
			FeatureSessionCache<FeatureInput> cacheSpecificChildren, Set<ChildCacheName> specificChildren) {
		super();
		this.delegatePrimary = delegatePrimary;
		this.cacheSpecificChildren = cacheSpecificChildren;
		this.specificChildren = specificChildren;
	}	
	
	
	@Override
	public void init(FeatureInitParams featureInitParams, LogErrorReporter logger) throws InitException {
		delegatePrimary.init(featureInitParams, logger);
	}

	@Override
	public void invalidate() {
		delegatePrimary.invalidate();
	}

	@Override
	public FeatureSessionCacheCalculator<T> calculator() {
		return delegatePrimary.calculator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(ChildCacheName childName, Class<?> paramsType,
			CacheCreator cacheCreator) {

		if (specificChildren.contains(childName)) {
			return (FeatureSessionCache<V>) cacheSpecificChildren;
		}
		
		return delegatePrimary.childCacheFor(childName, paramsType, cacheCreator);
	}
}
