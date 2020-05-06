package org.anchoranalysis.io.generator;



/*-
 * #%L
 * anchor-io-generator
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
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows us to call an IterableGenerator<ExternalType> as if it was an IterableGenerator<InternalType>
 *   using an interface function to connect the two
 * 
 * @author Owen Feehan
 *
 * @param <S> generator-type
 * @param <T> exposed-iterator type
 * @param <V> hidden-iterator-type
 */
public class IterableObjectGeneratorBridge<S,T,V> implements IterableObjectGenerator<T,S> {

	private T element;
	
	private IterableObjectGenerator<V,S> internalGenerator;
	
	private IObjectBridge<T,V,? extends Throwable> elementBridge;
	
	public IterableObjectGeneratorBridge(IterableObjectGenerator<V,S> internalGenerator, IObjectBridge<T,V,? extends Throwable> elementBridge) {
		super();
		this.internalGenerator = internalGenerator;
		this.elementBridge = elementBridge;
	}

	@Override
	public T getIterableElement() {
		return this.element;
	}

	@Override
	public void setIterableElement(T element) throws SetOperationFailedException {
		
		assert(element!=null);
		this.element = element;
		try {
			V bridgedElement = elementBridge.bridgeElement(element); 
			internalGenerator.setIterableElement( bridgedElement );
		} catch (Throwable e) {
			throw new SetOperationFailedException(e);
		}
	}

	@Override
	public ObjectGenerator<S> getGenerator() {
		return internalGenerator.getGenerator();
	}

	@Override
	public void start() throws OutputWriteFailedException {
		internalGenerator.start();
	}


	@Override
	public void end() throws OutputWriteFailedException {
		internalGenerator.end();
	}

}
