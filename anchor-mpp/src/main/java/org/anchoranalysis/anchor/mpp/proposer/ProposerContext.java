package org.anchoranalysis.anchor.mpp.proposer;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;

/*-
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemoFactory;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.ImageDim;

public class ProposerContext {

	private RandomNumberGenerator re;
	private NRGStackWithParams nrgStack;
	private RegionMap regionMap;
	private ErrorNode errorNode;
	
	public ProposerContext(RandomNumberGenerator re, NRGStackWithParams nrgStack, RegionMap regionMap,
			ErrorNode errorNode) {
		super();
		this.re = re;
		this.nrgStack = nrgStack;
		this.regionMap = regionMap;
		this.errorNode = errorNode;
		assert(errorNode!=null);
	}
	
	public ProposerContext replaceError( ErrorNode errorNode ) {
		assert( errorNode!=null );
		return new ProposerContext(
			re,
			nrgStack,
			regionMap,
			errorNode
		);
	}
	
	public ProposerContext addErrorLevel( String errorMessage ) {
		return new ProposerContext(
			re,
			nrgStack,
			regionMap,
			errorNode.add(errorMessage)
		);
	}
	
	public ProposerContext addErrorLevel( String errorMessage, AnchorBean<?> bean ) {
		return new ProposerContext(
			re,
			nrgStack,
			regionMap,
			errorNode.addBean(errorMessage, bean)
		);
	}
	
	/** Samples an integer uniformally between [0..maxVal) */
	public int sampleInteger(int maxValExclusive) {
		return (int) (re.nextDouble() * maxValExclusive);
	}
	
	public RandomNumberGenerator getRe() {
		return re;
	}

	public ImageDim getDimensions() {
		return nrgStack.getNrgStack().getDimensions();
	}
	
	public PxlMarkMemo create(Mark mark) {
		return PxlMarkMemoFactory.create(mark, nrgStack.getNrgStack(), regionMap);
	}

	public RegionMap getRegionMap() {
		return regionMap;
	}

	public ErrorNode getErrorNode() {
		return errorNode;
	}

	public NRGStackWithParams getNrgStack() {
		return nrgStack;
	}
}
