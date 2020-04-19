package org.anchoranalysis.feature.cachedcalculation;

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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

/**
 * Binds params with a CachedCalculation and exposes it as the {@link org.anchoranalysis.core.cache.Operation} interface
 * 
 * (reverse-currying)
 * 
 * @author Owen Feehan
 *
 * @param <S> result-type
 * @param <T> params-type
 */
public class CachedCalculationOperation<S, T extends FeatureCalcParams> implements Operation<S> {

	private RslvdCachedCalculation<S,T> cachedCalculation;
	private T params;
		
	public CachedCalculationOperation(RslvdCachedCalculation<S,T> cachedCalculation,	T params) {
		super();
		this.cachedCalculation = cachedCalculation;
		this.params = params;
	}
	
	@Override
	public S doOperation() throws ExecuteException {
		return cachedCalculation.getOrCalculate(params);
	}
}
