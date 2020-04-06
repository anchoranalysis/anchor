package org.anchoranalysis.feature.session;

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
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheFactory;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.shared.SharedFeatureSet;


/**
 * Creates caches as needed, remembering them all, so they can be invalidated as needed.
 *  
 * The features are each queried for the names of any additional-caches needed, so a superset
 *  of all necessary features exists.
 * 
 * @author Owen Feehan
 *
 */
public class CachePlus extends FeatureSessionCache {

	private FeatureSessionCacheFactory factory;
	private FeatureList namedFeatures;
	private SharedFeatureSet sharedFeatures;
	
	private FeatureInitParams featureInitParams;
	private LogErrorReporter logger;
		
	public CachePlus(FeatureSessionCacheFactory factory, FeatureList namedFeatures, SharedFeatureSet sharedFeatures) {
		this.namedFeatures = namedFeatures;
		this.sharedFeatures = sharedFeatures;
		this.factory = factory;
	}
	
	public FeatureSessionCache createCache() {
				
		FeatureSessionCache cache = factory.create(
			namedFeatures,
			sharedFeatures.duplicate()
		);
		try {
			cache.init(featureInitParams, logger, false);
		} catch (InitException e) {
			assert(false);
		}
		return cache;
	}
	
	@Override
	public void init(FeatureInitParams featureInitParams,
			LogErrorReporter logger, boolean logCacheInit) throws InitException {
		
		// Init shared-features
		initSharedFeatures( featureInitParams, logger );
	}

	@Override
	public void invalidate() {
		// DEBUG
		//cacheMain.invalidate();
	}
	

	@Override
	public FeatureSessionCacheRetriever retriever() {
		assert(false);
		return null;
	}

	@Override
	public void assignResult(FeatureSessionCache other) throws OperationFailedException {
		//CachePlus otherCast = (CachePlus) other;
		//cacheMain.assignResult( otherCast.cacheMain );
	}

	@Override
	public FeatureSessionCache duplicate() {
		return new CachePlus( factory, namedFeatures, sharedFeatures );
	}
		
	private void initSharedFeatures( FeatureInitParams featureInitParams, LogErrorReporter logger) throws InitException {
		sharedFeatures.initRecursive( featureInitParams, logger );
		this.featureInitParams = featureInitParams;
		this.logger = logger;
	}
}
