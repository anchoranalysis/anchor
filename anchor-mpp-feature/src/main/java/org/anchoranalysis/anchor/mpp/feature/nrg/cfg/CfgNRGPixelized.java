package org.anchoranalysis.anchor.mpp.feature.nrg.cfg;

import java.util.Optional;

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
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

import lombok.Getter;

/**
 * Contains a particular energy configuration
 * 
 * <p>i.e. A {@link CfgNRG} together with the cached voxelization calculations.</p> 
 * 
 * @author Owen Feehan
 *
 */
public final class CfgNRGPixelized  {
    
	@Getter
    private CfgNRG cfgNRG;
	
    private LogErrorReporter logger;
	
	/** A cached version of the calculations for each energy component in the associated {@link NRGScheme} */
    private Optional<MemoCollection> memoMarks;
    
    public CfgNRGPixelized( CfgNRG cfgNRG, LogErrorReporter logger ) {
    	this.cfgNRG = cfgNRG;
    	this.memoMarks = Optional.empty();
    	this.logger = logger;
    }
   
    // Copy constructor - we do shallow copying of configuration
    public CfgNRGPixelized shallowCopy() {
    	CfgNRGPixelized newCfgNRG = new CfgNRGPixelized(cfgNRG.shallowCopy(), logger); 
    	newCfgNRG.memoMarks = memoMarks.map(MemoCollection::new);
    	return newCfgNRG;
    }
    
    
    // Copy constructor - we do shallow copying of configuration
    public CfgNRGPixelized deepCopy() {
    	CfgNRGPixelized newCfgNRG = new CfgNRGPixelized(cfgNRG.deepCopy(), logger);
    	newCfgNRG.memoMarks = memoMarks.map(MemoCollection::new);
    	return newCfgNRG;
    }

	
	public Cfg getCfg() {
		return cfgNRG.getCfg();
	}
	

	
	public void assertValid() {
		memoMarks.ifPresent(MemoCollection::assertValid);
		cfgNRG.assertValid();
	}
	
	public void assertFresh( NRGStackWithParams nrgStack, SharedFeatureMulti sharedFeatures ) throws FeatureCalcException, InitException {

		double old = cfgNRG.getNrgTotal();
		this.init( nrgStack, sharedFeatures );
		assert( Math.abs( cfgNRG.getNrgTotal() - old) < 1e-6 );
	}
	
	// The initial calculation of the NRG, thereafter it can be updated
	public void init( NRGStackWithParams nrgStack, SharedFeatureMulti sharedFeatures ) throws InitException, FeatureCalcException {

		cfgNRG.init();
		
		this.memoMarks = Optional.of(
			new MemoCollection(
				cfgNRG.getCalcMarkInd(),
				nrgStack.getNrgStack(),
				cfgNRG.getCfg(),
				cfgNRG.getNrgScheme()
			)
		);
		
		cfgNRG.getCalcMarkPair().initUpdatableMarkSet( memoMarks.get(), nrgStack, logger, sharedFeatures );
		
		// Some nrg components need to be calculated in terms of interactions
		//  this we need to track in an intelligent way
		cfgNRG.updateTotal( memoMarks.get(), nrgStack.getNrgStack() );
	}
	
	public void clean() {
		memoMarks.ifPresent(MemoCollection::clean);
	}
	
	public NRGSchemeWithSharedFeatures getNRGScheme() {
		return cfgNRG.getNrgScheme();
	}
	
	public void add( PxlMarkMemo newPxlMark, NRGStack stack ) throws FeatureCalcException {
		cfgNRG.add(memoMarks.get(), newPxlMark, stack);
	}
	
	public void rmv( int index, NRGStack stack ) throws FeatureCalcException {
		PxlMarkMemo memoRmv = getMemoForIndex(index);
		cfgNRG.rmv(memoMarks.get(), index, memoRmv, stack );
	}
	
	public void rmv(PxlMarkMemo memoRmv, NRGStack stack) throws FeatureCalcException {
		cfgNRG.rmv(memoMarks.get(), memoRmv, stack);
	}

	public void rmvTwo( int index1, int index2, NRGStack stack ) throws FeatureCalcException {
		cfgNRG.rmvTwo(memoMarks.get(), index1, index2, stack);
	}

	// Does the pairs hash only contains items contained in a particular configuration
	public boolean isCfgSpan() {
		return cfgNRG.getCalcMarkPair().isCfgSpan(cfgNRG.getCfg() );
	}
	
	// calculates a new energy and configuration based upon a mark at a particular index
	//   changing into new mark
	public void exchange( int index, PxlMarkMemo newMark, NRGStackWithParams nrgStack ) throws FeatureCalcException {
		cfgNRG.exchange(memoMarks.get(), index, newMark, nrgStack );
	}

	public double getTotal() {
		return cfgNRG.getNrgTotal();
	}
	
	@Override
	public String toString() {
		
		String newLine = System.getProperty("line.separator");
		
		StringBuilder s = new StringBuilder("{");
		
		s.append( String.format("size=%d, total=%e, ind=%e, pair=%e%n", cfgNRG.getCfg().size(), cfgNRG.getNrgTotal(), cfgNRG.getCalcMarkInd().getNrgTotal(), cfgNRG.getCalcMarkPair().getNRGTotal() ) );
		
		s.append( cfgNRG.getCalcMarkInd().stringCfgNRG( cfgNRG.getCfg() ) );
		s.append( cfgNRG.getCalcMarkPair().toString() );
		
		s.append("}");
		s.append( newLine );
		
		return s.toString();
	}

	// Adds all current marks to the updatable-pair list
	public void addAllToUpdatablePairList( ListUpdatableMarkSetCollection updatablePairList ) throws UpdateMarkSetException {
		updatablePairList.add( memoMarks.get() );
	}
	
	// Adds the particular memo to the updatable pair-list
	public void addToUpdatablePairList( ListUpdatableMarkSetCollection updatablePairList, PxlMarkMemo memo ) throws UpdateMarkSetException {
		updatablePairList.add( memoMarks.get(), memo );
	}
	
	// Removes a memo from the updatable pair-list
	public void rmvFromUpdatablePairList( ListUpdatableMarkSetCollection updatablePairList, Mark mark ) throws UpdateMarkSetException {
		PxlMarkMemo memo = getMemoForMark( mark );
		updatablePairList.rmv(memoMarks.get(), memo);
	}
	
	// Exchanges one mark with another on the updatable pair list
	public void exchangeOnUpdatablePairList(ListUpdatableMarkSetCollection updatablePairList, Mark markExst, PxlMarkMemo memoNew) throws UpdateMarkSetException {
		PxlMarkMemo memoExst = getMemoForMark( markExst );
		updatablePairList.exchange(memoMarks.get(), memoExst, getCfg().indexOf(markExst), memoNew );
	}
	
	
	public MemoList createDuplicatePxlMarkMemoList() {
		MemoList list = new MemoList();
		list.addAll(memoMarks.get());
		return list;
	}
	
	public PxlMarkMemo getMemoForMark(Mark mark ) {
		return memoMarks.get().getMemoForMark(cfgNRG.getCfg(), mark );
	}
	
	public PxlMarkMemo getMemoForIndex(int index ) {
		PxlMarkMemo pmm = memoMarks.get().getMemoForIndex(index);
		assert(pmm!=null);
		return pmm;
	}
}
