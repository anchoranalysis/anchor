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
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.outline.FindOutline;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class RGBBBoxOutlineWriter extends ObjMaskWriter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 790220184130344304L;
	
	// START BEAN PROPERTIES
	@BeanField
	private int outlineWidth;
	// END BEAN PROPERTIES
	
	public RGBBBoxOutlineWriter() {
		this(1);
	}
	
	public RGBBBoxOutlineWriter(int outlineWidth) {
		super();
		this.outlineWidth = outlineWidth;
	}


	private ObjMask createBBoxMask( ObjMask mask ) {

		ObjMask bbox = mask.duplicate();
		bbox.getVoxelBox().setAllPixelsTo(1);
		return bbox;
	}
	
	@Override
	public PrecalcOverlay precalculate(ObjMaskWithProperties mask, ImageDim dim)
			throws CreateException {
		ObjMask outline = FindOutline.outline(
			createBBoxMask(mask.getMask()),
			outlineWidth,
			true,
			dim.getZ() > 1
		);
		
		return new PrecalcOverlay(mask) {

			@Override
			public void writePrecalculatedMask(RGBStack stack, IDGetter<ObjMaskWithProperties> idGetter,
					IDGetter<ObjMaskWithProperties> colorIDGetter, int iter, ColorIndex colorIndex,
					BoundingBox bboxContainer) throws OperationFailedException {
				
				IntersectionWriter.writeRGBMaskIntersection(
						outline,
						colorIndex.get( colorIDGetter.getID(mask, iter) ),
						stack,
						bboxContainer
					);
			}
			
		};
	}
	
	public int getOutlineWidth() {
		return outlineWidth;
	}

	public void setOutlineWidth(int outlineWidth) {
		this.outlineWidth = outlineWidth;
	}



}
