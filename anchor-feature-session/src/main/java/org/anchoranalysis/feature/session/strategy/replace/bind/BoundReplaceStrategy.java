package org.anchoranalysis.feature.session.strategy.replace.bind;

import java.util.function.Function;

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
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.creator.CacheCreatorSimple;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

/** 
 * Attaches a replacement-strategy to a session lazily (i.e. when it is needed)
 * 
 * <p>This is because as the relevant parameters are not available when we need to call the constructor</op>.
 **/
public class BoundReplaceStrategy<T extends FeatureInput, S extends ReplaceStrategy<T>> {

	private S strategy;
	
	private Function<CacheCreator,S> funcCreateStrategy;
	
	public BoundReplaceStrategy(Function<CacheCreator, S> funcCreateStrategy) {
		super();
		this.funcCreateStrategy = funcCreateStrategy;
	}	
	
	public ReplaceStrategy<T> bind(
		FeatureList<T> featureList,
		FeatureInitParams featureInitParams,
		SharedFeatureSet<T> sharedFeatures, 
		LogErrorReporter logger
	) {
		CacheCreator cacheCreator = new CacheCreatorSimple(featureList, sharedFeatures, featureInitParams, logger);
		if (strategy==null) {
			// TODO this is a hack for multiple reuse
			//strategy = new CacheAndReuseStrategy<>(cacheCreator);
			strategy = funcCreateStrategy.apply(cacheCreator);
		}
		return strategy;
	}

	public S getStrategy() {
		return strategy;
	}
}