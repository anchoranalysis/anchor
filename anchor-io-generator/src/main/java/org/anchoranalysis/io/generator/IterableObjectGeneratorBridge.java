package org.anchoranalysis.io.generator;

import org.anchoranalysis.core.bridge.BridgeElementException;

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

// Allows us to call an IterableGenerator<ExternalType> as if it was an IterableGenerator<InternalType>
//   using an interface function to connect the two
public class IterableObjectGeneratorBridge<GeneratorType,ExposedIteratorType,HiddenIteratorType> implements IterableObjectGenerator<ExposedIteratorType,GeneratorType> {

	private ExposedIteratorType element;
	
	private IterableObjectGenerator<HiddenIteratorType,GeneratorType> internalGenerator;
	
	private IObjectBridge<ExposedIteratorType,HiddenIteratorType> elementBridge;
	
	public IterableObjectGeneratorBridge(IterableObjectGenerator<HiddenIteratorType,GeneratorType> internalGenerator, IObjectBridge<ExposedIteratorType,HiddenIteratorType> elementBridge) {
		super();
		this.internalGenerator = internalGenerator;
		this.elementBridge = elementBridge;
	}

	@Override
	public ExposedIteratorType getIterableElement() {
		return this.element;
	}

	@Override
	public void setIterableElement(ExposedIteratorType element) throws SetOperationFailedException {
		
		assert(element!=null);
		this.element = element;
		try {
			HiddenIteratorType bridgedElement = elementBridge.bridgeElement(element); 
			internalGenerator.setIterableElement( bridgedElement );
		} catch (BridgeElementException e) {
			throw new SetOperationFailedException(e);
		}
	}

	@Override
	public ObjectGenerator<GeneratorType> getGenerator() {
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
