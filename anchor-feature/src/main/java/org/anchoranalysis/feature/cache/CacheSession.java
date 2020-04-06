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

import java.util.List;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculationMap;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.session.cache.ICachedCalculationSearch;

/**
 * A time-limited period where we cache certain results of feature-calculaiton
 * 
 * @author feehano
 *
 */
public class CacheSession implements ICachedCalculationSearch {
	
	private FeatureSessionCacheRetriever main;
	private FeatureSessionCacheRetriever[] additionalCaches;
	
	public CacheSession(FeatureSessionCacheRetriever main,
			FeatureSessionCacheRetriever[] additionalCaches) {
		super();
		this.main = main;
		this.additionalCaches = additionalCaches;
	}

	public FeatureSessionCacheRetriever main() {
		return main;
	}

	public FeatureSessionCacheRetriever additional(int index) {
		return additionalCaches[index];
	}

	public double calc(Feature feature, CacheableParams<? extends FeatureCalcParams> params) throws FeatureCalcException {
		return main.calc(feature, params);
	}

	public ResultsVector calc(List<Feature> features,  CacheableParams<? extends FeatureCalcParams> params) throws FeatureCalcException {
		return main.calc(features, params);
	}

	public double calcFeatureByID(String resolvedID, CacheableParams<? extends FeatureCalcParams> params) throws FeatureCalcException {
		return main.calcFeatureByID(resolvedID, params);
	}

	@Override
	public <T> CachedCalculation<T> search(CachedCalculation<T> cc) {
		return main.search(cc);
	}
	
	@Override
	public <S, T> CachedCalculationMap<S, T> search(CachedCalculationMap<S, T> cc) {
		return main.search(cc);
	}

	public String resolveFeatureID(String id) {
		return main.resolveFeatureID(id);
	}

	public boolean hasBeenInit() {
		return main.hasBeenInit();
	}
	
	@Override
	public String toString() {
		return String.format(
			"CacheSession(%d) with main=%d and %d additionals",
			System.identityHashCode(this),
			System.identityHashCode(main),
			additionalCaches.length
		);
	}
	
	
}
