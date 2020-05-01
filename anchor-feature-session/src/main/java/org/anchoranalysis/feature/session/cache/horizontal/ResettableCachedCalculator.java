package org.anchoranalysis.feature.session.cache.horizontal;

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
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheableCalculation;
import org.anchoranalysis.feature.cache.calculation.CacheableCalculationMap;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.feature.cache.calculation.FeatureSessionCacheCalculator;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculation;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculationMap;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

class ResettableCachedCalculator<T extends FeatureInput> extends FeatureSessionCacheCalculator<T> {

	private ResettableSet<FeatureCalculation<?,T>> setCalculation = new ResettableSet<>(false);
	private ResettableSet<CacheableCalculationMap<?,T,?,FeatureCalcException>> setCalculationMap = new ResettableSet<>(false);
	
	private LogErrorReporter logger;
	private SharedFeatureSet<T> sharedFeatures;
	
	public ResettableCachedCalculator(SharedFeatureSet<T> sharedFeatures) {
		super();
		this.sharedFeatures = sharedFeatures;
	}
	
	public void init( LogErrorReporter logger ) {
		this.logger = logger;
	}
	
	@Override
	public double calc(Feature<T> feature, SessionInput<T> input )
			throws FeatureCalcException {
		double val = feature.calcCheckInit(input);
		if (Double.isNaN(val)) {
			logger.getLogReporter().logFormatted("WARNING: NaN returned from feature %s", feature.getFriendlyName() );
		}
		return val;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <U> ResolvedCalculation<U,T> search(FeatureCalculation<U,T> calculation) {
		assert(calculation!=null);

		return new ResolvedCalculation<>(
			(CacheableCalculation<U,T,FeatureCalcException>) setCalculation.findOrAdd(calculation,null)
		);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S, U> ResolvedCalculationMap<S,T,U> search(
		CacheableCalculationMap<S,T,U,FeatureCalcException> calculation
	) {
		return new ResolvedCalculationMap<>( 
			(CacheableCalculationMap<S,T,U,FeatureCalcException>) setCalculationMap.findOrAdd(calculation,null)
		);
	}

	@Override
	public double calcFeatureByID(String id, SessionInput<T> input)
			throws FeatureCalcException {
		try {
			Feature<T> feature = sharedFeatures.getException(id);
			return calc( feature, input );
		} catch (NamedProviderGetException e) {
			throw new FeatureCalcException(
				String.format("Cannot locate feature with resolved-ID: %s", id ),
				e.summarize()
			);
		}
	}
	
	public void invalidate() {
		setCalculation.invalidate();
		setCalculationMap.invalidate();
	}

	@Override
	public String resolveFeatureID(String id) {
		return id;
	}
}