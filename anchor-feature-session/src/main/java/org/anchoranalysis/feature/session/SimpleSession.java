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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/**
 * A simple session where each feature is initialized and calculated freshly every time
 * 
 * No caching occurs.
 * 
 * @author Owen Feehan
 *
 */
public class SimpleSession<T extends FeatureCalcParams> extends FeatureSession {

	public double calc(
		Feature<T> feature,
		FeatureInitParams paramsInit,
		SharedFeatureSet<T> sharedFeatures,
		T params,
		LogErrorReporter logger
	) throws FeatureCalcException, InitException {
		
		//NullCache cache = new NullCache(sharedFeatures);
		
		//FeatureSessionCacheRetriever retriever = cache.retriever();
		feature.initRecursive(paramsInit, logger);
		
		
		CacheableParams<T> paramsCacheable = SessionUtilities.createCacheable(
			params,
			new FeatureList<T>(feature),
			sharedFeatures,
			paramsInit,
			logger
		);
		
		double val = paramsCacheable.calc(feature);
		
//		if (Double.isNaN(val)) {
//			throw new FeatureCalcException( String.format("Warning: Feature %s returned NaN", feature.getCustomNameOrDscr() ) );
//		}
		return val;
	}
}
