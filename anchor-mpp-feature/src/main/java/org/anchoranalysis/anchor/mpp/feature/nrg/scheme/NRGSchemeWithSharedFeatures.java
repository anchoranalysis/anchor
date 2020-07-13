package org.anchoranalysis.anchor.mpp.feature.nrg.scheme;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaNRGElemPair;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
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


import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.nrg.NRGTotal;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

public class NRGSchemeWithSharedFeatures {

	private NRGScheme nrgScheme;
	private SharedFeatureMulti sharedFeatures;
	
	private CalcElemIndTotalOperation operationIndCalc;
	private Logger logger;
	
	// Caches NRG value by index
	private class CalcElemIndTotalOperation implements FunctionWithException<Integer,NRGTotal,FeatureCalcException> {

		private VoxelizedMarkMemo pmm;
		private NRGStack raster;
		private KeyValueParams kvp;
		
		public CalcElemIndTotalOperation() {
			super();
		}

		public void update( VoxelizedMarkMemo pmm, NRGStack raster ) throws FeatureCalcException {
			this.pmm = pmm;
			this.raster = raster;
						
			KeyValueParamsForImageCreator creator = new KeyValueParamsForImageCreator(
				nrgScheme,
				sharedFeatures,
				logger
			);
			this.kvp = creator.createParamsForImage( raster );
		}
		
		@Override
		public NRGTotal apply(Integer index) throws FeatureCalcException {
			return calc();
		}
		
		public NRGTotal calc() throws FeatureCalcException {
			
			FeatureCalculatorMulti<FeatureInputSingleMemo> session = FeatureSession.with(
				nrgScheme.getElemIndAsFeatureList(),
				new FeatureInitParams(kvp),
				sharedFeatures,
				logger
			);
						
			FeatureInputSingleMemo params = new FeatureInputSingleMemo(
				pmm,
				new NRGStackWithParams(raster,kvp)
			);
			
			return new NRGTotal( session.calc(params).total() );
		}
		
	}
		
	public NRGSchemeWithSharedFeatures(NRGScheme nrgScheme,	SharedFeatureMulti sharedFeatures, Logger logger ) {
		super();
		this.nrgScheme = nrgScheme;
		this.sharedFeatures = sharedFeatures;
		this.logger = logger;
		
		operationIndCalc = new CalcElemIndTotalOperation();
	}
	
	public NRGTotal calcElemAllTotal( MemoCollection pxlMarkMemoList, NRGStack raster ) throws FeatureCalcException {
		
		NRGStackWithParams nrgStack = createNRGStack(raster);
	
		FeatureCalculatorMulti<FeatureInputAllMemo> session = FeatureSession.with(
			nrgScheme.getElemAllAsFeatureList(),
			new FeatureInitParams(nrgStack.getParams()),
			sharedFeatures,
			logger
		);
		
		FeatureInputAllMemo params = new FeatureInputAllMemo(
			pxlMarkMemoList,
			nrgStack
		);

		return new NRGTotal( session.calc(params).total() );

	}
	
	public NRGTotal calcElemIndTotal( VoxelizedMarkMemo pmm, NRGStack raster ) throws FeatureCalcException {
		operationIndCalc.update(pmm, raster);
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
	
	public NRGScheme getNrgScheme() {
		return nrgScheme;
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

	public SharedFeatureMulti getSharedFeatures() {
		return sharedFeatures;
	}

	public RegionMap getRegionMap() {
		return nrgScheme.getRegionMap();
	}
}
