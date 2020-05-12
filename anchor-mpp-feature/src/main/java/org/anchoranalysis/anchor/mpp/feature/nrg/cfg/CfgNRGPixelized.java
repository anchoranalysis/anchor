package org.anchoranalysis.anchor.mpp.feature.nrg.cfg;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.mark.ListUpdatableMarkSetCollection;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoList;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;

/*-
 * #%L
 * anchor-mpp-feature
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

/* Contains a particular energy configuration */

// A CfgNRG together with the cached voxelization calculations
public final class CfgNRGPixelized {

	/**
	 * 
	 */
	
	// A cached version of the calculations for
	//   each energy component in the associated NRGScheme
    private MemoCollection memoMarks;
    
    private CfgNRG cfgNrg;
    
    private LogErrorReporter logger;
    
    public CfgNRGPixelized( CfgNRG cfgNRG, LogErrorReporter logger ) {
    	
    	this.cfgNrg = cfgNRG;
    	this.memoMarks = null;
    	this.logger = logger;
    }
   
    // Copy constructor - we do shallow copying of configuration
    public CfgNRGPixelized shallowCopy() {
    	CfgNRGPixelized newCfgNRG = new CfgNRGPixelized(cfgNrg.shallowCopy(), logger); 
    	newCfgNRG.memoMarks = new MemoCollection( this.memoMarks );
    	return newCfgNRG;
    }
    
    
    // Copy constructor - we do shallow copying of configuration
    public CfgNRGPixelized deepCopy() {
    	CfgNRGPixelized newCfgNRG = new CfgNRGPixelized(cfgNrg.deepCopy(), logger);
    	newCfgNRG.memoMarks = new MemoCollection( this.memoMarks );
    	return newCfgNRG;
    }

	
	public Cfg getCfg() {
		return cfgNrg.getCfg();
	}
	

	
	public void assertValid() {
		if (this.memoMarks!=null) {
			this.memoMarks.assertValid();
		}
		cfgNrg.assertValid();
	}
	
	public void assertFresh( NRGStackWithParams nrgStack, SharedFeatureMulti<FeatureInput> sharedFeatures ) throws FeatureCalcException, InitException {

		double old = cfgNrg.getNrgTotal();
		this.init( nrgStack, sharedFeatures );
		assert( Math.abs( cfgNrg.getNrgTotal() - old) < 1e-6 );
	}
	
	// The initial calculation of the NRG, thereafter it can be updated
	public void init( NRGStackWithParams nrgStack, SharedFeatureMulti<FeatureInput> sharedFeatures ) throws InitException, FeatureCalcException {

		cfgNrg.init();
		
		this.memoMarks = new MemoCollection(
			cfgNrg.getCalcMarkInd(),
			nrgStack.getNrgStack(),
			cfgNrg.getCfg(),
			cfgNrg.getNrgScheme()
		);
		
		cfgNrg.getCalcMarkPair().initUpdatableMarkSet( this.memoMarks, nrgStack, logger, sharedFeatures );
		
		// Some nrg components need to be calculated in terms of interactions
		//  this we need to track in an intelligent way
		cfgNrg.updateTotal( memoMarks, nrgStack.getNrgStack() );
	}
	
	public void clean() {
		if (this.memoMarks!=null) {
			this.memoMarks.clean();
		}
	}
	
	public NRGSchemeWithSharedFeatures getNRGScheme() {
		return cfgNrg.getNrgScheme();
	}
	
	public void add( PxlMarkMemo newPxlMark, NRGStack stack ) throws FeatureCalcException {
		cfgNrg.add(memoMarks, newPxlMark, stack, logger);
	}
	
	public void rmv( int index, NRGStack stack ) throws FeatureCalcException {
		PxlMarkMemo memoRmv = getMemoForIndex(index);
		cfgNrg.rmv(memoMarks, index, memoRmv, stack );
	}
	
	public void rmv(PxlMarkMemo memoRmv, NRGStack stack) throws FeatureCalcException {
		cfgNrg.rmv(memoMarks, memoRmv, stack);
	}

	public void rmvTwo( int index1, int index2, NRGStack stack ) throws FeatureCalcException {
		cfgNrg.rmvTwo(memoMarks, index1, index2, stack);
	}

	// Does the pairs hash only contains items contained in a particular configuration
	public boolean isCfgSpan() {
		return cfgNrg.getCalcMarkPair().isCfgSpan(cfgNrg.getCfg() );
	}
	
	// calculates a new energy and configuration based upon a mark at a particular index
	//   changing into new mark
	public void exchange( int index, PxlMarkMemo newMark, NRGStackWithParams nrgStack ) throws FeatureCalcException {
		cfgNrg.exchange(memoMarks, index, newMark, nrgStack );
	}

	public double getTotal() {
		return cfgNrg.getNrgTotal();
	}
	
	@Override
	public String toString() {
		
		String newLine = System.getProperty("line.separator");
		
		StringBuilder s = new StringBuilder("{");
		
		s.append( String.format("size=%d, total=%e, ind=%e, pair=%e%n", cfgNrg.getCfg().size(), cfgNrg.getNrgTotal(), cfgNrg.getCalcMarkInd().getNrgTotal(), cfgNrg.getCalcMarkPair().getNRGTotal() ) );
		
		s.append( cfgNrg.getCalcMarkInd().stringCfgNRG( cfgNrg.getCfg() ) );
		s.append( cfgNrg.getCalcMarkPair().toString() );
		
		s.append("}");
		s.append( newLine );
		
		return s.toString();
	}

	// Adds all current marks to the updatable-pair list
	public void addAllToUpdatablePairList( ListUpdatableMarkSetCollection updatablePairList ) throws UpdateMarkSetException {
		updatablePairList.add( memoMarks );
	}
	
	// Adds the particular memo to the updatable pair-list
	public void addToUpdatablePairList( ListUpdatableMarkSetCollection updatablePairList, PxlMarkMemo memo ) throws UpdateMarkSetException {
		updatablePairList.add( memoMarks, memo );
	}
	
	// Removes a memo from the updatable pair-list
	public void rmvFromUpdatablePairList( ListUpdatableMarkSetCollection updatablePairList, Mark mark ) throws UpdateMarkSetException {
		PxlMarkMemo memo = getMemoForMark( mark );
		updatablePairList.rmv(memoMarks, memo);
	}
	
	// Exchanges one mark with another on the updatable pair list
	public void exchangeOnUpdatablePairList(ListUpdatableMarkSetCollection updatablePairList, Mark markExst, PxlMarkMemo memoNew) throws UpdateMarkSetException {
		PxlMarkMemo memoExst = getMemoForMark( markExst );
		updatablePairList.exchange( memoMarks, memoExst, getCfg().indexOf(markExst), memoNew );
	}
	
	
	public MemoList createDuplicatePxlMarkMemoList() {
		MemoList list = new MemoList();
		list.addAll(memoMarks);
		return list;
	}
	
	public PxlMarkMemo getMemoForMark(Mark mark ) {
		return memoMarks.getMemoForMark( cfgNrg.getCfg(), mark );
	}
	
	public PxlMarkMemo getMemoForIndex(int index ) {
		PxlMarkMemo pmm = memoMarks.getMemoForIndex(index);
		assert(pmm!=null);
		return pmm;
	}

	public CfgNRG getCfgNRG() {
		return cfgNrg;
	}
}
