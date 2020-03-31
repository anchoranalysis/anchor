package org.anchoranalysis.io.bean.objmask.writer;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class ObjMaskListWriter extends ObjMaskWriter {

	// Currently no way to add objects in bean mode
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4458953467764493258L;
	
	// START BEAN PROPERTIES
	@BeanField
	private List<ObjMaskWriter> list;
	// END BEAN PROPERTIES
	
	public ObjMaskListWriter() {
		
	}
	
	public ObjMaskListWriter(List<ObjMaskWriter> list) {
		super();
		this.list = list;
	}

	@Override
	public PrecalcOverlay precalculate(ObjMaskWithProperties mask,
			ImageDim dim) throws CreateException {

		List<PrecalcOverlay> listPrecalc = new ArrayList<>();
		
		for( ObjMaskWriter writer : list) {
			listPrecalc.add( writer.precalculate(mask, dim) );
		}
		
		return new PrecalcOverlay(mask) {

			@Override
			public void writePrecalculatedMask(RGBStack stack, IDGetter<ObjMaskWithProperties> idGetter,
					IDGetter<ObjMaskWithProperties> colorIDGetter, int iter, ColorIndex colorIndex,
					BoundingBox bboxContainer) throws OperationFailedException {

				for(PrecalcOverlay preCalc : listPrecalc) {
					preCalc.writePrecalculatedMask(stack, idGetter, colorIDGetter, iter, colorIndex, bboxContainer);
				}
				
			}
			
		};
	}

	public List<ObjMaskWriter> getList() {
		return list;
	}

	public void setList(List<ObjMaskWriter> list) {
		this.list = list;
	}
}
