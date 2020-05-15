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
import java.util.List;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.session.strategy.replace.ReplaceStrategy;
import org.anchoranalysis.feature.session.strategy.replace.ReuseSingletonStrategy;
import org.anchoranalysis.feature.session.strategy.replace.bind.BoundReplaceStrategy;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.apache.commons.collections.CollectionUtils;

/**
 * Calculates features with successively different parameters, taking care of caching etc. appropriately.
 * 
 * <p>All feature use the same InitParams, but successively different {#FeatureCalcParams} sequentially.</p>
 * 
 * <p>Caching is applied only within each call to {{@link #calc(FeatureInput)} but among successive calls</p>.
 * 
 * TODO after MergeSession is fixed, considering making all constructors package-private and renaming back to SequentialSession
 * 
 * @author Owen Feehan
 * @param T input-type for feature
 *
 */
public class SequentialSession<T extends FeatureInput> implements FeatureCalculatorMulti<T> {

	private final static String ERROR_NOT_STARTED = "Session has not been started yet. Call start().";
	
	private FeatureList<T> listFeatures;
	
	private boolean isStarted = false;
	
	// Should feature calculation errors be printed to the console?
	private boolean reportErrors = false;
	
	private ReplaceStrategy<T> replaceSession;
	
	private BoundReplaceStrategy<T,? extends ReplaceStrategy<T>> replacePolicyFactory;
			
	/**
	 * Constructor of a session
	 * 
	 * @param feature the feature that will be calculated in the session
	 */
	public SequentialSession(Feature<T> feature) {
		this( new FeatureList<>(feature) );
	}
	
	/**
	 * Constructor of a session
	 * 
	 * @param listFeatures the features that will be calculated in this session
	 */
	@SuppressWarnings("unchecked")
	SequentialSession(Iterable<Feature<T>> iterFeatures) {
		this(
			iterFeatures,
			(Collection<String>) CollectionUtils.EMPTY_COLLECTION,
			new BoundReplaceStrategy<>(
				cacheCreator -> new ReuseSingletonStrategy<>(cacheCreator)
			)
		);
	}
	
	/**
	 * Constructor of a session
	 * 
	 * @param listFeatures the features that will be calculated in this session
	 * @param prependFeatureName a string that can be prepended to feature-ID references e.g. when looking for  featureX,  concat(prependFeatureName,featureX)
	 *         is also considered. This helps with scoping.
	 */
	SequentialSession(Iterable<Feature<T>> iterFeatures, Collection<String> ignorePrefixes, BoundReplaceStrategy<T,? extends ReplaceStrategy<T>> replacePolicyFactory) {
		this.replacePolicyFactory = replacePolicyFactory;
		this.listFeatures = new FeatureList<>(iterFeatures);
		assert(listFeatures!=null);
	}
	
	/**
	 * Starts the session
	 * 
	 * @param featureInitParams		The parameters used to initialise the feature
	 * @param logger				Logger
	 * @param sharedFeatures		A list of features that are shared between the features we are calculating (and thus also init-ed)
	 * @throws InitException
	 */
	public void start( FeatureInitParams featureInitParams, SharedFeatureMulti sharedFeatures, LogErrorReporter logger ) throws InitException{
		
		if (isStarted) {
			throw new InitException("Session has already been started.");
		}
		
		checkNoIntersectionWithSharedFeatures( sharedFeatures );
		
		assert( logger!=null );
		
		setupCacheAndInit( featureInitParams, sharedFeatures, logger );
		
		isStarted = true;
	}
	
	/**
	 * Calculates one params for each feature.
	 * 
	 * @param params
	 * @return
	 * @throws FeatureCalcException
	 */
	@Override
	public ResultsVector calc( T params ) throws FeatureCalcException {
		checkIsStarted();
		return calcCommonExceptionAsVector(params);
	}

	

	@Override
	public ResultsVector calc(T params, FeatureList<T> featuresSubset) throws FeatureCalcException {
		checkIsStarted();
	
		return replaceSession.createOrReuse(
			params
		).calc(featuresSubset);
	}
	
