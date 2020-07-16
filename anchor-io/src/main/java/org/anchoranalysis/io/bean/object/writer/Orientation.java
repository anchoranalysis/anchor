package org.anchoranalysis.io.bean.object.writer;

import java.util.Optional;

import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
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
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

import lombok.Getter;
import lombok.Setter;

/**
 * Writes a line through the midpoint illustrating the orientation of the object
 * 
 * @author Owen Feehan
 *
 */
public class Orientation extends DrawObject {

	// START BEAN PROPERTIES
	@BeanField @Getter @Setter
	private double xDiv = 1;
	
	@BeanField @Getter @Setter
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
			public void writePrecalculatedMask(RGBStack background, ObjectDrawAttributes attributes, int iteration,
					BoundingBox restrictTo) throws OperationFailedException {
				
				Point3i midpoint = Midpoint.calcMidpoint( mask, false );
				
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
				
				RGBColor color = attributes.colorFor(mask, iteration);
				
				writeOrientationLine(
					midpoint,
					orientationRadians.get(),
					color,
					background,
					restrictTo
				);
				
				// Reverse
				if (drawReverseLine) {
					writeOrientationLine(
						midpoint,
						orientationRadians.get(),
						color,
						background,
						restrictTo
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
		BoundingBox bbox
	) {
		
		// We start at 0
		double x = midpoint.getX();
		double y = midpoint.getY();
		
		double xIncr = Math.cos( orientationRadians ) * xDiv;
		double yIncr = Math.sin( orientationRadians ) * xDiv;
		
		while( true ) {
			Point3i point = new Point3i( (int) x, (int) y, 0);

			if (bbox.contains().pointIgnoreZ(point)) {
				Midpoint.writeRelPoint(point, color, stack, bbox );
				
				x += xIncr;
				y += yIncr;
			} else {
				break;
			}
		}
	}
}
