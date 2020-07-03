package org.anchoranalysis.anchor.mpp.feature.mark;

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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedInd;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemoFactory;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGTotal;

/**
 * A collection of memoized marks on which energies can be derived.
 * 
 * <p>TODO, can it be united with {@link #MemoList}?</p>
 * 
 * @author Owen Feehan
 *
 */
public class MemoCollection implements Serializable, MemoForIndex {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9067220044867268357L;
    
    // We keep the pixelized version of the marks
    private transient List<PxlMarkMemo> pxlMarkMemo;
    
    private transient RegionMap regionMap;
    
	// START CONSTRUCTORS
    public MemoCollection() {
    	pxlMarkMemo = new ArrayList<PxlMarkMemo>();
    }
    
	public MemoCollection(
		NRGSavedInd nrgSavedInd,
		NRGStack nrgStack,
		Cfg cfg,
		NRGSchemeWithSharedFeatures nrgSchemeTotal
	) throws FeatureCalcException {
		this.regionMap = nrgSchemeTotal.getRegionMap();
		calcFreshInd( nrgSavedInd, nrgStack, cfg, nrgSchemeTotal );
	}
	
	public MemoCollection( MemoCollection src ) {
    	this.pxlMarkMemo = new ArrayList<>();
    	this.regionMap = src.getRegionMap();
    	for( PxlMarkMemo pmm : src.pxlMarkMemo ) {
    		this.pxlMarkMemo.add(pmm);
    	}
	}
	// END CONSTRUCTORS
		
	public void clean() {
		pxlMarkMemo = null;
	}
	
	@Override
	public int size() {
		return pxlMarkMemo.size();
	}
	
	public RegionMap getRegionMap() {
		return regionMap;
	}
	
	public PxlMarkMemo getMemoForMark( Cfg cfg, Mark mark ) {
		int index = cfg.indexOf(mark);
		if (index==-1) {
			throw new AnchorFriendlyRuntimeException("Mark doesn't exist in cfg");
		}
		return pxlMarkMemo.get( index );
	}
	
	public PxlMarkMemo getMemoForIndex( int index ) {
		PxlMarkMemo pmm = pxlMarkMemo.get( index );
		assert( pmm != null );
		return pmm;
	}
	
	public int getIndexForMemo( PxlMarkMemo memo ) {
		for (int i=0; i<pxlMarkMemo.size(); i++) {
			
			PxlMarkMemo pmm = pxlMarkMemo.get(i);
			
			if (pmm.equals(memo)) {
				return i;
			}
		}
		return -1;
	}

	// calc fresh ind	
	private void calcFreshInd( NRGSavedInd nrgSavedInd, NRGStack nrgStack, Cfg cfg, NRGSchemeWithSharedFeatures nrgSchemeTotal ) throws FeatureCalcException {

		nrgSavedInd.setNrgTotal(0);
		
		this.pxlMarkMemo = new ArrayList<>();
				
		nrgSavedInd.resetInd();
		
		// Some nrg components need to be calculated individually
		for( Mark mrk : cfg ) {

			PxlMarkMemo pmm = PxlMarkMemoFactory.create( mrk, nrgStack, nrgSchemeTotal.getRegionMap() );
			this.pxlMarkMemo.add(pmm);
			
			NRGTotal ind = nrgSchemeTotal.calcElemIndTotal(pmm, nrgStack );
			nrgSavedInd.add(ind);
			
		}

	}
		
	
	
	// calculates a new energy and configuration based upon a mark at a particular index
	//   changing into new mark
	public PxlMarkMemo exchange( NRGSavedInd nrgSavedInd, int index, PxlMarkMemo newMark, NRGStack stack, Cfg cfg, NRGSchemeWithSharedFeatures nrgSchemeTotal ) throws FeatureCalcException {
		
		assert( newMark != null );

		// We calculate energy for individual components
		NRGTotal ind = nrgSchemeTotal.calcElemIndTotal(newMark, stack );
		nrgSavedInd.exchange(index, ind);
		
		this.pxlMarkMemo.set(index, newMark);
		
		return newMark;
	}
	
	public PxlMarkMemo add( NRGSavedInd nrgSavedInd, PxlMarkMemo pmm, NRGStack stack, NRGSchemeWithSharedFeatures nrgScheme ) throws FeatureCalcException {
		
		assert( pmm!=null );
		NRGTotal nrg = nrgScheme.calcElemIndTotal(pmm, stack);
		
		// We calculate energy for individual components
		this.pxlMarkMemo.add(pmm);
		
		nrgSavedInd.add(nrg);
		
		return pmm;
	}
	
	public void rmv( NRGSavedInd nrgSavedInd, int index ) {
		
		nrgSavedInd.rmv(index);
		
		this.pxlMarkMemo.remove(index);
	}
	
	public void rmvTwo( NRGSavedInd nrgSavedInd, int index1, int index2 ) { 		
		int indexMax = Math.max(index1, index2);
		int indexMin = Math.min(index1, index2);
		
		rmv( nrgSavedInd, indexMax );
		rmv( nrgSavedInd, indexMin );
	}
	
	public void assertValid() {
		for (PxlMarkMemo pmm : pxlMarkMemo) {
			if(pmm==null) {
				assert false;
			}
		}
	}
	
	public Cfg asCfg() {
		Cfg cfg = new Cfg();
		for( int i=0; i<this.size(); i++ ) {
			PxlMarkMemo pmm = this.getMemoForIndex(i);
			cfg.add( pmm.getMark() );
		}
		return cfg;
	}
}
