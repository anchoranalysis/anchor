package ch.ethz.biol.cell.mpp.nrg;

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

import org.anchoranalysis.feature.calc.params.FeatureCalcParamsNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.ImageDim;

public class NRGElemAllCalcParams extends FeatureCalcParamsNRGStack {

	private MemoMarks pxlMarkMemoList;
	private NRGStackWithParams raster;
	
	public NRGElemAllCalcParams(
		MemoMarks pxlMarkMemoList,
		NRGStackWithParams raster
	) {
		this.pxlMarkMemoList = pxlMarkMemoList;
		this.raster = raster;
	}

	public MemoMarks getPxlPartMemo() {
		return pxlMarkMemoList;
	}

	public void setPxlPartMemo(MemoMarks pxlPartMemoList) {
		this.pxlMarkMemoList = pxlPartMemoList;
	}

	public ImageDim getDimensions() {
		return raster.getDimensions();
	}

	@Override
	public NRGStackWithParams getNrgStack() {
		return raster;
	}


}
