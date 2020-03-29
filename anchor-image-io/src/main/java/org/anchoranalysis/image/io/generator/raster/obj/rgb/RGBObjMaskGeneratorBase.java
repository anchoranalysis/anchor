package org.anchoranalysis.image.io.generator.raster.obj.rgb;

import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;

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

import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithPropertiesCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public abstract class RGBObjMaskGeneratorBase extends RasterGenerator implements IterableObjectGenerator<ObjMaskWithPropertiesCollection,Stack> {

	// Iterable element
	private ObjMaskWithPropertiesCollection masks;
	
	private ObjMaskWriter objMaskWriter;
	private ColorIndex colorIndex;
	private IDGetter<ObjMaskWithProperties> idGetter;
	private IDGetter<ObjMaskWithProperties> colorIDGetter;
	
	protected RGBObjMaskGeneratorBase(ObjMaskWriter objMaskWriter, ColorIndex colorIndex, IDGetter<ObjMaskWithProperties> idGetter, IDGetter<ObjMaskWithProperties> colorIDGetter) {
		super();
		this.objMaskWriter = objMaskWriter;
		this.colorIndex = colorIndex;
		this.idGetter = idGetter;
		this.colorIDGetter = colorIDGetter;
	}
	
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		try {
			RGBStack backgroundRGB = generateBackground();
			objMaskWriter.write(
				generateMasks(),
				backgroundRGB,
				colorIndex,
				idGetter,
				colorIDGetter
			);
			return backgroundRGB.asStack();
		} catch (OperationFailedException | CreateException e) {
			throw new OutputWriteFailedException(e);
		}
	}
	
	@Override
	public ObjMaskWithPropertiesCollection getIterableElement() {
		return masks;
	}

	@Override
	public void setIterableElement(ObjMaskWithPropertiesCollection element) {
		this.masks = element;
	}

	@Override
	public ObjectGenerator<Stack> getGenerator() {
		return this;
	}

	@Override
	public void start() throws OutputWriteFailedException {
	}


	@Override
	public void end() throws OutputWriteFailedException {
	}

	@Override
	public boolean isRGB() {
		return true;
	}
	

	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("raster", "rgbObjMask");
	}
	
	protected abstract RGBStack generateBackground() throws CreateException;
	
	protected abstract ObjMaskWithPropertiesCollection generateMasks() throws CreateException;
}
