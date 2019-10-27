package org.anchoranalysis.feature.cache;

/*-
 * #%L
 * anchor-feature
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.FeatureBase;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;

public abstract class FeatureCacheDefinition {
	
	/**
	 * Specifies a prefix that is prepended to the names of the additional-caches that children need
	 * 
	 * All child-features are treated identically
	 * 
	 * @return
	 */
	public abstract String prefixForAdditionalCachesForChildren();

	private FeatureBase parentFeature;
	
	private Feature feature;

	public FeatureCacheDefinition( Feature feature ) {
		super();
		this.feature = feature;
	}

	public CacheSession rslv( FeatureBase parentFeature, CacheRetrieverPlusAll cache ) throws InitException {
		
		this.parentFeature = parentFeature;
		
		// We record the cache for later
		FeatureSessionCacheRetriever retriever = cache.getCache();
		
		// We setup the additional caches, and record them for later
		List<String> listAdditionalCachesNeeded = additionalCachesIncludingParentPrefixes();
		
		FeatureSessionCacheRetriever[] additionalCaches = createAdditionalRetrievers(
			cache.getAllAdditionalCaches(),
			listAdditionalCachesNeeded
		);	
		
		return new CacheSession( retriever, additionalCaches );
	}
	
	private static FeatureSessionCacheRetriever[] createAdditionalRetrievers( AllAdditionalCaches allAdditionalCaches, List<String> listAdditionalCachesNeeded ) throws InitException {
		// catch the null case
		if (allAdditionalCaches==null) {
			if (listAdditionalCachesNeeded.size()>0) {
				throw new InitException("At least one additional-cache is specified, but allCaches is null");
			} else {
				return new FeatureSessionCacheRetriever[]{};
			}
		}
		
		return allAdditionalCaches.createRetrievers( listAdditionalCachesNeeded );
	}
	
	
	/**
	 * Finds the additional caches this feature needs, prefixing as needed with the parent-prefixes
	 * 
	 * @param out
	 */
	public List<String> additionalCachesIncludingParentPrefixes() {
		List<String> out = new ArrayList<>();
		additionalCaches(out);
		
		// Wet prefix
		String prefix = allPrefixesFromParents();
		if (prefix.isEmpty()) {
			return out;
		}
		
		List<String> outPrepend = new ArrayList<>();
			
		for( String s : out ) {
			outPrepend.add( prefix + s );
		}
		return outPrepend;
	}
	
	
	// 
	/**
	 * One cache is always passed to Features-Calculation.
	 * 
	*  This specifies the name and order of additional caches required.
	 * @param out TODO
	 */
	protected abstract void additionalCaches(List<String> out);	
		

	/**
	 * One cache is always passed to Features-Calculation.
	 * 
	 *  This specifies the name and order of additional caches required.
	 *  
	 * 
	 * @return 
	 * @throws CreateException 
	 * @throws BeanMisconfiguredException 
	 * @throws OperationFailedException 
	 */	
	public final void additionalCachesIncludingChildren( List<String> out ) throws OperationFailedException {
		additionalCachesIncludingChildrenFor( feature, out );
	}

	private void additionalCachesIncludingChildrenFor( Feature feature, List<String> out ) throws OperationFailedException {
		
		additionalCaches(out);
		
		try {
			List<Feature> childFeatures = feature.findChildrenOfClass( feature.getOrCreateBeanFields(), Feature.class );
			
			String prefix = prefixForAdditionalCachesForChildren();
			for( Feature f : childFeatures) {
				
				if (prefix.isEmpty()) {
					// We directly add
					additionalCachesIncludingChildrenFor(f, out);
				} else {
					// We add with a prefix
					List<String> childNeeds = new ArrayList<String>();
					additionalCachesIncludingChildrenFor(f, childNeeds);
					
					addToListWithPrefix( childNeeds, prefix, out );
				}
			}
			
		} catch (BeanMisconfiguredException e) {
			throw new OperationFailedException(e);
		}
	}
	
	/** Builds a unique string of prefixes until parent */
	private final String allPrefixesFromParents() {
		StringBuilder sb = new StringBuilder();
		FeatureCacheDefinition fb = this;
		while( (fb = parentCacheDefinition(fb) )!=null ) {
			String prefix = fb.prefixForAdditionalCachesForChildren();
			if (prefix!=null) {
				sb.append(prefix);
			}
		}
		return sb.toString();
	}
	
	private FeatureCacheDefinition parentCacheDefinition( FeatureCacheDefinition fb ) {
		if (fb.getParentFeature()==null) {
			return null;
		}
		return fb.getParentFeature().cacheDefinition();
	}
	
	
	private static void addToListWithPrefix( List<String> in, String prefix, List<String> out ) {
		for( String s : in) {
			out.add( prefix + s );
		}
	}
	
	public FeatureBase getParentFeature() {
		return parentFeature;
	}
}
