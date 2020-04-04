package org.anchoranalysis.feature.bean;

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

import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheRetriever;

public abstract class FeatureCastInitParams<CastInitParamsType extends FeatureInitParams> extends Feature {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Class<?> castInitParamsType;
	
	protected FeatureCastInitParams( Class<?> castInitParamsType ) {
		this.castInitParamsType = castInitParamsType;
	}
	
	protected FeatureCastInitParams( Class<?> castInitParamsType, PropertyInitializer<FeatureInitParams> initializer ) {
		super(initializer);
		this.castInitParamsType = castInitParamsType;
	}
	
	// DUMMY METHOD that can be overrridden
	public void beforeCalcCast( CastInitParamsType params, FeatureSessionCacheRetriever session ) throws InitException {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(CacheableParams<FeatureInitParams> params, FeatureBase parentFeature, LogErrorReporter logger) throws InitException {
		assert( logger!=null );
		if (castInitParamsType.isAssignableFrom(params.getClass())) {
			CastInitParamsType paramsCast = (CastInitParamsType) params.getParams();
			super.init(params, parentFeature, logger);
			beforeCalcCast( paramsCast, params.getCacheSession() );
		} else {
			throw new InitException(
				String.format("Requires %s", castInitParamsType.getName())
			);
		}
	}
}
