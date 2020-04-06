package org.anchoranalysis.feature.session;

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


import java.util.List;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

/**
 * A sub-session is spawned from another session
 * 
 * It is used to calculate feature-values for a subset of associated features (rather than them all, as with the direct calc() methods)
 * 
 * Unlike the other calc methods, no cache reset occurs, when they are called. There params are fixed.
 * 
 * Only features that have been initialised should be calculated. Initialization should be triggered through calling a start() or similar method
 *   on a FeatureSession. This means only features associated with a session (or their recursive dependencies) should be calculated.
 * 
 * @author Owen Feehan
 *
 */
public class Subsession {
	
	private CachePlus cache;

	public Subsession(CachePlus cache) {
		super();
		this.cache = cache;
	}

	public void beforeNewCalc() {
		cache.invalidate();
	}

	public ResultsVector calcSubset( FeatureList features, FeatureCalcParams params ) throws FeatureCalcException {
		
		ResultsVector res = new ResultsVector( features.size() );

		for( int i=0; i<features.size(); i++) {
			Feature f = features.get(i);
			double val = cache.retriever().calc(
				f,
				createCacheable(params)
			);
			res.set(i,val);
		}
		return res;
	}
	
	public ResultsVector calcSubsetSuppressErrors(
		FeatureList features,
		List<CacheableParams<? extends FeatureCalcParams>> listParams,
		ErrorReporter errorReporter
	) {
		assert(features.size()==listParams.size());
		
		ResultsVector res = new ResultsVector( features.size() );

		for( int i=0; i<features.size(); i++) {
			Feature f = features.get(i);
			
			double val;
			try {
				CacheableParams<? extends FeatureCalcParams> params = listParams.get(i);
				
				val = cache.retriever().calc( f, params	);
				res.set(i,val);
			} catch (Exception e) {
				res.setError(i,e);
				errorReporter.recordError(Subsession.class, e);
			}
			
		}
		return res;

	}
	
	public double calc( Feature feature, FeatureCalcParams params ) throws FeatureCalcException {
		return cache.retriever().calc(
			feature,
			createCacheable(params)
		);
	}
	
	private CacheableParams<FeatureCalcParams> createCacheable(FeatureCalcParams params) {
		return SessionUtilities.createCacheable(
			params,
			() -> cache.createCache().retriever()
		);
	}

}