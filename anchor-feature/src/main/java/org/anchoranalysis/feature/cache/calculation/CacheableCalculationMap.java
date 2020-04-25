package org.anchoranalysis.feature.cache.calculation;

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
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Similar to a {@link CacheableCalculation} but stores several evaluations parameterised
 *   by a key
 * 
 * @author Owen Feehan
 * @param S result-type
 * @param T feature input-type
 * @param U key-type
 */
public abstract class CacheableCalculationMap<S,T extends FeatureInput,U> implements ResettableCalculation {
	/**
	 * Executes the operation and returns a result, either by doing the calculation, or retrieving
	 *   a cached-result from previously.
	 * 
	 * @param If there is no cached-value, and the calculation occurs, these parameters are used. Otherwise ignored.
	 * @return the result of the calculation
	 * @throws ExecuteException if the calculation cannot finish, for whatever reason
	 */
	public abstract S getOrCalculate( T input, U key ) throws FeatureCalcException;

	@Override
	public abstract boolean equals(Object other);

	@Override
	public abstract int hashCode();
	
}
