package org.anchoranalysis.feature.session;

/*-
 * #%L
 * anchor-feature-session
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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.AllAdditionalCaches;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheFactory;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

// TODO BROKEN, most likely to be removed
class CacheUtilities {

	public static AllAdditionalCaches createNecessaryAdditionalCaches( FeatureSessionCacheFactory factory, FeatureList namedFeatures, SharedFeatureSet sharedFeatures ) throws CreateException {
		
		AllAdditionalCaches cacheAdditional = new AllAdditionalCaches();
		
		//try {
			// Let's get a list of all the additional-features we need
			//List<String> additionalCachesNeededList = namesOfAllAdditionalCaches( namedFeatures, sharedFeatures );
			
			// Make it a set
			//Set<String> additionalCachesNeeded = additionalCachesNeededList.stream().collect( Collectors.toSet() );
			
			// For each name, we create a cache
			//for( String key : additionalCachesNeeded) {
			//	FeatureSessionCache cacheNew = factory.create(namedFeatures, sharedFeatures.duplicate() );
			//	cacheAdditional.put(key, cacheNew);
			//}
			
		//} catch (OperationFailedException e) {
//			throw new CreateException(e);
	//	}
		
		return cacheAdditional;
	}
}
