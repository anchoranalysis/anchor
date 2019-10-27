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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheFactory;
import org.anchoranalysis.feature.session.cache.HorizontalCalculationCacheFactory;
import org.anchoranalysis.feature.session.cache.HorizontalFeatureCacheFactory;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.apache.commons.collections.CollectionUtils;

/**
 * Applies the same Params to several features.
 * 
 * All feature use the same InitParams;
 * 
 * Object calculations occur sequentially thereafter.
 * 
 * @author Owen Feehan
 *
 */
public class SequentialSession extends FeatureSession implements ISequentialSessionSingleParams {

	private FeatureList listFeatures;

	// Our main cache which does our processing (and contains potentially additional caches)
	private CachePlus cache;
	
	private boolean isStarted = false;
	
	// Should feature calculation errors be printed to the console?
	private boolean reportErrors = false;
	
	private FeatureSessionCacheFactory cacheFactory;
	
	/**
	 * Constructor of a session
	 * 
	 * @param feature the feature that will be calculated in the session
	 */
	public SequentialSession(Feature feature) {
		this( new FeatureList(feature) );
	}
	
	/**
	 * Constructor of a session
	 * 
	 * @param listFeatures the features that will be calculated in this session
	 */
	@SuppressWarnings("unchecked")
	public SequentialSession(FeatureList listFeatures) {
		this( listFeatures, (Collection<String>) CollectionUtils.EMPTY_COLLECTION );
	}
	
	/**
	 * Constructor of a session
	 * 
	 * @param listFeatures the features that will be calculated in this session
	 * @param prependFeatureName a string that can be prepended to feature-ID references e.g. when looking for  featureX,  concat(prependFeatureName,featureX)
	 *         is also considered. This helps with scoping.
	 */
	public SequentialSession(FeatureList listFeatures, Collection<String> ignorePrefixes) {
		assert(listFeatures!=null);
		this.listFeatures = listFeatures;
		this.cacheFactory = new HorizontalFeatureCacheFactory( new HorizontalCalculationCacheFactory(), ignorePrefixes );
	}
	
	
	
	/**
	 * Starts the session
	 * 
	 * @param featureInitParams		The parameters used to initialise the feature
	 * @param logger				Logger
	 * @param sharedFeatures		A list of features that are shared between the features we are calculating (and thus also init-ed)
	 * @throws InitException
	 */
	@Override
	public void start( FeatureInitParams featureInitParams, SharedFeatureSet sharedFeatures, LogErrorReporter logger ) throws InitException{
		
		if (isStarted) {
			throw new InitException("Session has already been started.");
		}
		
		checkNoIntersectionWithSharedFeatures( sharedFeatures );
		
		assert( logger!=null );
		
		this.cache = setupCacheAndInit( featureInitParams, sharedFeatures, logger );
		
		isStarted = true;
	}
	
	/**
	 * Calculates the next-object in our sequential series
	 * 
	 * @param params
	 * @return
	 * @throws FeatureCalcException
	 */
	public ResultsVector calc( FeatureCalcParams params ) throws FeatureCalcException {
		
		if (!isStarted) {
			throw new FeatureCalcException("Session has not been started yet. Call start().");
		}
		
		cache.invalidate();
		
		ResultsVector res = new ResultsVector( listFeatures.size() );
				
		for( int i=0; i<listFeatures.size(); i++) {
			Feature f = listFeatures.get(i);
			double val = cache.retriever().calc(f, params);
			res.set(i,val);
		}
		return res;
	}
	
	
	
	/**
	 * Calculates the next-object in our sequential series
	 * 
	 * @param params
	 * @return
	 * @throws FeatureCalcException
	 */
	public ResultsVector calc( List<CreateParams> listCreateParams ) throws FeatureCalcException {
		
		if (!isStarted) {
			throw new FeatureCalcException("Session has not been started yet. Call start().");
		}
		
		cache.invalidate();
		
		checkSizesMatch( listCreateParams, listFeatures );
		return calcException( listCreateParams );
	}
	
