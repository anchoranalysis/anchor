package org.anchoranalysis.io.bean.objmask.writer;

import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;

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



import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

// Note doesn't cache the underlying maskWriter
public class MIPWriter extends ObjMaskWriter {

	// START BEAN PROPERTIES
	@BeanField
	private ObjMaskWriter maskWriter;
	// END BEAN PROPERTIES
	
	public MIPWriter() {
		
	}
	
	public MIPWriter(ObjMaskWriter maskWriter) {
		super();
		this.maskWriter = maskWriter;
	}
	
	@Override
	public PrecalcOverlay precalculate(ObjMaskWithProperties mask, ImageDim dim)
			throws CreateException {
		
		ObjMaskWithProperties maskMIP = createMIPMask(mask);
		
		return new PrecalcOverlay(mask) {

			@Override
			public void writePrecalculatedMask(RGBStack stack, IDGetter<ObjMaskWithProperties> idGetter,
					IDGetter<ObjMaskWithProperties> colorIDGetter, int iter, ColorIndex colorIndex,
					BoundingBox bboxContainer) throws OperationFailedException {
				maskWriter.writeSingle( (ObjMaskWithProperties) maskMIP, stack, idGetter, colorIDGetter, iter, colorIndex, bboxContainer);
			}
			
		};
	}
	
	public ObjMaskWriter getMaskWriter() {
		return maskWriter;
	}

	public void setMaskWriter(ObjMaskWriter maskWriter) {
		this.maskWriter = maskWriter;
	}

	private ObjMaskWithProperties createMIPMask( ObjMaskWithProperties mask ) {

		// Now we manipulate the mask
		ObjMaskWithProperties copyMask = mask.duplicate();
		copyMask.convertToMaxIntensityProjection();
		return copyMask;
	}	
}
