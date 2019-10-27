package ch.ethz.biol.cell.core;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

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


import ch.ethz.biol.cell.mpp.MPPBean;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.check.CheckException;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMap;
import ch.ethz.biol.cell.mpp.pair.addcriteria.IOrderedListOfFeatures;
import ch.ethz.biol.cell.mpp.proposer.ICompatibleWith;

public abstract class CheckMark extends MPPBean<CheckMark> implements ICompatibleWith, IOrderedListOfFeatures {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8676574486398286141L;
	
	/**
	 * Called before any calls to check()
	 * @param nrgStack TODO
	 */
	public void start(NRGStackWithParams nrgStack) throws OperationFailedException {

	}
	
	/**
	 * Checks a mark
	 * 
	 * @param mark
	 * @param regionMap
	 * @param nrgStack
	 * @param featureSession session (nb nrgStack can be added to featureSession)
	 * @return
	 */
	public abstract boolean check(Mark mark, RegionMap regionMap, NRGStackWithParams nrgStack) throws CheckException;

	/**
	 * Called after any calls to check()
	 */
	public void end() {
		
	}

}
