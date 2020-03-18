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
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class RGBOrientationWriter extends ObjMaskWriter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1732346635451171139L;
	
	// START BEAN PROPERTIES
	@BeanField
	private double xDiv = 1;
	
	@BeanField
	private boolean drawReverseLine = false;
	// END BEAN PROPERTIES
	
	public RGBOrientationWriter() {
		
	}

	public static Point3d calcPoint(ObjMaskWithProperties mask, String propertyName) {
		
		if (!mask.hasProperty(propertyName)) {
			return null;
		}
		
		return new Point3d( (Point3d) mask.getProperty(propertyName) );
	}

	public static Double calcOrientation(ObjMaskWithProperties mask) {
		
		if (!mask.hasProperty("orientationRadians")) {
			return null;
		}
		
		return (Double) mask.getProperty("orientationRadians");
	}
	
	
	@Override
	public PrecalcOverlay precalculate(ObjMaskWithProperties mask,
			ImageDim dim) throws CreateException {
		return new PrecalcOverlay(mask) {

			@Override
			public void writePrecalculatedMask(RGBStack stack, IDGetter<ObjMaskWithProperties> idGetter,
					IDGetter<ObjMaskWithProperties> colorIDGetter, int iter, ColorIndex colorIndex,
					BoundingBox bboxContainer) throws OperationFailedException {

				Point3i midpoint = RGBMidpointWriter.calcMidpoint( mask, false );
				if (midpoint==null) {
					return;
				}
				
				Double orientationRadians = calcOrientation( mask );
				if (orientationRadians==null) {
					return;
				}
				
				Point3d xAxisMin = calcPoint(mask, "xAxisMin");
				if (xAxisMin==null) {
					return;
				}
				
				Point3d xAxisMax = calcPoint(mask, "xAxisMax");
				if (xAxisMax==null) {
					return;
				}
				
				writeOrientationLine(  midpoint, orientationRadians, colorIndex.get( colorIDGetter.getID(mask, iter) ), stack, bboxContainer, xAxisMin, xAxisMax );
				
				// Reverse
				if (drawReverseLine) {
					writeOrientationLine( midpoint, orientationRadians, colorIndex.get( colorIDGetter.getID(mask, iter) ), stack, bboxContainer, xAxisMin, xAxisMax );
				}
				
			}
			
		};
	}
	
	private void writeOrientationLine( Point3i midpoint, double orientationRadians, RGBColor color, RGBStack stack, BoundingBox bbox, Point3d min, Point3d max ) {
		
		// We start at 0
		double x = midpoint.getX();
		double y = midpoint.getY();
		
		double xIncr = Math.cos( orientationRadians ) * xDiv;
		double yIncr = Math.sin( orientationRadians ) * xDiv;
			
		while( true ) {
			int xI = (int) x;
			int yI = (int) y;

			if (!bbox.containsX(xI)) {
				break;
			}

			if (!bbox.containsY(yI)) {
				break;
			}
			
			if ( (xI < min.getX() || xI > max.getX()) && (yI < min.getY() || yI > max.getY())) {
				break;
			}
		
			RGBMidpointWriter.writeRelPoint( new Point3i(xI,yI,0), color, stack, bbox );
						
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
