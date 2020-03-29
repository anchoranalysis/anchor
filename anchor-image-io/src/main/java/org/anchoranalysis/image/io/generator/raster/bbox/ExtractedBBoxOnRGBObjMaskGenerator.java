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
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.rgb.RGBObjMaskGenerator;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithPropertiesCollection;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class ExtractedBBoxOnRGBObjMaskGenerator extends RasterGenerator implements IterableObjectGenerator<ObjMask,Stack> {

	private ExtractedBBoxGenerator chnlGenerator;
	private RGBObjMaskGenerator rgbObjMaskGenerator;
	
	private ObjMask element;
	private String manifestFunction;
	private boolean mip;
	
	public ExtractedBBoxOnRGBObjMaskGenerator(RGBObjMaskGenerator rgbObjMaskGenerator, ExtractedBBoxGenerator chnlGenerator, String manifestFunction,  boolean mip ) throws CreateException {
		super();
		this.rgbObjMaskGenerator = rgbObjMaskGenerator;
		this.chnlGenerator = chnlGenerator;
		this.manifestFunction = manifestFunction;
		this.mip = mip;
	}

	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		if (getIterableElement()==null) {
			throw new OutputWriteFailedException("no mutable element set");
		}
		
		chnlGenerator.setIterableElement(element.getBoundingBox());

		Stack chnlExtracted = chnlGenerator.generate();
		
		if (mip) {
			chnlExtracted = chnlExtracted.maxIntensityProj();
		}
		
		// We apply the generator
		try {
			rgbObjMaskGenerator.setBackground( DisplayStack.create(chnlExtracted) );
		} catch (CreateException e) {
			throw new OutputWriteFailedException(e);
		}

		ObjMask om = this.getIterableElement();
		
		if (mip) {
			om = om.flattenZ();
		}
		
		// We create a version that is relative to the extracted section
		ObjMask omLocal = new ObjMask( new BoundingBox(om.getVoxelBox().extnt()), om.binaryVoxelBox() );
		
		ObjMaskWithPropertiesCollection objs = new ObjMaskWithPropertiesCollection();
		objs.add( new ObjMaskWithProperties( omLocal ));
		
		rgbObjMaskGenerator.setIterableElement( objs );
		
		Stack stack = rgbObjMaskGenerator.generate();

		return stack;
	}

	@Override
	public ObjMask getIterableElement() {
		return element;
	}

	@Override
	public void setIterableElement(ObjMask element) {
		this.element = element;
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
		return rgbObjMaskGenerator.isRGB();
	}

}
