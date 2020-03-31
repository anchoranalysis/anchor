package org.anchoranalysis.anchor.mpp.pxlmark.memo;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactory;
import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactoryHistogram;

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

import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.image.histogram.Histogram;

// We more explicitly manage the creation and deletiion of PxlMarkMemo as they can take up a large amount of memory
public class PxlMarkMemoFactory {

	private static PixelPartFactory<Histogram> factoryHistogram = new PixelPartFactoryHistogram();
	
	private PxlMarkMemoFactory() {
		// ONLY ALLOWED AS STATIC
	}
	
	public static PxlMarkMemo create(Mark mark, NRGStack stack, RegionMap regionMap) {
		return new PxlMarkMemo(mark, stack, regionMap, factoryHistogram);
	}
}
