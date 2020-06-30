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
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.outline.FindOutline;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class RGBOutlineWriter extends ObjMaskWriter {

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
	public PrecalcOverlay precalculate(ObjectWithProperties mask, ImageDimensions dim)
			throws CreateException {
		
		ObjectMask om = FindOutline.outline(
			mask.getMask(),
			outlineWidth,
			true,
			(dim.getZ() > 1) && !force2D
		);
		
		ObjectWithProperties omMask = new ObjectWithProperties(om, mask.getProperties());
		
		return new PrecalcOverlay(mask) {

			@Override
			public void writePrecalculatedMask(RGBStack stack, IDGetter<ObjectWithProperties> idGetter,
					IDGetter<ObjectWithProperties> colorIDGetter, int iter, ColorIndex colorIndex,
					BoundingBox bboxContainer) throws OperationFailedException {
				
				assert( om.getVoxelBox().extent().getZ() > 0 );
				assert( colorIDGetter!=null );
				assert( colorIndex!=null );
				
				// TODO this can get broken! Fix!
				assert( om.getBoundingBox().getCornerMin().getZ()>=0 );
				
				int colorID = colorIDGetter.getID(omMask, iter);
				assert( colorIndex.has(colorID) );
				
				RGBColor color = colorIndex.get( colorID );
				
				IntersectionWriter.writeRGBMaskIntersection(
					om,
					color,
					stack,
					bboxContainer
				);
			}
			
		};
			
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
