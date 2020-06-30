package org.anchoranalysis.image.bean.arrangeraster;

/*
 * #%L
 * anchor-image-io
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


import java.util.Iterator;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.ArrangeRasterException;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.BBoxSetOnPlane;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.rgb.RGBStack;

// Overlays one image on the other
// FIRST image passed is assumed to be the source
// SECOND image passed is assumed to be the overlay
// We have no Z implemented yet, so we always overlay at z position 0
public class ArrangeRasterOverlay extends ArrangeRasterBean {

	

	// START BEAN PROPERTIES
	
	// left, right, center
	@BeanField
	private String horizontalAlign = "left";
	
	// top, bottom, center
	@BeanField
	private String verticalAlign = "top";
	
	// top, bottom, center
	@BeanField
	private String zAlign = "top";
	
	// END BEAN PROPERTIES
	
	@Override
	public String getBeanDscr() {
		return getBeanName();
	}

	private int calcHorizontalPos( BBoxSetOnPlane bboxSet, ImageDimensions dim ) {

		if (horizontalAlign.equalsIgnoreCase("left")) {
			return 0;
		} else if (horizontalAlign.equalsIgnoreCase("right")) {
			return bboxSet.getExtnt().getX() - dim.getX();
		} else {
			return(bboxSet.getExtnt().getX() - dim.getX()) / 2;
		}
	}
	
	private int calcVerticalPos( BBoxSetOnPlane bboxSet, ImageDimensions dim ) {

		if (verticalAlign.equalsIgnoreCase("top")) {
			return 0;
		} else if (verticalAlign.equalsIgnoreCase("bottom")) {
			return bboxSet.getExtnt().getY() - dim.getY();
		} else {
			return(bboxSet.getExtnt().getY() - dim.getY()) / 2;
		}
	}
	
	private int calcZPos( BBoxSetOnPlane bboxSet, ImageDimensions dim ) {

		if (zAlign.equalsIgnoreCase("bottom") || zAlign.equalsIgnoreCase("repeat")) {
			return 0;
		} else if (zAlign.equalsIgnoreCase("top")) {
			return bboxSet.getExtnt().getZ() - dim.getZ();
		} else {
			return(bboxSet.getExtnt().getZ() - dim.getZ()) / 2;
		}
	}
	
	@Override
	public BBoxSetOnPlane createBBoxSetOnPlane(
			Iterator<RGBStack> rasterIterator) throws ArrangeRasterException {

		if (!rasterIterator.hasNext()) {
			throw new ArrangeRasterException("No image in iterator for source");
		}

		SingleRaster sr = new SingleRaster();
		BBoxSetOnPlane bboxSet = sr.createBBoxSetOnPlane(rasterIterator);
		
		if (!rasterIterator.hasNext()) {
			throw new ArrangeRasterException("No image in iterator for overlay");
		}

		RGBStack overlayImg = rasterIterator.next();
		
		Extent overlayE = deriveExtent(
			overlayImg.getChnl(0).getDimensions().getExtnt(),
			bboxSet.getExtnt()
		);
				
		int hPos = calcHorizontalPos(bboxSet, overlayImg.getDimensions() );
		int vPos = calcVerticalPos(bboxSet, overlayImg.getDimensions() );
		int zPos = calcZPos(bboxSet, overlayImg.getDimensions() );
		
		bboxSet.add(
			new BoundingBox(
				new Point3i(hPos,vPos,zPos),
				overlayE
			)
		);
		return bboxSet;
	}

	private Extent deriveExtent( Extent overlay, Extent bbox ) {
		return new Extent(
			Math.min(overlay.getX(), bbox.getX()),
			Math.min(overlay.getY(), bbox.getY()),
			zAlign.equalsIgnoreCase("repeat") || (overlay.getZ() > bbox.getZ()) ? bbox.getZ() : overlay.getZ()	
		);
	}
	
	public String getHorizontalAlign() {
		return horizontalAlign;
	}

	public void setHorizontalAlign(String horizontalAlign) {
		this.horizontalAlign = horizontalAlign;
	}

	public String getVerticalAlign() {
		return verticalAlign;
	}

	public void setVerticalAlign(String verticalAlign) {
		this.verticalAlign = verticalAlign;
	}

	public String getzAlign() {
		return zAlign;
	}

	public void setzAlign(String zAlign) {
		this.zAlign = zAlign;
	}

}
