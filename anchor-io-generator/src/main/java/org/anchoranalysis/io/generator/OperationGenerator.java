package org.anchoranalysis.io.generator;

import java.nio.file.Path;

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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.OutputWriteFailedException;

public class OperationGenerator<GeneratedType,IterationType> extends ObjectGenerator<GeneratedType> implements IterableObjectGenerator<Operation<IterationType>, GeneratedType> {

	private IterableObjectGenerator<IterationType, GeneratedType> delegate;

	private Operation<IterationType> element;
	
	public OperationGenerator(
			IterableObjectGenerator<IterationType, GeneratedType> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void setIterableElement(Operation<IterationType> element)
			throws SetOperationFailedException {
		this.element = element;
		try {
			delegate.setIterableElement(element.doOperation());
		} catch (ExecuteException e) {
			throw new SetOperationFailedException(e.getCause());
		}
	}
	
	@Override
	public Operation<IterationType> getIterableElement() {
		return element;
	}

	@Override
	public void start() throws OutputWriteFailedException {
		delegate.start();
	}

	@Override
	public void end() throws OutputWriteFailedException {
		delegate.end();
	}

	@Override
	public ObjectGenerator<GeneratedType> getGenerator() {
		return delegate.getGenerator();
	}

	@Override
	public GeneratedType generate() throws OutputWriteFailedException {
		return delegate.getGenerator().generate();
	}

	@Override
	public void writeToFile(OutputWriteSettings outputWriteSettings,
			Path filePath) throws OutputWriteFailedException {
		delegate.getGenerator().writeToFile(outputWriteSettings, filePath);
		
	}

	@Override
	public String getFileExtension(OutputWriteSettings outputWriteSettings) {
		return delegate.getGenerator().getFileExtension(outputWriteSettings);
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return delegate.getGenerator().createManifestDescription();
	}
	
	
}
