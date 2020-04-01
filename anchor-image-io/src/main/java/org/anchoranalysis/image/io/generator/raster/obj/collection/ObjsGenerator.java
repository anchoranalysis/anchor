package org.anchoranalysis.image.io.generator.raster.obj.collection;

/*-
 * #%L
 * anchor-image-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


/**
 * Base class for generators that accept a set of objects as input
 * 
 * @author owen
 *
 */
public abstract class ObjsGenerator extends RasterGenerator implements IterableGenerator<ObjMaskCollection> {
	
	private ObjMaskCollection objs;
	private ImageDim dim;
	
	public ObjsGenerator(ImageDim dim) {
		this.dim = dim;
	}
	
	public ObjsGenerator(ObjMaskCollection objs, ImageDim dim) {
		this(dim);
		this.objs = objs;
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
		return objs;
	}

	@Override
	public void setIterableElement(ObjMaskCollection element)
			throws SetOperationFailedException {
		this.objs = element;
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

	protected ObjMaskCollection getObjs() {
		return objs;
	}
}
