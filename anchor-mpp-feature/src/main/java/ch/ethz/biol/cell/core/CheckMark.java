package ch.ethz.biol.cell.core;

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

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.list.OrderedFeatureList;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.params.ICompatibleWith;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

import ch.ethz.biol.cell.mpp.mark.check.CheckException;

public abstract class CheckMark extends MPPBean<CheckMark> implements ICompatibleWith, OrderedFeatureList {

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
