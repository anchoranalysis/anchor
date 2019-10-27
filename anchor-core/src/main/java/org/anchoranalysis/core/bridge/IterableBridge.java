package org.anchoranalysis.core.bridge;

/*
 * #%L
 * anchor-core
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


import java.util.Iterator;

/**
 * 
 * @author Owen Feehan
 *
 * @param <S> source-type
 * @param <D> destination-type
 */
public class IterableBridge<S,D> implements Iterator<D> {

	private Iterator<S> iterator;
	private IObjectBridgeNoException<S,D> bridge;
	
	public IterableBridge( Iterator<S> iterator, IObjectBridgeNoException<S,D> bridge ) {
		super();
		this.iterator = iterator;
		this.bridge = bridge;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public D next() {
		return bridge.bridgeElement( iterator.next() );
	}

	@Override
	public void remove() {
		iterator.remove();
	}
	
	
}
