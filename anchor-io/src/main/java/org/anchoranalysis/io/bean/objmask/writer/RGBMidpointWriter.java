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
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objectmask.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class RGBMidpointWriter extends ObjMaskWriter {

	private static final String PROPERTY_MIDPOINT = "midpointInt";
	
	// START BEAN PROEPRTIES
	@BeanField
	private int extraLength = 2;
	// END BEAN PROPERTIES
	
	public RGBMidpointWriter() {
		
	}
	
	public RGBMidpointWriter( int extraLength ) {
		this.extraLength = extraLength;
	}

	public static Point3i calcMidpoint(ObjectWithProperties mask, boolean suppressZ) {

		return maybeSuppressZ(
			calcMidpoint3D(mask),
			suppressZ
		);
	}
	
	private static Point3i calcMidpoint3D(ObjectWithProperties mask) {
		if (mask.hasProperty(PROPERTY_MIDPOINT)) {
			return Point3i.immutableAdd(
				(Point3i) mask.getProperty(PROPERTY_MIDPOINT),
				mask.getBoundingBox().getCrnrMin()
			);
		} else {
			return PointConverter.intFromDouble( mask.getMask().centerOfGravity() );
		}
	}
	
	private static Point3i maybeSuppressZ( Point3i pnt, boolean suppressZ ) {
		if (suppressZ) {
			pnt.setZ(0);
		}
		return pnt;
	}
	
	@Override
	public PrecalcOverlay precalculate(ObjectWithProperties mask,
			ImageDim dim) throws CreateException {
		
		// We ignore the z-dimension so it's projectable onto a 2D slice
		Point3i midPoint = calcMidpoint( mask, true );
		
		return new PrecalcOverlay(mask) {

			@Override
			public void writePrecalculatedMask(RGBStack stack, IDGetter<ObjectWithProperties> idGetter,
					IDGetter<ObjectWithProperties> colorIDGetter, int iter, ColorIndex colorIndex,
					BoundingBox bboxContainer) throws OperationFailedException {

				if (midPoint==null) {
					return;
				}
				
				writeCross(
					midPoint,
					colorIndex.get( colorIDGetter.getID(mask, iter) ),
					stack,
					extraLength,
					bboxContainer
				);
			}
			
		};
	}
	
	public static void writeRelPoint( Point3i pnt, RGBColor color, RGBStack stack, BoundingBox bboxContainer ) {
		if (bboxContainer.contains().point(pnt)) {
			stack.writeRGBPoint(
				Point3i.immutableSubtract(pnt, bboxContainer.getCrnrMin()),
				color
			);
		}
	}

	public static void writeCross( Point3i midpoint, RGBColor color, RGBStack stack, int extraLength, BoundingBox bboxContainer ) {
		
		if (!stack.getDimensions().contains(midpoint)) {
			return;
		}
		
		stack.writeRGBPoint( midpoint, color);
		
		// X direction
		for (int i=0; i<extraLength; i++) {
			midpoint.decrX();
			writeRelPoint( midpoint, color, stack, bboxContainer);
		}
		midpoint.incrementX(extraLength);
		
		for (int i=0; i<extraLength; i++) {
			midpoint.incrementX();
			writeRelPoint( midpoint, color, stack, bboxContainer);
		}
		midpoint.decrX(extraLength);
		
		
		// Y direction
		for (int i=0; i<extraLength; i++) {
			midpoint.decrY();
			writeRelPoint( midpoint, color, stack, bboxContainer);
		}
		midpoint.incrementY(extraLength);
		
		for (int i=0; i<extraLength; i++) {
			midpoint.decrY();
			writeRelPoint( midpoint, color, stack, bboxContainer);
		}
		midpoint.decrY( extraLength );
		
	}
	
	public int getExtraLength() {
		return extraLength;
	}

	public void setExtraLength(int extraLength) {
		this.extraLength = extraLength;
	}



}