	/**
	 * Calculates the next-object in our sequential series, reporting any exceptions into a reporter log
	 * 
	 * @param params
	 * @return
	 * @throws FeatureCalcException
	 */
	public ResultsVector calcSuppressErrors( FeatureCalcParams params, ErrorReporter errorReporter ) {
		
		ResultsVector res = new ResultsVector( listFeatures.size() );
		
		if (!isStarted) {
			String errorMsg = "Session has not been started yet. Call start().";
			errorReporter.recordError(SequentialSession.class, errorMsg);
			res.setErrorAll( new OperationFailedException(errorMsg) );
		}
		
		cache.invalidate();
		
		calcSuppressErrors(res, params, errorReporter);
		
		return res;
	}
	
	/**
	 * Resets cache, and creates a new sub-session (for calculating particular sub-features)
	 * 
	 * @return
	 * @throws FeatureCalcException 
	 */
	public Subsession createSubsession() throws CreateException {
		
		if (!isStarted) {
			throw new CreateException("Session has not been started yet. Call start().");
		}
		
		cache.invalidate();
		
		return new Subsession(cache);
	}
	
	
	public boolean hasSingleFeature() {
		return listFeatures.size()==1;
	}
	
	public int numFeatures() {
		return listFeatures.size();
	}

	public List<Feature> getFeatureList() {
		return listFeatures.getList();
	}

	@Override
	public CachePlus getCache() {
		return cache;
	}
	
	private void calcSuppressErrors( ResultsVector res, FeatureCalcParams params, ErrorReporter errorReporter ) {
		for( int i=0; i<listFeatures.size(); i++) {
			Feature f = listFeatures.get(i);
			
			try {
				calcThroughCache( f, res, i, params );
				
			} catch (FeatureCalcException e) {
				if (reportErrors) {
					errorReporter.recordError(SequentialSession.class, e);
				}
				res.setError(i, e);
			}
		}	
	}

	private ResultsVector calcException( List<CreateParams> listCreateParams ) throws FeatureCalcException {
		ResultsVector res = new ResultsVector( listFeatures.size() );
		
		try {
			for( int i=0; i<listFeatures.size(); i++) {
				
				Feature f = listFeatures.get(i);
				FeatureCalcParams params = listCreateParams.get(i).createForFeature(f);
				
				calcThroughCache( f, res, i, params );
			}
			return res;
			
		} catch (CreateException e) {
			throw new FeatureCalcException(e);
		}		
	}
	
	private void calcThroughCache( Feature f, ResultsVector res, int i, FeatureCalcParams params ) throws FeatureCalcException {
		double val = cache.retriever().calc(f, params);
		res.set(i,val);
	}
	
	
	

	/**
	 * Checks that there's no common features in the featureList and the shared-features as this can create
	 *   complications with initialisation of caches (recursive initializations)
	 * 
	 * @param sharedFeatures
	 * @throws InitException
	 */
	private void checkNoIntersectionWithSharedFeatures( SharedFeatureSet sharedFeatures ) throws InitException {
		assert(listFeatures!=null);
		try {
			for( Feature f : listFeatures ) {
				
				FeatureList allDependents = f.createListChildFeatures(false);
				
				for( Feature dep : allDependents ) {
				
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
	
	
	private CachePlus setupCacheAndInit( FeatureInitParams featureInitParams, SharedFeatureSet sharedFeatures, LogErrorReporter logger ) throws InitException {
		try {
			CachePlus out = new CachePlus(cacheFactory,listFeatures,sharedFeatures);
		
			FeatureInitParams featureInitParamsDup = featureInitParams.duplicateChangeCachePlus(
				out.retrieverPlus()
			);

			out.init(featureInitParamsDup, logger, false);
			
			listFeatures.initRecursive(featureInitParamsDup, logger);
			
			return out;
			
		} catch (CreateException e) {
			throw new InitException(e);
		}		
	}
		
	private static void checkSizesMatch( List<?> listCreateParams, FeatureList listFeatures ) throws FeatureCalcException {
		if( listCreateParams.size()!=listFeatures.size() ) {
			throw new FeatureCalcException( String.format(
				"The number of params (%d) should match the number of features (%d)",
				listCreateParams.size(),
				listFeatures.size()
			) );
		}
	}
	
}
