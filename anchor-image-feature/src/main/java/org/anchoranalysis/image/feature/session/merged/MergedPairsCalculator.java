package org.anchoranalysis.image.feature.session.merged;

import java.util.Optional;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.feature.objmask.pair.FeatureInputPairObjs;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;
import org.anchoranalysis.image.init.ImageInitParams;

class MergedPairsCalculator {

	private MergedPairsFeatures features;
	private CreateCalculatorHelper cc;
	private boolean suppressErrors;
	private MergedPairsInclude include;
	
	private FeatureCalculatorMulti<FeatureInputStack> sessionImage;
	
	/**
	 * Session for calculating first and second single objects
	 * 
	 * We avoid using separate sessions for First and Second, as we want them
	 *  to share the same Vertical-Cache for object calculation.
	 * 
	 * <p>Can be null, if neither the first or second features are included</p>
	 */
	private FeatureCalculatorMulti<FeatureInputSingleObj> sessionFirstSecond;

	/**
	 * Session for calculating merged objects
	 * 
	 * <p>Can be null, if neither the merged are not included</p>
	 */
	private FeatureCalculatorMulti<FeatureInputSingleObj> sessionMerged;
		
	/**
	 * Session for calculating pair objects
	 * 
	 * <p>Can be null, if neither the pair are not included</p>
	 */
	private FeatureCalculatorMulti<FeatureInputPairObjs> sessionPair;
	
	public MergedPairsCalculator(MergedPairsFeatures features, CreateCalculatorHelper cc, MergedPairsInclude include, ImageInitParams soImage, boolean suppressErrors) throws InitException {
		super();
		this.cc = cc;
		this.features = features;
		this.suppressErrors = suppressErrors;
		this.include = include;
		
		createImage(soImage);
		
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> cachingStrategyFirstSecond
			= CachingStrategies.cacheAndReuse();
		
		createFirstAndSecond(soImage, cachingStrategyFirstSecond);
		createMergedAndPair(soImage, cachingStrategyFirstSecond);
	}
		
	public ResultsVector calcForInput(FeatureInputPairObjs input, Optional<ErrorReporter> errorReporter) throws FeatureCalcException {
		
		ResultsVectorBuilder helper = new ResultsVectorBuilder(
			sizeFeatures(),
			errorReporter
		);
		
		// First we calculate the Image features (we rely on the NRG stack being added by the calculator)
		
		// TODO these are identical and do not need to be repeatedly calculated
		helper.calcAndInsert(new FeatureInputStack(), sessionImage );
		
		// First features
		if (include.includeFirst()) {
			helper.calcAndInsert( input, FeatureInputPairObjs::getFirst, sessionFirstSecond );
		}
		
		// Second features
		if (include.includeSecond()) {
			helper.calcAndInsert( input, FeatureInputPairObjs::getSecond, sessionFirstSecond );
		}
		
		// Merged. Because we know we have FeatureObjMaskPairMergedParams, we don't need to change params
		if (include.includeMerged()) {
			helper.calcAndInsert(input, FeatureInputPairObjs::getMerged, sessionMerged );
		}

		// Pair features
		helper.calcAndInsert(input, sessionPair );
		
		assert(helper.getResultsVector().hasNoNulls());
		return helper.getResultsVector();
	}
	
	
	public int sizeFeatures() {
		
		// Number of times we use the listSingle
		int numSingle = (
			1 
			+ integerFromBoolean( include.includeFirst() )
			+ integerFromBoolean( include.includeSecond() )
		);
				
		return (
			features.numImageFeatures()
			+ features.numPairFeatures()
			+ (numSingle * features.numSingleFeatures())
		);
	}
	
	private void createImage(ImageInitParams soImage) throws InitException {
		sessionImage = features.createImageSession(cc, soImage, CachingStrategies.noCache(), suppressErrors);
	}
	
	private void createFirstAndSecond(
		ImageInitParams soImage,
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> cachingStrategyFirstSecond		
	) throws InitException {
		if (include.includeFirstOrSecond()) {
			sessionFirstSecond = features.createSingleSession(cc, soImage, cachingStrategyFirstSecond, suppressErrors);
		}		
	}
	
	private void createMergedAndPair(
		ImageInitParams soImage,
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> cachingStrategyFirstSecond
	) throws InitException {
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> cachingStrategyMerged
			= CachingStrategies.cacheAndReuse();
	
		if (include.includeMerged()) {
			sessionMerged = features.createSingleSession(cc, soImage, cachingStrategyMerged, suppressErrors);
		}
				
		sessionPair = features.createPairSession(
			cc,
			soImage,
			TransferSourceHelper.createTransferSource(
				cachingStrategyFirstSecond,
				cachingStrategyMerged
			)
		);		
	}
		
	/**
	 * Integer value from boolean
	 * 
	 * @param b
	 * @return 0 for FALSE, 1 for TRUE
	 */
	private static int integerFromBoolean( boolean b ) {
		return b ? 1 : 0;
	}
}
