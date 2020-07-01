package org.anchoranalysis.image.feature.session.merged;



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
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.session.FeatureTableCalculator;


/**
 * A particular type of feature-session where successive pairs of objects are evaluated by features in five different ways:
 * 
 * <div>
 * <ul>
 * <li>the image in which the object exists (on {@link #listImage}) i.e. the nrg-stack.</li>
 * <li>the left-object in the pair (on {@link #listSingle})</li>
 * <li>the right-object in the pair (on {@link #listSingle})</li>
 * <li>the pair (on {@link #listPair})</li>
 * <li>both objects merged together (on {@link #listSingle}}</li>
 * </ul>
 * </div>
 * 
 * <p>Due to the predictable pattern, feature-calculations can be cached predictably and appropriately to avoid redundancies</p>.
 * 
 * <div>
 * Two types of caching are applied to avoid redundancy
 * <li>The internal calculation caches of first/second/merged are reused as the internal calculation sub-caches in pair.</li>
 * <li>The entire results are cached (as a function of the input) for first/second, as the same inputs reappear multiple times.</li>
 * </div>
 * 
 * <p>TODO a further type of caching (of the results of internal feature calculations for the same inputs)</p>
 * 
 * @author Owen Feehan
 *
 */
public class FeatureCalculatorMergedPairs extends FeatureTableCalculator<FeatureInputPairObjects> {
		
	private CombinedCalculator calculator;

	// The lists we need
	private MergedPairsFeatures features;
	private MergedPairsInclude include;
	
	// Prefixes that are ignored
	private Collection<String> ignoreFeaturePrefixes;
	
	private boolean suppressErrors;
	
	public FeatureCalculatorMergedPairs(MergedPairsFeatures features) {
		this(
			features,
			new MergedPairsInclude(),
			Collections.emptySet(),
			true
		);
	}
		
	public FeatureCalculatorMergedPairs(
		MergedPairsFeatures features,
		MergedPairsInclude include,
		Collection<String> ignoreFeaturePrefixes,
		boolean suppressErrors
	) {
		this.include = include;
		this.features = features;
		this.ignoreFeaturePrefixes = ignoreFeaturePrefixes;
		this.suppressErrors = suppressErrors;
	}
	
	@Override
	public void start(
		ImageInitParams soImage,
		Optional<NRGStackWithParams> nrgStack,
		LogErrorReporter logErrorReporter
	) throws InitException {
		
		calculator = new CombinedCalculator(
			features,
			new CreateCalculatorHelper(ignoreFeaturePrefixes, nrgStack,	logErrorReporter),
			include,			
			soImage,
			suppressErrors
		);
	}
	
	@Override
	public ResultsVector calc(FeatureInputPairObjects input) throws FeatureCalcException {
		return calculator.calcForInput(
			input,
			Optional.empty()
		);
	}

	@Override
	public ResultsVector calc(FeatureInputPairObjects input, FeatureList<FeatureInputPairObjects> featuresSubset)
			throws FeatureCalcException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ResultsVector calcSuppressErrors(FeatureInputPairObjects input, ErrorReporter errorReporter) {
		
		try {
			return calculator.calcForInput(
				input,
				Optional.of(errorReporter)
			);
		} catch (FeatureCalcException e) {
			errorReporter.recordError(FeatureCalculatorMergedPairs.class, e);
			
			ResultsVector rv = new ResultsVector( sizeFeatures() );
			rv.setErrorAll(e);
			return rv;
		}
	}
	
	public ResultsVector calcMaybeSuppressErrors(FeatureInputPairObjects input, ErrorReporter errorReporter) throws FeatureCalcException {
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
		
		if (include.includeFirst()) {
			out.addCustomNamesWithPrefix( "first.", features.getSingle() );
		}
		
		if (include.includeSecond()) {
			out.addCustomNamesWithPrefix( "second.", features.getSingle() );
		}
				
		if (include.includeMerged()) {
			out.addCustomNamesWithPrefix( "merged.", features.getSingle() );
		}
		
		out.addCustomNamesWithPrefix( "pair.", features.getPair() );
		
		return out;
	}
	
	@Override
	public int sizeFeatures() {
		return calculator.sizeFeatures();
	}
	
	@Override
	public FeatureTableCalculator<FeatureInputPairObjects> duplicateForNewThread() {
		return new FeatureCalculatorMergedPairs(
			features.duplicate(),
			include,
			ignoreFeaturePrefixes,	// NOT DUPLICATED
			suppressErrors
		);
	}
}