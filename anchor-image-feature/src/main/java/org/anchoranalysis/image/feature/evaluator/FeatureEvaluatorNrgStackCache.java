package org.anchoranalysis.image.feature.evaluator;

/*
 * #%L
 * anchor-image-feature
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


import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.cache.LRUHashMapCache;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.feature.session.FeatureSessionCreateParamsSingle;
import org.anchoranalysis.image.objmask.ObjMask;

public class FeatureEvaluatorNrgStackCache implements EvaluateSingleObjMask {

	FeatureSessionCreateParamsSingle featureSession;
	private LRUHashMapCache<Double, ObjMask> cache;

	private LRUHashMapCache.Getter<Double, ObjMask> getter = new LRUHashMapCache.Getter<Double, ObjMask>() {

		@Override
		public Double get(ObjMask index) throws GetOperationFailedException {
			try {
				return featureSession.calc(index);
			} catch (FeatureCalcException e) {
				throw new GetOperationFailedException(e);
			}
		}
		
	};
	
	public FeatureEvaluatorNrgStackCache(FeatureSessionCreateParamsSingle featureSession, int cacheSize, CacheMonitor cacheMonitor) {
		super();
		this.featureSession = featureSession;
		cache = LRUHashMapCache.createAndMonitor(cacheSize, getter, cacheMonitor, "featureEvaluatorNrgStackCache");
	}

	@Override
	public double calc(ObjMask om) throws FeatureCalcException {
		try {
			return cache.get(om);
		} catch (GetOperationFailedException e) {
			throw new FeatureCalcException(e);
		}
	}

	public void clear() {
		cache.clear();
	}



}
