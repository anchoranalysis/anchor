package org.anchoranalysis.image.io.generator.raster;

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

import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class ChnlGenerator extends RasterGenerator implements IterableObjectGenerator<Chnl,Stack> {

	private Chnl chnl = null;
	private String manifestFunction;
	
	public ChnlGenerator(String manifestFunction) {
		this.manifestFunction = manifestFunction;
	}
	
	public ChnlGenerator(Chnl chnl) {
		this(chnl,"chnl");
	}
	
	public ChnlGenerator(Chnl chnl, String manifestFunction) {
		super();
		this.chnl = chnl;
		this.manifestFunction = manifestFunction;
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		if (getIterableElement()==null) {
			throw new OutputWriteFailedException("no mutable element set");
		}
		
		Stack stack = new Stack( getIterableElement() );
		return stack;
	}

	@Override
	public Chnl getIterableElement() {
		return chnl;
	}

	@Override
	public void setIterableElement(Chnl element) {
		this.chnl = element;
	}

	@Override
	public ObjectGenerator<Stack> getGenerator() {
		return this;
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", manifestFunction);
	}
	
	@Override
	public void start() throws OutputWriteFailedException {
	}


	@Override
	public void end() throws OutputWriteFailedException {
	}

	@Override
	public boolean isRGB() {
		return false;
	}

}
