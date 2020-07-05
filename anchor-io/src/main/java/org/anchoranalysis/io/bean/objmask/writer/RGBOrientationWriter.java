package org.anchoranalysis.io.bean.objmask.writer;

import java.util.Optional;

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
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class RGBOrientationWriter extends ObjMaskWriter {

	// START BEAN PROPERTIES
	@BeanField
	private double xDiv = 1;
	
	@BeanField
	private boolean drawReverseLine = false;
	// END BEAN PROPERTIES
	
	public static Optional<Point3d> calcPoint(ObjectWithProperties mask, String propertyName) {
		
		if (!mask.hasProperty(propertyName)) {
			return Optional.empty();
		}
		
		return Optional.of(
			new Point3d( (Point3d) mask.getProperty(propertyName) )
		);
	}

	public static Optional<Double> calcOrientation(ObjectWithProperties mask) {
		
		if (!mask.hasProperty("orientationRadians")) {
			return Optional.empty();
		}
		
		return Optional.of(
			(Double) mask.getProperty("orientationRadians")
		);
	}
	
	
	@Override
	public PrecalcOverlay precalculate(ObjectWithProperties mask,
			ImageDimensions dim) throws CreateException {
		return new PrecalcOverlay(mask) {

			@Override
			public void writePrecalculatedMask(RGBStack stack, IDGetter<ObjectWithProperties> idGetter,
					IDGetter<ObjectWithProperties> colorIDGetter, int iter, ColorIndex colorIndex,
					BoundingBox bboxContainer) throws OperationFailedException {

				Point3i midpoint = RGBMidpointWriter.calcMidpoint( mask, false );
				if (midpoint==null) {
					return;
				}
				
				Optional<Double> orientationRadians = calcOrientation( mask );
				if (!orientationRadians.isPresent()) {
					return;
				}
				
				Optional<Point3d> xAxisMin = calcPoint(mask, "xAxisMin");
				if (!xAxisMin.isPresent()) {
					return;
				}
				
				Optional<Point3d> xAxisMax = calcPoint(mask, "xAxisMax");
				if (!xAxisMax.isPresent()) {
					return;
				}
				
				writeOrientationLine(
					midpoint,
					orientationRadians.get(),
					colorIndex.get( colorIDGetter.getID(mask, iter) ),
					stack,
					bboxContainer,
					xAxisMin.get(),
					xAxisMax.get()
				);
				
				// Reverse
				if (drawReverseLine) {
					writeOrientationLine(
						midpoint,
						orientationRadians.get(),
						colorIndex.get( colorIDGetter.getID(mask, iter) ),
						stack,
						bboxContainer,
						xAxisMin.get(),
						xAxisMax.get()
					);
				}
				
			}
			
		};
	}
	
	private void writeOrientationLine(
		Point3i midpoint,
		double orientationRadians,
		RGBColor color,
		RGBStack stack,
		BoundingBox bbox,
		Point3d min,
		Point3d max
	) {
		
		// We start at 0
		double x = midpoint.getX();
		double y = midpoint.getY();
		
		double xIncr = Math.cos( orientationRadians ) * xDiv;
		double yIncr = Math.sin( orientationRadians ) * xDiv;
		
		while( true ) {
			Point3i pnt = new Point3i( (int) x, (int) y, 0);

			if (!bbox.contains().x(pnt.getX()) || !bbox.contains().y(pnt.getY())) {
				break;
			}
			
			if ( (pnt.getX() < min.getX() || pnt.getX() > max.getX()) && (pnt.getY() < min.getY() || pnt.getY() > max.getY())) {
				break;
			}
		
			RGBMidpointWriter.writeRelPoint(pnt, color, stack, bbox );
						
			x += xIncr;
			y += yIncr;
		}
	}
	
	public double getxDiv() {
		return xDiv;
	}

	public void setxDiv(double xDiv) {
		this.xDiv = xDiv;
	}

	public boolean isDrawReverseLine() {
		return drawReverseLine;
	}

	public void setDrawReverseLine(boolean drawReverseLine) {
		this.drawReverseLine = drawReverseLine;
	}



}
