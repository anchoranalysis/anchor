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
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.outline.FindOutline;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class RGBOutlineWriter extends ObjMaskWriter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2945435530773944398L;
	
	// START BEAN PROPERTIES
	@BeanField
	private int outlineWidth;
	
	@BeanField
	private boolean force2D = false;
	// END BEAN PROPERTIES
	
	public RGBOutlineWriter() {
		this(1);
	}
	
	public RGBOutlineWriter(int outlineWidth) {
		this(outlineWidth,false);
		
	}
	
	public RGBOutlineWriter(int outlineWidth, boolean force2D) {
		this.outlineWidth = outlineWidth;
		this.force2D = force2D;
	}

	
	@Override
	public PrecalcOverlay precalculate(ObjMaskWithProperties mask, ImageDim dim)
			throws CreateException {
		
		ObjMask om = FindOutline.outline(
			mask.getMask(),
			outlineWidth,
			true,
			(dim.getZ() > 1) && !force2D
		);
		return new PrecalcOverlay(
			mask,
			new ObjMaskWithProperties(om, mask.getProperties())
		);
	}

	@Override
	public void writePrecalculatedMask(PrecalcOverlay precalculatedObj,
		RGBStack stack,
		IDGetter<ObjMaskWithProperties> idGetter,
		IDGetter<ObjMaskWithProperties> colorIDGetter,
		int iter,
		ColorIndex colorIndex,
		BoundingBox bboxContainer
	)
	throws OperationFailedException {
		
		ObjMaskWithProperties preCast = (ObjMaskWithProperties) precalculatedObj.getSecond();
		
		ObjMask om = preCast.getMask();
		
		assert( preCast.getVoxelBox().extnt().getZ() > 0 );
		assert( colorIDGetter!=null );
		assert( colorIndex!=null );
		
		// TODO this can get broken! Fix!
		assert( om.getBoundingBox().getCrnrMin().getZ()>=0 );
		
		ObjMaskWithProperties maskOrig = precalculatedObj.getFirst();
		int colorID = colorIDGetter.getID(maskOrig, iter);
		assert( colorIndex.has(colorID) );
		
		RGBColor color = colorIndex.get( colorID );
		
		IntersectionWriter.writeRGBMaskIntersection(
			om,
			color,
			stack,
			bboxContainer
		);
	}
	
	public boolean isForce2D() {
		return force2D;
	}

	public void setForce2D(boolean force2d) {
		force2D = force2d;
	}

	public int getOutlineWidth() {
		return outlineWidth;
	}

	public void setOutlineWidth(int outlineWidth) {
		this.outlineWidth = outlineWidth;
	}
}
