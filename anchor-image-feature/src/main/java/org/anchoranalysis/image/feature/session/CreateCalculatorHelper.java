package org.anchoranalysis.image.feature.session;

import java.util.Arrays;

/*-
 * #%L
 * anchor-plugin-mpp-experiment
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Supplier;

import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorCachedResults;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMultiChangeInput;
import org.anchoranalysis.feature.session.strategy.child.CheckCacheForSpecificChildren;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReuseSingletonStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.feature.objmask.pair.FeatureDeriveFromPair;
import org.anchoranalysis.image.init.ImageInitParams;

class CreateCalculatorHelper {

	// Prefixes that are ignored
	private Collection<String> ignoreFeaturePrefixes;
	private Optional<NRGStackWithParams> nrgStack;
	private LogErrorReporter logErrorReporter;
	
	public CreateCalculatorHelper(
		Collection<String> ignoreFeaturePrefixes,
		Optional<NRGStackWithParams> nrgStack,
		LogErrorReporter logErrorReporter
	) {
		super();
		this.ignoreFeaturePrefixes = ignoreFeaturePrefixes;
		this.nrgStack = nrgStack;
		this.logErrorReporter = logErrorReporter;
	}	
		
	public <T extends FeatureInputNRGStack> FeatureCalculatorMulti<T> createCached(
		FeatureList<T> features,
		ImageInitParams soImage,
		BoundReplaceStrategy<T,? extends ReplaceStrategy<T>> replacePolicyFactory,
		boolean suppressErrors
	) throws InitException {
		
		return wrapWithNrg( 
			new FeatureCalculatorCachedResults<>(
				createWithoutNrg(features, soImage, replacePolicyFactory ),
				suppressErrors
			)
		);		
	}
	
	/**
	 * Create a pair-calculator. We want to substitute existing caches where they exist for specific sub-caches of Pair features
	 * 
	 * <p>This is to reduce calculation, as they've already been calculated for the "single" features</p>.
	 * 
	 * @param <T>
	 * @param features
	 * @param soImage
	 * @return
	 * @throws InitException
	 */
	public <T extends FeatureInputNRGStack> FeatureCalculatorMulti<T> createPair(
			FeatureList<T> features,
			ImageInitParams soImage,
			Supplier<LRUCache<
				FeatureInputSingleObj,SessionInput<FeatureInputSingleObj>
			>> replaceStrategyFirstAndSecond,
			Supplier<LRUCache<
				FeatureInputSingleObj,SessionInput<FeatureInputSingleObj>
			>> replaceStrategyMerged
		) throws InitException {
		
		CheckCacheForSpecificChildren.Source<FeatureInputSingleObj> srcFirstSecond = new CheckCacheForSpecificChildren.Source<>(
			replaceStrategyFirstAndSecond,
			new HashSet<>(Arrays.asList(FeatureDeriveFromPair.CACHE_NAME_FIRST, FeatureDeriveFromPair.CACHE_NAME_SECOND))
		);
		
		CheckCacheForSpecificChildren.Source<FeatureInputSingleObj> srcMerged = new CheckCacheForSpecificChildren.Source<>(
			replaceStrategyMerged,
			new HashSet<>(Arrays.asList(FeatureDeriveFromPair.CACHE_NAME_MERGED))
		);
		
		BoundReplaceStrategy<T,ReplaceStrategy<T>> replaceStrategy =
			new	BoundReplaceStrategy<>(
				cacheCreator -> new ReuseSingletonStrategy<>(
					cacheCreator,
					new CheckCacheForSpecificChildren<>(
						FeatureInputSingleObj.class,
						Arrays.asList(srcFirstSecond, srcMerged)
					)
				)
			);
	
		return wrapWithNrg(
			createWithoutNrg(features, soImage, replaceStrategy)
		);		
	}
	
	public <T extends FeatureInputNRGStack> FeatureCalculatorMulti<T> create(
		FeatureList<T> features,
		ImageInitParams soImage,
		BoundReplaceStrategy<T,? extends ReplaceStrategy<T>> replacePolicyFactory
	) throws InitException {
		return wrapWithNrg(
			createWithoutNrg(features, soImage, replacePolicyFactory)
		);		
	}
	
	private <T extends FeatureInputNRGStack> FeatureCalculatorMulti<T> createWithoutNrg(
		FeatureList<T> features,
		ImageInitParams soImage,
		BoundReplaceStrategy<T,? extends ReplaceStrategy<T>> replacePolicyFactory
	) throws InitException {

		try {
			return FeatureSession.with(
				features,
				createInitParams(soImage),
				new SharedFeatureSet<>(),
				logErrorReporter,
				ignoreFeaturePrefixes,
				replacePolicyFactory
			);
			
		} catch (FeatureCalcException e) {
			throw new InitException(e);
		}
	}
	
	/** Ensures any input-parameters have the NRG-stack attached */
	private <T extends FeatureInputNRGStack> FeatureCalculatorMulti<T> wrapWithNrg(
		FeatureCalculatorMulti<T> calculator
	) {
		return new FeatureCalculatorMultiChangeInput<T>(
			calculator,
			params->params.setNrgStack(nrgStack)
		);
	}
	
	private FeatureInitParams createInitParams(ImageInitParams soImage) {
		return InitParamsHelper.createInitParams(
			soImage,
			nrgStack
		);
	}
}
