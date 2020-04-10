package org.anchoranalysis.feature.session;

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


import java.util.Collection;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.apache.commons.collections.CollectionUtils;

/**
 * Remembers the first-calculation and always returns this value for all subsequent calculations
 * 
 * @author Owen Feehan
 *
 */
public class SequentialSessionRepeatFirst<T extends FeatureCalcParams> extends FeatureSession implements ISequentialSessionSingleParams<T> {

	private SequentialSession<T> delegate;
	
	private ResultsVector rv = null;
	
	@SuppressWarnings("unchecked")
	public SequentialSessionRepeatFirst(FeatureList<T> listFeatures) {
		this( listFeatures, (Collection<String>) CollectionUtils.EMPTY_COLLECTION );
	}
	
	public SequentialSessionRepeatFirst(FeatureList<T> listFeatures, Collection<String> ignoreFeaturePrefixes ) {
		super();
		this.delegate = new SequentialSession<>(listFeatures, ignoreFeaturePrefixes);
	}

	@Override
	public void start(FeatureInitParams featureInitParams, SharedFeatureSet<T> sharedFeatureList, LogErrorReporter logger) throws InitException {
		delegate.start(featureInitParams, sharedFeatureList, logger);
	}

	@Override
	public ResultsVector calcSuppressErrors(T params,
			ErrorReporter errorReporter) {
		if (rv==null) {
			rv = delegate.calcSuppressErrors(params, errorReporter);
		}
		return rv;
	}

	@Override
	public ResultsVector calc(T params)
			throws FeatureCalcException {
		return delegate.calc(params);
	}

}
