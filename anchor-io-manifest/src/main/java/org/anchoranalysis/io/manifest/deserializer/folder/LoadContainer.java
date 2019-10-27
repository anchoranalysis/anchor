package org.anchoranalysis.io.manifest.deserializer.folder;

/*-
 * #%L
 * anchor-io-manifest
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

import org.anchoranalysis.core.index.container.IBoundedIndexContainer;

public class LoadContainer<T> {

	// What do we need to create a frame generically
	private IBoundedIndexContainer<T> cntr;
	
	// If true, then it takes a long time to load a state when requested (use of Bundles, expensive deserialization etc.)
	// If false, then it assumes it does not take a long time to load a state when requested (quick deserialization, in memory, etc.)
	private boolean expensiveLoad;
	
	public boolean isExpensiveLoad() {
		return expensiveLoad;
	}
	public void setExpensiveLoad(boolean expensiveLoad) {
		this.expensiveLoad = expensiveLoad;
	}
	public IBoundedIndexContainer<T> getCntr() {
		return cntr;
	}
	public void setCntr(IBoundedIndexContainer<T> cntr) {
		this.cntr = cntr;
	}
}
