package org.anchoranalysis.image.feature.session;

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


import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.name.FeatureNameList;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.strategy.replace.CacheAndReuseStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.feature.objmask.pair.FeatureInputPairObjs;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;
import org.anchoranalysis.image.init.ImageInitParams;


/**
 * A particular type of feature-session where successive pairs of objects are evaluated by features in five different ways:
 * 
 * <div>
 * <ul>
 * the image in which the object exists (on {@link #listImage}) i.e. the nrg-stack.
 * the left-object in the pair (on {@link #listSingle})
 * the right-object in the pair (on {@link #listSingle})
 * the pair (on {@link #listPair})
 * both objects merged together (on {@link #listSingle}}
 * </ul>
 * </div>
 * 
 * <p>Due to the predictable pattern, feature-calculations can be cached predictably and appropriately to avoid redundancies</p>.
 * 
 * <div>
 * Two types of caching are applied to avoid redundant 
 * 
 * @author Owen Feehan
 *
 */
public class MergedPairsSession extends FeatureTableSession<FeatureInputPairObjs> {

	private boolean includeFirst;
	private boolean includeSecond;
	
	// Our sessions
	private FeatureCalculatorMulti<FeatureInputStack> sessionImage;
	
	// We avoid using seperate sessions for First and Second, as we want them
	//  to share the same Vertical-Cache for object calculation.
	private FeatureCalculatorMulti<FeatureInputSingleObj> sessionFirstSecond;
	private FeatureCalculatorMulti<FeatureInputSingleObj> sessionMerged;
	private FeatureCalculatorMulti<FeatureInputPairObjs> sessionPair;

	// The lists we need
	private MergedPairsFeatures features;
	private boolean checkInverse = false;
	
	// Prefixes that are ignored
	private Collection<String> ignoreFeaturePrefixes;
	
	private boolean suppressErrors;
	
	public MergedPairsSession(MergedPairsFeatures features) {
		this(false, false, features, Collections.emptySet(), false, true);
	}
		
	public MergedPairsSession(
		boolean includeFirst,
		boolean includeSecond,
		MergedPairsFeatures features,
		Collection<String> ignoreFeaturePrefixes,
		boolean checkInverse,
		boolean suppressErrors
	) {
		this.includeFirst = includeFirst;
		this.includeSecond = includeSecond;
		this.features = features;
		this.checkInverse = checkInverse;
		this.ignoreFeaturePrefixes = ignoreFeaturePrefixes;
		this.suppressErrors = suppressErrors;
	}
	
	
//	private SharedFeatureList createSharedFeatures( SharedObjectsFeature soFeature, FeatureList fl ) {
//		SharedFeatureList out = new SharedFeatureList();
//		out.add( soFeature.getSharedFeatureSet() );
//		fl.copyToCustomName(out.getSet(),false);
//		return out;
//	}
	
	@Override
	public void start(
		ImageInitParams soImage,
		SharedFeaturesInitParams soFeature,
		Optional<NRGStackWithParams> nrgStack,
		LogErrorReporter logErrorReporter
	) throws InitException {
		
		// We create our SharedFeatures including anything from the NamedDefinitions, and all our additional features
		// TODO fix
		//SharedFeatureSet<FeatureCalcParams> sharedFeatures = soFeature.getSharedFeatureSet();
		// sharedFeatures = createSharedFeatures(soFeature,listSingle);
		//listImage.copyToCustomName(sharedFeatures.getSet(),false);
		//listSingle.copyToCustomName(sharedFeatures.getSet(),false);
		//listPair.copyToCustomName(sharedFeatures.getSet(),false);
		
		
		// We create more caches for the includeFirst and includeSecond Features and merged features.
		
		CreateCalculatorHelper cc = new CreateCalculatorHelper(ignoreFeaturePrefixes, nrgStack,	logErrorReporter);
		
		sessionImage = features.createImageSession(cc, soImage, MergedPairsCachingStrategies.noCache(), suppressErrors);
		
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> cachingStrategyFirstSecond
			= MergedPairsCachingStrategies.cacheAndReuse();
		
		if (includeFirst || includeSecond) {
			sessionFirstSecond = features.createSingleSession(cc, soImage, cachingStrategyFirstSecond, suppressErrors);
		}
		
		BoundReplaceStrategy<FeatureInputSingleObj,CacheAndReuseStrategy<FeatureInputSingleObj>> cachingStrategyMerged
			= MergedPairsCachingStrategies.cacheAndReuse();
		sessionMerged = features.createSingleSession(cc, soImage, cachingStrategyMerged, suppressErrors);
				
		sessionPair = features.createPairSession(cc, soImage, cachingStrategyFirstSecond, cachingStrategyMerged);
	}
	
