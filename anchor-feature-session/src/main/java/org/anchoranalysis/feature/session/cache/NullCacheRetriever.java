package org.anchoranalysis.feature.session.cache;

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

import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculationMap;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class NullCacheRetriever extends FeatureSessionCacheRetriever {

	private SharedFeatureSet sharedFeatures;

	public NullCacheRetriever(SharedFeatureSet sharedFeatures) {
		super();
		this.sharedFeatures = sharedFeatures;
	}
	
	@Override
	public double calc(Feature feature, CacheableParams<? extends FeatureCalcParams> params )
			throws FeatureCalcException {
		return feature.calcCheckInit(params);
	}

	/**
	 * Always returns the cachedCalculation passed to the function
	 */
	@Override
	public <T> CachedCalculation<T> search(CachedCalculation<T> cc) {
		return cc;
	}
	
	@Override
	public <S, T> CachedCalculationMap<S, T> search(
			CachedCalculationMap<S, T> cc) {
		return cc;
	}

	@Override
	public double calcFeatureByID(String id, CacheableParams<? extends FeatureCalcParams> params) throws FeatureCalcException {
		try {
			return calc( sharedFeatures.getException(id), params );
		} catch (NamedProviderGetException e) {
			throw new FeatureCalcException(e.summarize());
		}
	}

	@Override
	public SharedFeatureSet getSharedFeatureList() {
		return sharedFeatures.duplicate();
	}

	@Override
	public String resolveFeatureID(String id) {
		return id;
	}

	@Override
	public String describeCaches() {
		return new String();
	}
	
	
	@Override
	public FeatureSessionCache createNewCache() {
		return null;	// TODO fix
	}

	@Override
	public boolean hasBeenInit() {
		return true;
	}
}
