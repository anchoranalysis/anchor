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

import org.anchoranalysis.core.bridge.IObjectBridgeIndex;

/**
 * 
 * @author Owen Feehan
 *
 * @param <E> external-type
 * @param <H> hidden-type
 */
public class ITypedGetFromIndexBridge<E,H> implements ITypedGetFromIndex<E> {

	private ITypedGetFromIndex<H> delegate;

	private IObjectBridgeIndex<H,E> bridge;
	
	public ITypedGetFromIndexBridge(ITypedGetFromIndex<H> delegate,
			IObjectBridgeIndex<H,E> bridge) {
		super();
		this.delegate = delegate;
		this.bridge = bridge;
	}

	@Override
	public E get(int index) throws GetOperationFailedException {
		try {
			return bridge.bridgeElement( index, delegate.get(index) );
		} catch (BridgeElementException e) {
			throw new GetOperationFailedException(e);
		}
	}
}