	/**
	 * Calculates the next-object in our sequential series, reporting any exceptions into a reporter log
	 * 
	 * @param params
	 * @return
	 * @throws FeatureCalcException
	 */
	public ResultsVector calcSuppressErrors( T params, ErrorReporter errorReporter ) {
		
		ResultsVector res = new ResultsVector( listFeatures.size() );
		
		if (!isStarted) {
			errorReporter.recordError(SequentialSession.class, ERROR_NOT_STARTED);
			res.setErrorAll( new OperationFailedException(ERROR_NOT_STARTED) );
		} else {
			calcCommonSuppressErrors(res, params, errorReporter);
		}
		
		return res;
	}
	
	public boolean hasSingleFeature() {
		return listFeatures.size()==1;
	}

	public List<Feature<T>> getFeatureList() {
		return listFeatures.getList();
	}
	
	@Override
	public int sizeFeatures() {
		return listFeatures.size();
	}
		
	private void calcCommonSuppressErrors( ResultsVector res, T params, ErrorReporter errorReporter ) {
		
		// Create cacheable params, and record any errors for all features
		SessionInput<T> cacheableParams;
		try {
			cacheableParams = replaceSession.createOrReuse(params);
		} catch (FeatureCalcException e) {
			// Return all features as errored
			if (reportErrors) {
				errorReporter.recordError(SequentialSession.class, e);
			}
			for( int i=0; i<listFeatures.size(); i++) {
				res.setError(i, e);
			}
			return;
		} 
		
		// Perform individual calculations on each feature
		for( int i=0; i<listFeatures.size(); i++) {
			Feature<T> f = listFeatures.get(i);
			
			try {
				res.set(
					i,
					cacheableParams.calc(f)
				);
				
			} catch (FeatureCalcException e) {
				if (reportErrors) {
					errorReporter.recordError(SequentialSession.class, e);
				}
				res.setError(i, e);
			}
		}	
	}
	
	private ResultsVector calcCommonExceptionAsVector( T input ) throws FeatureCalcException {
		ResultsVector res = new ResultsVector( listFeatures.size() );

		SessionInput<T> sessionInput = replaceSession.createOrReuse(input); 
		
		for( int i=0; i<listFeatures.size(); i++) {
			Feature<T> f = listFeatures.get(i);
			
			double val = sessionInput.calc(f);
			res.set(i,val);
		}
		
		return res;
	}
	
	/**
	 * Checks that there's no common features in the featureList and the shared-features as this can create
	 *   complications with initialisation of caches (recursive initializations)
	 * 
	 * @param sharedFeatures
	 * @throws InitException
	 */
	private void checkNoIntersectionWithSharedFeatures( SharedFeatureMulti sharedFeatures ) throws InitException {
		assert(listFeatures!=null);
		try {
			for( Feature<T> f : listFeatures ) {
				
				FeatureList<FeatureInput> allDependents = f.createListChildFeatures(false);
				
				for( Feature<FeatureInput> dep : allDependents ) {
				
					if (sharedFeatures.contains(dep)) {
						throw new InitException(
							String.format("Feature '%s' is found in both the session and in the SharedFeatures", dep.getFriendlyName() )
						);
					}
				}
			}
			
		} catch (BeanMisconfiguredException e) {
			throw new InitException(e);
		}
	}
		
	private void setupCacheAndInit( FeatureInitParams featureInitParams, SharedFeatureMulti sharedFeatures, LogErrorReporter logger ) throws InitException {
		assert(featureInitParams!=null);
		FeatureInitParams featureInitParamsDup = featureInitParams.duplicate();
		listFeatures.initRecursive(featureInitParamsDup, logger);
		
		replaceSession = replacePolicyFactory.bind(listFeatures, featureInitParamsDup, sharedFeatures, logger);
	}
	
	private void checkIsStarted() throws FeatureCalcException {
		if (!isStarted) {
			throw new FeatureCalcException(ERROR_NOT_STARTED);
		}
	}
}
