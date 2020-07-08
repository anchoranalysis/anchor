package org.anchoranalysis.image.io.generator.raster;

import java.util.Optional;

/*-
 * #%L
 * anchor-image-io
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

import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> iteration-type
 */
public class RasterGeneratorFromDisplayStack<T> extends RasterGenerator implements IterableObjectGenerator<T, Stack> {

	private IterableObjectGenerator<T,DisplayStack> delegate;
	private boolean rgb;
	
	public RasterGeneratorFromDisplayStack(
			IterableObjectGenerator<T, DisplayStack> delegate, boolean rgb) {
		super();
		this.delegate = delegate;
		this.rgb = rgb;
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
	public ObjectGenerator<Stack> getGenerator() {
		return this;
	}



	@Override
	public boolean isRGB() {
		return rgb;
	}

	@Override
	public Optional<ManifestDescription> createManifestDescription() {
		return delegate.getGenerator().createManifestDescription();
	}
	
	@Override
	public T getIterableElement() {
		return delegate.getIterableElement();
	}

	@Override
	public void setIterableElement(T element)
			throws SetOperationFailedException {
		delegate.setIterableElement(element);
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		return delegate.getGenerator().generate().createImgStack(false);
	}


}
