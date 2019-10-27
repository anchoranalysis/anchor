package org.anchoranalysis.io.bean.output;

import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;

/*
 * #%L
 * anchor-io
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


class ColorIndexModulo implements ColorIndex {

	private ColorIndex delegate;

	public ColorIndexModulo(ColorIndex delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public RGBColor get(int i) {
		return delegate.get( i % numUniqueColors() );
	}

	@Override
	public int numUniqueColors() {
		return delegate.numUniqueColors();
	}

	@Override
	public boolean has(int i) {
		return delegate.has(i % numUniqueColors() );
	}
	
	
}
