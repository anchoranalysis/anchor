package org.anchoranalysis.image.io.generator.raster.objmask;

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
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.objmask.ops.BinaryChnlFromObjs;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class ObjMaskCollectionGenerator extends RasterGenerator implements IterableGenerator<ObjMaskCollection> {

	private ObjMaskCollection masks;
	private ImageDim dim;
	
	// Constructor
	public ObjMaskCollectionGenerator(ImageDim dim) {
		this.dim = dim;
	}
	
	// Constructor
	public ObjMaskCollectionGenerator(ObjMaskCollection masks, ImageDim dim) {
		this(dim);
		this.masks = masks;
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		try {
			BinaryChnl chnl = BinaryChnlFromObjs.createFromObjs(masks, dim, BinaryValues.getDefault() );
			return new ChnlGenerator(chnl.getChnl(), "maskCollection").generate();
		} catch (CreateException e) {
			throw new OutputWriteFailedException(e);
		}
		
		
	}

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", "maskCollection");
	}

	@Override
	public boolean isRGB() {
		return false;
	}

	@Override
	public ObjMaskCollection getIterableElement() {
		return masks;
	}

	@Override
	public void setIterableElement(ObjMaskCollection element)
			throws SetOperationFailedException {
		this.masks = element;
	}

	@Override
	public void start() throws OutputWriteFailedException {
		
	}

	@Override
	public void end() throws OutputWriteFailedException {
		
	}

	@Override
	public Generator getGenerator() {
		return this;
	}

	public ImageDim getDimensions() {
		return dim;
	}
}
