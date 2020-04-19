package org.anchoranalysis.feature.session.cache;

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

import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculationMap;
import org.anchoranalysis.feature.cachedcalculation.RslvdCachedCalculation;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

public interface ICachedCalculationSearch<T extends FeatureCalcParams> {

	/**
	 * 
	 * Returns an equivalent CachedCalculation to what is passed. Different caches implement different
	 * strategies.
	 * 
	 * @param cc the cached-calculation to find an equivalent for
	 * @return
	 */
	<S> RslvdCachedCalculation<S,T> search( CachedCalculation<S,T> cc );

	
	/**
	 * 
	 * Returns an equivalent CachedCalculationMap to what is passed. Different caches implement different
	 * strategies.
	 * 
	 * @param cc the cached-calculation map to find an equivalent for
	 * @return
	 */
	<S,U> CachedCalculationMap<S,T,U> search( CachedCalculationMap<S,T,U> cc );
}
