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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.init.FeatureInitParams;


/**
 * When a user calculates a feature, he/she typically calculates the one or more features on many different objects
 * 
 * This creates possibilities for various different forms of caching of results, and different initialization
 *   strategies.
 *   
 * The FeatureSession object represents one such bunch of calculations. Different implementations provide
 *   different strategies for caching.
 *
 * 
 * @author Owen Feehan
 *
 */
public abstract class FeatureSessionCache {

	
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
	
	public abstract FeatureSessionCacheRetriever retriever();
	
	public abstract void assignResult( FeatureSessionCache other ) throws OperationFailedException;
	
	/**
	 * A deep copy of the current state of the cache
	 * 
	 * @return
	 */
	public abstract FeatureSessionCache duplicate();
}
