package org.anchoranalysis.feature.session.cache;

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


import org.anchoranalysis.core.error.OperationFailedRuntimeException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class NullCache<T extends FeatureCalcParams> extends FeatureSessionCache<T> {

	private NullCacheRetriever<T> retriever;
	
	public NullCache( SharedFeatureSet<T> sharedFeatures ) {
		retriever = new NullCacheRetriever<>( sharedFeatures );
	}
	
	@Override
	public void invalidate() {
	}

	@Override
	public void init(FeatureInitParams featureInitParams, LogErrorReporter logger, boolean logCacheInit) {

	}

	@Override
	public FeatureSessionCacheRetriever<T> retriever() {
		return retriever;
	}

	@Override
	public void assignResult(FeatureSessionCache<T> other) {
		throw new OperationFailedRuntimeException("Operation not supported for this type of cache");
	}

	@Override
	public FeatureSessionCache<T> duplicate() {
		return new NullCache<>( retriever.getSharedFeatureList() );
	}
}