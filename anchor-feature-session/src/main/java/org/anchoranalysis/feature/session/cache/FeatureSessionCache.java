package org.anchoranalysis.feature.session.cache;

import org.anchoranalysis.core.error.InitException;

/*
 * #%L
 * anchor-feature
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.creator.CacheCreator;


/**
 * A context in which to calculate features while caching certain duplicated internal calculations among the features.
 * 
 * <p>When a user calculates a a single feature, they often are calculating other features at the same time (on one or more objects). This creates
 * possibilities for various different forms of caching of results, and different initialization strategies.</p>
 *   
 * <p>This class represents one such bunch of calculations. Different implementations provide different strategies.</p>
 * 
 * <p>Each session-cache may contain "child" caches for particular string identifiers. This provides a hierarchy of caches and sub-caches as
 *   many features change the underlying objects that are being calculated, and need separate space in the cache.</p>
 *   
 * <p>A call to {{@link #invalidate()} removes any existing caching (also in the children) and guarantees the next calculation will be fresh</p>/
 *
 * @author Owen Feehan
 * @param T feature-input
 *
 */
public abstract class FeatureSessionCache<T extends FeatureInput> {

	
	/**
	 * Initialises the cache. Should always be called once before any calculations occur
	 * @param featureInitParams TODO
	 * @param logger TODO
	 * @param logCacheInit TODO
	 */
	public abstract void init(FeatureInitParams featureInitParams, LogErrorReporter logger, boolean logCacheInit) throws InitException;
	
	/**
	 * Triggered before a new calculation occurs
	 */
	public abstract void invalidate();
	
	
	/**
	 * A means of calculating feature values using this cache.
	 * 
	 * @return
	 */
	public abstract FeatureSessionCacheCalculator<T> calculator();
	
	/**
	 * Gets/creates a child-cache for a given name
	 * 
	 * <p>This function trusts the caller to use the correct type for the child-cache.</p>
	 * 
	 * @param <V> params-type of the child cache to found
	 * @param childName name of the child-cache
	 * @param paramsType the type of V
	 * @param cacheCreator TODO
	 * @return the existing or new child cache of the given name
	 */
	public abstract <V extends FeatureInput> FeatureSessionCache<V> childCacheFor(String childName, Class<?> paramsType, CacheCreator cacheCreator);
}