	@Override
	public ResultsVector calc(FeatureInputPairObjs input) throws FeatureCalcException {

		return calcForInputAndMaybeInverse(
			input,
			Optional.empty()
		);
	}

	@Override
	public ResultsVector calc(FeatureInputPairObjs input, FeatureList<FeatureInputPairObjs> featuresSubset)
			throws FeatureCalcException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ResultsVector calcSuppressErrors(FeatureInputPairObjs input, ErrorReporter errorReporter) {
		
		try {
			return calcForInputAndMaybeInverse(
				input,
				Optional.of(errorReporter)
			);
		} catch (FeatureCalcException e) {
			errorReporter.recordError(MergedPairsSession.class, e);
			assert(false);
			return null;
		}
	}
	
	public ResultsVector calcMaybeSuppressErrors(FeatureInputPairObjs input, ErrorReporter errorReporter) throws FeatureCalcException {
		if (suppressErrors) {
			return calcSuppressErrors(input, errorReporter);
		} else {
			return calc(input);
		}
	}
	

	@Override
	public FeatureNameList createFeatureNames() {
		FeatureNameList out = new FeatureNameList();
		
		out.addCustomNamesWithPrefix( "image.", features.getImage() );
		
		if (includeFirst) {
			out.addCustomNamesWithPrefix( "first.", features.getSingle() );
		}
		
		if (includeSecond) {
			out.addCustomNamesWithPrefix( "second.", features.getSingle() );
		}
				
		out.addCustomNamesWithPrefix( "merged.", features.getSingle() );
		
		out.addCustomNamesWithPrefix( "pair.", features.getPair() );
		
		return out;
	}
	
	@Override
	public int sizeFeatures() {
		
		// Number of times we use the listSingle
		int numSingle = 1 + integerFromBoolean(includeFirst) + integerFromBoolean(includeSecond);
				
		return features.numImageFeatures()
			+ features.numPairFeatures()
			+ (numSingle * features.numSingleFeatures());
	}
	
	@Override
	public FeatureTableSession<FeatureInputPairObjs> duplicateForNewThread() {
		return new MergedPairsSession(
			includeFirst,
			includeSecond,
			features.duplicate(),
			ignoreFeaturePrefixes,	// NOT DUPLICATED
			checkInverse,
			suppressErrors
		);
	}

	
	private ResultsVector calcForInputAndMaybeInverse(FeatureInputPairObjs input, Optional<ErrorReporter> errorReporter) throws FeatureCalcException {
		
		ResultsVector rv = calcForInput(input, errorReporter);
		
		if (checkInverse) {
			
			ResultsVector rvInverse = calcForInput(
				input.createInverse(),
				errorReporter
			);
			
			InverseChecker checker = new InverseChecker(
				includeFirst,
				includeSecond,
				features.numImageFeatures(),
				features.numSingleFeatures(),
				() -> createFeatureNames()
			);
			checker.checkInverseEqual(rv, rvInverse, input);
		}
		
		return rv;
	}
	
	
	private ResultsVector calcForInput(FeatureInputPairObjs input, Optional<ErrorReporter> errorReporter) throws FeatureCalcException {
		
		ResultsVectorBuilder helper = new ResultsVectorBuilder(
			sizeFeatures(),
			errorReporter
		);
		
		// First we calculate the Image features (we rely on the NRG stack being added by the calculator)
		
		// TODO these are identical and do not need to be repeatedly calculated
		helper.calcAndInsert(new FeatureInputStack(), sessionImage );
		
		// First features
		if (includeFirst) {
			helper.calcAndInsert( input, FeatureInputPairObjs::getFirst, sessionFirstSecond );
		}
		
		// Second features
		if (includeSecond) {
			helper.calcAndInsert( input, FeatureInputPairObjs::getSecond, sessionFirstSecond );
		}
		
		// Merged. Because we know we have FeatureObjMaskPairMergedParams, we don't need to change params
		helper.calcAndInsert(input, FeatureInputPairObjs::getMerged, sessionMerged );

		// Pair features
		helper.calcAndInsert(input, sessionPair );
		
		assert(helper.getResultsVector().hasNoNulls());
		return helper.getResultsVector();
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