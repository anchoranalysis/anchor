package org.anchoranalysis.core.index;

import org.anchoranalysis.core.bridge.BridgeElementException;

/*-
 * #%L
 * anchor-core
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

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;

public class BoundedIndexBridge<T> implements IObjectBridge<Integer, T> {
	
	private IBoundedIndexContainer<T> cntr;
	
	public BoundedIndexBridge(IBoundedIndexContainer<T> cntr) {
		super();
		this.cntr = cntr;
	}

	@Override
	public T bridgeElement(Integer sourceObject) throws BridgeElementException {
		int index = cntr.previousEqualIndex(sourceObject);
		if (index==-1) {
			throw new BridgeElementException("Cannot find a previousEqualIndex in the cntr");
		}
		try {
			return cntr.get( index );
		} catch (GetOperationFailedException e) {
			throw new BridgeElementException(e);
		}
	}

	// Updates the cntr associated with the bridge
	public void setCntr(IBoundedIndexContainer<T> cntr) {
		this.cntr = cntr;
	}

}
