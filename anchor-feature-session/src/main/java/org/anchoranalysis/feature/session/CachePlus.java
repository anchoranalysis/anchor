package org.anchoranalysis.feature.session;

import org.anchoranalysis.core.error.CreateException;
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
import org.anchoranalysis.feature.cache.AllAdditionalCaches;
import org.anchoranalysis.feature.cache.CacheRetrieverPlusAll;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheFactory;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.shared.SharedFeatureSet;


/**
 * A cache for features with multiple components
 * 	- cacheMain (the cache used mostly for calculating features)
 *  - cacheAdditional (other caches that the feature can use if they don't want to clash with cacheMain)
 *  
 * The features are each queried for the names of any additional-caches needed, so a superset
 *  of all necessary features exists.
 * 
 * @author Owen Feehan
 *
 */
public class CachePlus extends FeatureSessionCache {

	private FeatureSessionCache cacheMain;
	private AllAdditionalCaches cacheAdditional;
		
	public CachePlus(FeatureSessionCacheFactory factory, FeatureList namedFeatures, SharedFeatureSet sharedFeatures) throws CreateException {
		this(
			factory.create(namedFeatures, sharedFeatures.duplicate() ),
			CacheUtilities.createNecessaryAdditionalCaches( factory, namedFeatures, sharedFeatures )
		);
		
	}
	
	private CachePlus( FeatureSessionCache cacheMain, AllAdditionalCaches cacheAdditionalIn ) throws CreateException {
		// Deep copy the main cache
		this.cacheMain = cacheMain;
		
		// deep copy the additional caches
		this.cacheAdditional = cacheAdditionalIn;
	}
	
	public CacheRetrieverPlusAll retrieverPlus() {
		return new CacheRetrieverPlusAll(
			retriever(),
			getCacheAdditional()
		);
	}
	
	@Override
	public void init(FeatureInitParams featureInitParams,
			LogErrorReporter logger, boolean logCacheInit) throws InitException {
		
		// Init caches
		cacheMain.init(featureInitParams, logger, logCacheInit);
		
		featureInitParams.setCache( new CacheRetrieverPlusAll(
			retriever(),
			cacheAdditional
		));
		
		for( FeatureSessionCache add : cacheAdditional.values()) {
			add.init(featureInitParams, logger, logCacheInit);
			
		}
		
		// Init shared-features
		initSharedFeatures( featureInitParams, logger );
	}
	
	private void initSharedFeatures( FeatureInitParams featureInitParams, LogErrorReporter logger) throws InitException {
		cacheMain.retriever().getSharedFeatureList().initRecursive( featureInitParams, logger );
		for( FeatureSessionCache add : cacheAdditional.values()) {
			add.retriever().getSharedFeatureList().initRecursive( featureInitParams, logger );
		}
	}

	@Override
	public void invalidate() {
		// DEBUG
		cacheMain.invalidate();
		cacheAdditional.invalidate();
	}
	
	public void replaceAdditionalCache(String key, FeatureSessionCache cache)
			throws OperationFailedException {
		cacheAdditional.put(key, cache);
	}

	public FeatureSessionCache getAdditionalCache(String key) {
		return cacheAdditional.get(key);
	}
	

	@Override
	public FeatureSessionCacheRetriever retriever() {
		return cacheMain.retriever();
	}

	@Override
	public void assignResult(FeatureSessionCache other) throws OperationFailedException {
		CachePlus otherCast = (CachePlus) other;
		cacheMain.assignResult( otherCast.cacheMain );
		cacheAdditional.assignResult( otherCast.cacheAdditional );
	}

	@Override
	public FeatureSessionCache duplicate() throws CreateException {
		return new CachePlus( cacheMain.duplicate(), cacheAdditional.duplicate() );
	}

	public AllAdditionalCaches getCacheAdditional() {
		return cacheAdditional;
	}

	public int numItemsAdditional() {
		return cacheAdditional.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( String.format("main=%d\n", System.identityHashCode(cacheMain)) );
		sb.append( cacheAdditional.toString() );
		return sb.toString();
	}

}
