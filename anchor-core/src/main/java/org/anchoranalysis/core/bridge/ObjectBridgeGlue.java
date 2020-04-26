package org.anchoranalysis.core.bridge;

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

/**
 * Glues two object bridges together so that a bridge A->B and a bridge B->C becomes bridge A->C
 * 
 * @param <A> source-type
 * @param <B> intermediate-type
 * @param <C> destination-type
 * @param <E> exception throw if something goes wrong
 */
public class ObjectBridgeGlue<A,B,C,E extends Throwable> implements IObjectBridge<A,C,E> {

	private IObjectBridge<A,B,E> bridge1;
	private IObjectBridge<B,C,E> bridge2;
	
	public ObjectBridgeGlue(IObjectBridge<A,B,E> bridge1, IObjectBridge<B,C,E> bridge2) {
		super();
		this.bridge1 = bridge1;
		this.bridge2 = bridge2;
	}

	@Override
	public C bridgeElement(A sourceObject) throws E {
		B objTemp = bridge1.bridgeElement(sourceObject);
		return bridge2.bridgeElement(objTemp);
	}

}
