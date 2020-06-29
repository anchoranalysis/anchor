package org.anchoranalysis.image.feature.session.merged;

import java.util.Optional;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.feature.objmask.pair.FeatureInputPairObjs;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;

/**
 * Calculates a result for merged-pairs based upon combining the calculations from several calculators
 * @author Owen Feehan
 *
 */
class CombinedCalculator {

	private MergedPairsFeatures features;
	private CreateCalculatorHelper cc;
	private boolean suppressErrors;
	private MergedPairsInclude include;
	
	private FeatureCalculatorMulti<FeatureInputStack> calculatorImage;
	
	/**
	 * For calculating first and second single objects
	 * 
	 * We avoid using separate sessions for First and Second, as we want them
	 *  to share the same Vertical-Cache for object calculation.
	 * 
	 * <p>Can be null, if neither the first or second features are included</p>
	 */
	private FeatureCalculatorMulti<FeatureInputSingleObj> calculatorFirstSecond;

	/**
	 * For calculating merged objects
	 * 
	 * <p>Can be null, if neither the merged are not included</p>
	 */
	private FeatureCalculatorMulti<FeatureInputSingleObj> calculatorMerged;
		
	/**
	 * For calculating pair objects
	 * 
	 * <p>Can be null, if neither the pair are not included</p>
	 */
	private FeatureCalculatorMulti<FeatureInputPairObjs> calculatorPair;
	
	public CombinedCalculator(MergedPairsFeatures features, CreateCalculatorHelper cc, MergedPairsInclude include, ImageInitParams soImage, boolean suppressErrors) throws InitException {
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
		helper.calcAndInsert(new FeatureInputStack(), calculatorImage );
		
		// First features
		if (include.includeFirst()) {
			helper.calcAndInsert( input, FeatureInputPairObjs::getFirst, calculatorFirstSecond );
		}
		
		// Second features
		if (include.includeSecond()) {
			helper.calcAndInsert( input, FeatureInputPairObjs::getSecond, calculatorFirstSecond );
		}
		
		// Merged. Because we know we have FeatureObjMaskPairMergedParams, we don't need to change params
		if (include.includeMerged()) {
			helper.calcAndInsert(input, FeatureInputPairObjs::getMerged, calculatorMerged );
		}

		// Pair features
		helper.calcAndInsert(input, calculatorPair );
		
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
		calculatorImage = features.createImageSession(cc, soImage, CachingStrategies.noCache(), suppressErrors);
	}
	
	private void createFirstAndSecond(
		ImageInitParams soImage,
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> cachingStrategyFirstSecond		
	) throws InitException {
		if (include.includeFirstOrSecond()) {
			calculatorFirstSecond = features.createSingle(cc, soImage, cachingStrategyFirstSecond, suppressErrors);
		}		
	}
	
	private void createMergedAndPair(
		ImageInitParams soImage,
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> cachingStrategyFirstSecond
	) throws InitException {
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> cachingStrategyMerged
			= CachingStrategies.cacheAndReuse();
	
		if (include.includeMerged()) {
			calculatorMerged = features.createSingle(cc, soImage, cachingStrategyMerged, suppressErrors);
		}
				
		calculatorPair = features.createPair(
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
