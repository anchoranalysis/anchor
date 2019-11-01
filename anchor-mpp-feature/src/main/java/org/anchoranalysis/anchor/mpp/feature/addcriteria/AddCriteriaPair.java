package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;

/*
 * #%L
 * anchor-mpp-feature
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


import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.ImageDim;

import ch.ethz.biol.cell.mpp.nrg.feature.session.FeatureSessionCreateParamsMPP;

public abstract class AddCriteriaPair extends AnchorBean<AddCriteriaPair> implements AddCriteria<Pair<Mark>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4297170419831364319L;
	
	@Override
	public Pair<Mark> generateEdge(PxlMarkMemo mark1, PxlMarkMemo mark2, NRGStackWithParams nrgStack, FeatureSessionCreateParamsMPP session, boolean use3D) throws CreateException {

		try {
			if ( includeMarks(mark1, mark2, nrgStack.getDimensions(), session, use3D) ) {
				return new Pair<Mark>( mark1.getMark(), mark2.getMark() );
			}
		} catch (IncludeMarksFailureException e) {
			throw new CreateException(e);
		}
		
		return null;
	}

	public abstract boolean includeMarks( PxlMarkMemo mark1, PxlMarkMemo mark2, ImageDim dim, FeatureSessionCreateParamsMPP session, boolean use3D ) throws IncludeMarksFailureException;
	
	@Override
	public String getBeanDscr() {
		return getBeanName();
	}
}
