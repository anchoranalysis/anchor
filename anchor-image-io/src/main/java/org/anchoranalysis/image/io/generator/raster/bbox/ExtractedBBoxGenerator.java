package org.anchoranalysis.image.io.generator.raster.bbox;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class ExtractedBBoxGenerator extends RasterGenerator implements IterableObjectGenerator<BoundingBox,Stack> {

	private Stack stack = null;
	private String manifestFunction;
	private BoundingBox bbox;
	private int paddingXY = 0;
	private int paddingZ = 0;
	
	/**
	 * 
	 * @param stack
	 * @param manifestFunction
	 * @param factory must match the type of the input stack
	 * @throws CreateException
	 */
	public ExtractedBBoxGenerator(Stack stack, String manifestFunction) throws CreateException {
		super();
		//
		this.stack = stack;
		this.manifestFunction = manifestFunction;
	}
	
	private Stack createExtract( Stack stackIn ) throws CreateException {
		Stack stackOut = new Stack();
		
		for( Chnl chnlIn : stackIn ) {
			
			VoxelBox<?> vbIn = chnlIn.getVoxelBox().any();
			
			VoxelBox<?> vbExtracted = vbIn.createBufferAlwaysNew(bbox);
			
			Chnl chnlExtracted = ChnlFactory.instance().create( vbExtracted, stackIn.getDimensions().getRes() );
			try {
				stackOut.addChnl(chnlExtracted);
			} catch (IncorrectImageSizeException e) {
				throw new CreateException(e);
			}
		}
		
		return stackOut;
	}
	

	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		if (getIterableElement()==null) {
			throw new OutputWriteFailedException("no mutable element set");
		}

		try {
			return createExtract(stack);
		} catch (CreateException e) {
			throw new OutputWriteFailedException(e);
		}
	}

	@Override
	public BoundingBox getIterableElement() {
		return bbox;
	}

	@Override
	public void setIterableElement(BoundingBox element) {
		this.bbox = element;
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
		return stack.getNumChnl()==3;
	}

	public int getPaddingXY() {
		return paddingXY;
	}

	public void setPaddingXY(int paddingXY) {
		this.paddingXY = paddingXY;
	}

	public int getPaddingZ() {
		return paddingZ;
	}

	public void setPaddingZ(int paddingZ) {
		this.paddingZ = paddingZ;
	}
}
