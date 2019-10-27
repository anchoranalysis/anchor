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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

// Memoized calculation

/**
 * Caches the result of a calculation, until reset() is called
 * 
 * Implements an equivalence-relation via IParamsEquals.paramsEquals that checks
 * that the 'parameters' of a CachedCalculation are identical
 * 
 * This allows the user to search through a collection of CachedCalculation to
 *   find one with identical parameters and re-use it
 * 
 * The implementation of paramsEquals does a deep-search through fields
 *  of the class and does a equals() call.  All fields are considered apart
 *  from those marked 'transient'.
 * 
 * IMPORTANT NOTE: It is therefore important to make sure to put 'transient' on
 * all fields, which should not be equality-checked, when designing the classes.
 * 
 * @author Owen Feehan
 *
 * @param <ResultType> result-type of the calculation
 */
public abstract class CachedCalculation<ResultType> implements IResettableCachedCalculation {
	
	/**
	 * Executes the operation and returns a result, either by doing the calculation, or retrieving
	 *   a cached-result from previously.
	 * 
	 * @param If there is no cached-value, and the calculation occurs, these parameters are used. Otherwise ignored.
	 * @return the result of the calculation
	 * @throws ExecuteException if the calculation cannot finish, for whatever reason
	 */
	public abstract ResultType getOrCalculate( FeatureCalcParams params ) throws ExecuteException;
	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();	
	
	@Override
	public abstract CachedCalculation<ResultType> duplicate();
	
	@Override
	public abstract void assignResult( Object src ) throws OperationFailedException;
	
	public abstract boolean hasCachedCalculation();
}
