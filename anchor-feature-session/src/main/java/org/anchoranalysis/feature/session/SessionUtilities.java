package org.anchoranalysis.feature.session;

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

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.cache.creator.CacheCreator;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.creator.CacheCreatorSimple;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

class SessionUtilities {
	
	public static <T extends FeatureCalcParams> CacheableParams<T> createCacheable(
		T params,
		FeatureList<T> featureList,
		SharedFeatureSet<T> sharedFeatures,
		FeatureInitParams featureInitParams,
		LogErrorReporter logger
	) throws FeatureCalcException {
		return createCacheable(
			params,
			new CacheCreatorSimple(featureList, sharedFeatures, featureInitParams, logger)
		);
	}
	
	public static <T extends FeatureCalcParams> CacheableParams<T> createCacheable(T params, CacheCreator cacheFactory ) throws FeatureCalcException {
		
		if (params==null) {
			throw new FeatureCalcException("Params are null. Cannot proceed with feature calculation");
		}
		
		return new CacheableParams<T>(
			params,
			cacheFactory
		);
	}
}
