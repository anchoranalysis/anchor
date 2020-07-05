package org.anchoranalysis.image.io.generator.raster;

import java.util.Optional;

/*
 * #%L
 * anchor-image-io
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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class StackGenerator extends RasterGenerator implements IterableObjectGenerator< Stack, Stack> {

	private Stack stackIn;
	private boolean padIfNec;
	private String manifestFunction;
	
	// Won't do any padding
	public StackGenerator(String manifestFunction) {
		this(false,manifestFunction);
	}
	
	public StackGenerator(boolean padIfNec, String manifestFunction) {
		super();
		this.padIfNec = padIfNec;
		this.manifestFunction = manifestFunction;
	}
	
	
	// Notes pads the passed channel, would be better if it makes a new stack first
	public StackGenerator(Stack stack, boolean padIfNec, String manifestFunction) {
		super();
		this.stackIn = stack;
		this.padIfNec = padIfNec;
		this.manifestFunction = manifestFunction;
	}

	public static Stack generateImgStack( Stack stackIn, boolean padIfNec ) throws OutputWriteFailedException {
		Stack stackOut = new Stack();
		
		try {
			for (int c=0; c<stackIn.getNumChnl(); c++) {
				stackOut.addChnl( stackIn.getChnl(c) );
			}
		} catch (IncorrectImageSizeException e) {
			throw new OutputWriteFailedException(e);
		}
		
		try {
			if (padIfNec && stackOut.getNumChnl()==2) {
				stackOut.addBlankChnl();
			}
		} catch (OperationFailedException e) {
			throw new OutputWriteFailedException(e);
		}

		return stackOut;
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		assert( stackIn!=null);
		return generateImgStack( stackIn, padIfNec);
	}

	@Override
	public Optional<ManifestDescription> createManifestDescription() {
		return Optional.of(
			new ManifestDescription("raster", manifestFunction)
		);
	}


	@Override
	public ObjectGenerator<Stack> getGenerator() {
		return this;
	}

	@Override
	public Stack getIterableElement() {
		return stackIn;
	}

	@Override
	public void setIterableElement(Stack element) {
		this.stackIn = element;
	}

	@Override
	public void end() throws OutputWriteFailedException {
		this.stackIn = null;
	}

	@Override
	public boolean isRGB() {
		return stackIn.getNumChnl()==3 || (stackIn.getNumChnl()==2 && padIfNec);
	}

}
