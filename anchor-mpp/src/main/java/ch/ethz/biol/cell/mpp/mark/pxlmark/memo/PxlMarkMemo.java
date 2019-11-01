package ch.ethz.biol.cell.mpp.mark.pxlmark.memo;

import org.anchoranalysis.anchor.mpp.mark.Mark;

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


import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.image.histogram.Histogram;

import ch.ethz.biol.cell.imageprocessing.pixellist.factory.PixelPartFactory;
import ch.ethz.biol.cell.mpp.mark.pxlmark.PxlMark;
import ch.ethz.biol.cell.mpp.mark.pxlmark.PxlMarkHistogram;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMap;

// Memoization of retrieving a PxlMark from a mark
// So we only ever need to do this calculation once for a given instantiation
public class PxlMarkMemo extends CachedOperation<PxlMark> {

	// Parameters
	private Mark mark;
	private NRGStack stack;
	private RegionMap regionMap;
	private PixelPartFactory<Histogram> factoryHistogram;

	public PxlMarkMemo(Mark mark, NRGStack stack, RegionMap regionMap, PixelPartFactory<Histogram> factoryHistogram ) {
		super();
		assert(regionMap!=null);
		this.mark = mark;
		this.stack = stack;
		this.factoryHistogram = factoryHistogram;
		this.regionMap = regionMap;
	}
	
	public PxlMarkMemo(Mark mark, NRGStack stack, RegionMap regionMap, PixelPartFactory<Histogram> factoryHistogram, PxlMark result ) {
		super(result);
		assert(regionMap!=null);
		this.mark = mark;
		this.stack = stack;
		this.factoryHistogram = factoryHistogram;
		this.regionMap = regionMap;
	}
	
	// calculation
	@Override
	protected PxlMark execute() throws ExecuteException {
		return new PxlMarkHistogram(mark, stack, regionMap, factoryHistogram);
	}

	// The associated mark
	public Mark getMark() {
		return mark;
	}
	
	// Duplicates the current mark memo, resetting the calculation state
	public PxlMarkMemo duplicateFresh() {
		return PxlMarkMemoFactory.create(this.mark.duplicate(), stack, regionMap);
	}
	
	// Copies one into the other
	public void assignFrom( PxlMarkMemo pmm ) throws OptionalOperationUnsupportedException {

		super.assignFrom( pmm );
		
		if (pmm.mark!=this.mark) {
			this.mark.assignFrom(pmm.mark);
		}
		this.stack = pmm.stack;
	}

	public void cleanUp() {
		PxlMark pm = getResult();
		if (pm!=null) {
			pm.cleanUp();
		}
	}

	public RegionMap getRegionMap() {
		return regionMap;
	}
}
