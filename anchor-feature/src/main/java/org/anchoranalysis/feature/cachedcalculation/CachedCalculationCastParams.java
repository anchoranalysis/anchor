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


import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

public abstract class CachedCalculationCastParams<ResultType,ParamsType extends FeatureCalcParams> extends CachedCalculation<ResultType> {

	private transient ParamsType params;
	
	// We delegate the actualy execution of the cache
	private transient CachedOperation<ResultType> delegate = new CachedOperation<ResultType>() {

		@Override
		protected ResultType execute() throws ExecuteException {
			return CachedCalculationCastParams.this.execute( params );
		}
	};
	
	@Override
	public synchronized ResultType getOrCalculate( FeatureCalcParams params ) throws ExecuteException {
		
		// DEBUG
		// Checks we have the same params, if we call the cached calculation a second-time. This maybe catches errors.
		if (hasCachedCalculation()) {
			if (params!=null && !params.equals(this.params)) {
				throw new ExecuteException(
					new FeatureCalcException("This feature already has been used, its cache set is already set to different params")
				);
			}
		}
		
		initParams(params);
		return delegate.doOperation();
	}

	@Override
	public synchronized void reset() {
		delegate.reset();
		this.params = null;	// Just to be clean, release memory, before the next getOrCalculate
	}
		
	protected abstract ResultType execute( ParamsType params ) throws ExecuteException;

	@Override
	public boolean hasCachedCalculation() {
		return delegate.isDone();
	}

	@Override
	public void assignResult( Object savedResult) {
		@SuppressWarnings("unchecked")
		CachedCalculationCastParams<ResultType,ParamsType> savedResultCached = (CachedCalculationCastParams<ResultType,ParamsType>) savedResult;
		delegate.assignFrom( savedResultCached.delegate );
	}
	
	@SuppressWarnings("unchecked")
	private synchronized void initParams(FeatureCalcParams params) {
		this.params = (ParamsType) params;
	}	
}
