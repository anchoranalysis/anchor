package org.anchoranalysis.test.image;

/*-
 * #%L
 * anchor-test-feature-plugins
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.test.image.ChnlFixture.IntensityFunction;

public class NRGStackFixture {

	public static NRGStackWithParams create( boolean big, boolean use3d ) {
	
		Extent size = muxExtent(big, use3d);
		
		try {
			Stack stack = new Stack();
			addChnl(stack, size, ChnlFixture::sumMod);
			addChnl(stack, size, ChnlFixture::diffMod);
			addChnl(stack, size, ChnlFixture::multMod);
			
			NRGStack nrgStack = new NRGStack(stack);
			return new NRGStackWithParams(nrgStack);
			
		} catch (IncorrectImageSizeException e) {
			assert false;
			return null;
		}
	}
	
	private static Extent muxExtent( boolean big, boolean use3d ) {
		if (use3d) {
			return big ? ChnlFixture.LARGE_3D : ChnlFixture.MEDIUM_3D;
		} else {
			return big ? ChnlFixture.LARGE_2D : ChnlFixture.MEDIUM_2D;
		}
	}
	
	private static void addChnl( Stack stack, Extent size, IntensityFunction intensityFunction ) throws IncorrectImageSizeException {
		stack.addChnl(
			ChnlFixture.createChnl(size, intensityFunction)
		);
	}
}
