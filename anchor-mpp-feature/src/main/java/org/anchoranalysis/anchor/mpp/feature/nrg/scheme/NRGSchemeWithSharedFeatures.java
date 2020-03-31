package org.anchoranalysis.anchor.mpp.feature.nrg.scheme;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaNRGElemPair;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGScheme;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoMarks;
import org.anchoranalysis.anchor.mpp.feature.session.FeatureSessionCreateParamsMPP;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.bean.error.BeanDuplicateException;

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


import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.cache.LRUHashMapCache;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGTotal;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class NRGSchemeWithSharedFeatures {

	private NRGScheme nrgScheme;
	private SharedFeatureSet sharedFeatures;
	
	private LRUHashMapCache<NRGTotal, Integer> indCache;
	private CalcElemIndTotalOperation operationIndCalc;
	private CacheMonitor cacheMonitor;
	private LogErrorReporter logger;
	
	private int nrgSchemeIndCacheSize;
	
	// Caches NRG value by index
	private class CalcElemIndTotalOperation implements LRUHashMapCache.Getter<NRGTotal, Integer> {

		private PxlMarkMemo pmm;
		private NRGStack raster;
		private KeyValueParams kpv;
		
		public CalcElemIndTotalOperation() {
			super();
		}

		public void update( PxlMarkMemo pmm, NRGStack raster ) throws FeatureCalcException {
			this.pmm = pmm;
			this.raster = raster;
			
			
			KeyValueParamsForImageCreator creator = new KeyValueParamsForImageCreator( nrgScheme, sharedFeatures, logger );
			this.kpv = creator.createParamsForImage( raster );
		}
		
		@Override
		public NRGTotal get(Integer index)
				throws GetOperationFailedException {
			try {
				return calc();
			} catch (FeatureCalcException e) {
				throw new GetOperationFailedException(e);
			}
		}
		
		public NRGTotal calc() throws FeatureCalcException {
			
			FeatureSessionCreateParamsMPP session = new FeatureSessionCreateParamsMPP(
				nrgScheme.getElemIndAsFeatureList(),
				raster,
				kpv
			);
			
			try {
				session.start(
					createInitParams(kpv),
					sharedFeatures,
					logger
				);
			} catch (InitException | CreateException e) {
				throw new FeatureCalcException(e);
			}
			
			return new NRGTotal( session.calc(pmm).total() );
		}
		
	}
	
	public NRGSchemeWithSharedFeatures(NRGScheme nrgScheme,
			SharedFeatureSet sharedFeatures, int nrgSchemeIndCacheSize, CacheMonitor cacheMonitor, LogErrorReporter logger ) {
		super();
		this.nrgScheme = nrgScheme;
		this.sharedFeatures = sharedFeatures;
		this.nrgSchemeIndCacheSize = nrgSchemeIndCacheSize;
		this.cacheMonitor = cacheMonitor;
		this.logger = logger;
		
		operationIndCalc = new CalcElemIndTotalOperation();
		indCache = LRUHashMapCache.createAndMonitor(nrgSchemeIndCacheSize, operationIndCalc, cacheMonitor, "NRGSchemeWithSharedFeatures"); 
	}
	
	
	
	public NRGTotal calcElemAllTotal( MemoMarks pxlMarkMemoList, NRGStack raster ) throws FeatureCalcException {
		
		KeyValueParams kvp;
		try {
			kvp = nrgScheme.createKeyValueParams();
		} catch (CreateException e) {
			throw new FeatureCalcException(e);
		}
		
		FeatureSessionCreateParamsMPP session = new FeatureSessionCreateParamsMPP(
			nrgScheme.getElemAllAsFeatureList(),
			raster,
			kvp
		);
		
		try {
			session.start(createInitParams(kvp), sharedFeatures, logger);
		} catch (InitException | CreateException e) {
			throw new FeatureCalcException(e);
		}
		
		assert pxlMarkMemoList!=null;
		return new NRGTotal( session.calc(pxlMarkMemoList).total() );
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
	

	private static FeatureInitParams createInitParams( KeyValueParams kvp ) throws CreateException {
		return new FeatureInitParams( kvp );
	}
	
	
	public NRGSchemeWithSharedFeatures duplicateWithNewNRGScheme() {
		return new NRGSchemeWithSharedFeatures(nrgScheme.duplicateKeepFeatures(), sharedFeatures, nrgSchemeIndCacheSize, cacheMonitor, logger );
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

	public SharedFeatureSet getSharedFeatures() {
		return sharedFeatures;
	}

	public RegionMap getRegionMap() {
		return nrgScheme.getRegionMap();
	}
}
