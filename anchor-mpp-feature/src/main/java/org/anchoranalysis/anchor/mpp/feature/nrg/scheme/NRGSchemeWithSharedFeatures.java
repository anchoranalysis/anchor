package org.anchoranalysis.anchor.mpp.feature.nrg.scheme;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaNRGElemPair;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGScheme;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.cache.LRUCache.CacheRetrievalFailed;
import org.anchoranalysis.core.cache.LRUCache.CalculateForCache;

/*
 * #%L
 * anchor-mpp
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


import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.nrg.NRGTotal;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class NRGSchemeWithSharedFeatures {

	private NRGScheme nrgScheme;
	private SharedFeatureSet<FeatureInput> sharedFeatures;
	
	private LRUCache<Integer,NRGTotal> indCache;
	private CalcElemIndTotalOperation operationIndCalc;
	private LogErrorReporter logger;
	
	private int nrgSchemeIndCacheSize;
	
	// Caches NRG value by index
	private class CalcElemIndTotalOperation implements CalculateForCache<Integer,NRGTotal> {

		private PxlMarkMemo pmm;
		private NRGStack raster;
		private KeyValueParams kvp;
		
		public CalcElemIndTotalOperation() {
			super();
		}

		public void update( PxlMarkMemo pmm, NRGStack raster ) throws FeatureCalcException {
			this.pmm = pmm;
			this.raster = raster;
			
			
			KeyValueParamsForImageCreator creator = new KeyValueParamsForImageCreator(
				nrgScheme,
				sharedFeatures.downcast(),
				logger
			);
			this.kvp = creator.createParamsForImage( raster );
		}
		
		@Override
		public NRGTotal calculate(Integer index) throws CacheRetrievalFailed {
			try {
				return calc();
			} catch (FeatureCalcException e) {
				throw new CacheRetrievalFailed(e);
			}
		}
		
		public NRGTotal calc() throws FeatureCalcException {
			
			FeatureCalculatorMulti<FeatureInputSingleMemo> session;
			
			try {
				session = FeatureSession.with(
					nrgScheme.getElemIndAsFeatureList(),
					createInitParams(kvp),
					sharedFeatures.downcast(),
					logger
				);

			} catch (CreateException e) {
				throw new FeatureCalcException(e);
			}
			
			
			FeatureInputSingleMemo params = new FeatureInputSingleMemo(
				pmm,
				new NRGStackWithParams(raster,kvp)
			);
			
			return new NRGTotal( session.calc(params).total() );
		}
		
	}
	

	
	public NRGSchemeWithSharedFeatures(NRGScheme nrgScheme,
			SharedFeatureSet<FeatureInput> sharedFeatures, int nrgSchemeIndCacheSize, LogErrorReporter logger ) {
		super();
		this.nrgScheme = nrgScheme;
		this.sharedFeatures = sharedFeatures;
		this.nrgSchemeIndCacheSize = nrgSchemeIndCacheSize;
		this.logger = logger;
		
		operationIndCalc = new CalcElemIndTotalOperation();
		indCache = new LRUCache<>(nrgSchemeIndCacheSize, operationIndCalc); 
	}
	
	public NRGTotal calcElemAllTotal( MemoCollection pxlMarkMemoList, NRGStack raster ) throws FeatureCalcException {
		
		NRGStackWithParams nrgStack = createNRGStack(raster);
	
		try {
			FeatureCalculatorMulti<FeatureInputAllMemo> session = FeatureSession.with(
				nrgScheme.getElemAllAsFeatureList(),
				createInitParams(nrgStack.getParams()),
				sharedFeatures.downcast(),
				logger
			);
			
			FeatureInputAllMemo params = new FeatureInputAllMemo(
				pxlMarkMemoList,
				nrgStack
			);
			
			assert pxlMarkMemoList!=null;
			return new NRGTotal( session.calc(params).total() );

		} catch (CreateException e) {
			throw new FeatureCalcException(e);
		}
	}
	
	public NRGTotal calcElemIndTotal( PxlMarkMemo pmm, NRGStack raster ) throws FeatureCalcException {
		
		operationIndCalc.update(pmm, raster);
		
		// We go via the cache if we can
		if (pmm.getMark().hasCacheID()) {
			try {
				return indCache.get( pmm.getMark().getCacheID() );
			} catch (GetOperationFailedException e) {
				throw new FeatureCalcException(e);
			} 
		}
		
		return operationIndCalc.calc();
	}

	
	private NRGStackWithParams createNRGStack( NRGStack raster ) throws FeatureCalcException {
		
		KeyValueParams kvp;
		try {
			kvp = nrgScheme.createKeyValueParams();
		} catch (CreateException e) {
			throw new FeatureCalcException(e);
		}
		
		return new NRGStackWithParams(raster,kvp);
	}

	private static FeatureInitParams createInitParams( KeyValueParams kvp ) throws CreateException {
		return new FeatureInitParams( kvp );
	}
	
	
	public NRGSchemeWithSharedFeatures duplicateWithNewNRGScheme() {
		return new NRGSchemeWithSharedFeatures(nrgScheme.duplicateKeepFeatures(), sharedFeatures, nrgSchemeIndCacheSize, logger );
	}
	
	public NRGScheme getNrgScheme() {
		return nrgScheme;
	}
	public void setNrgScheme(NRGScheme nrgScheme) {
		this.nrgScheme = nrgScheme;
	}
	
	public AddCriteriaNRGElemPair createAddCriteria() throws CreateException {
		try {
			return new AddCriteriaNRGElemPair(
				getNrgScheme().getElemPairAsFeatureList(),
				(AddCriteriaPair) getNrgScheme().getPairAddCriteria().duplicateBean()
			);
		} catch (InitException | BeanDuplicateException e) {
			throw new CreateException(e);
		}
	}

	public SharedFeatureSet<FeatureInput> getSharedFeatures() {
		return sharedFeatures;
	}

	public RegionMap getRegionMap() {
		return nrgScheme.getRegionMap();
	}
}
