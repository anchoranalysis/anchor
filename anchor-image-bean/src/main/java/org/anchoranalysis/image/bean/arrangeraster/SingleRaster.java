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

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.arrangeraster.ArrangeRasterException;
import org.anchoranalysis.image.arrangeraster.BBoxSetOnPlane;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class SingleRaster extends ArrangeRasterBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1134691897037321537L;
	
	@Override
	public BBoxSetOnPlane createBBoxSetOnPlane(
			Iterator<RGBStack> rasterIterator)
			throws ArrangeRasterException {
	
		if (!rasterIterator.hasNext()) {
			throw new ArrangeRasterException("iterator has no more rasters");
		}
		
		RGBStack stack = rasterIterator.next();
		ImageDim sd = stack.getChnl(0).getDimensions();
		
		Point3i crnrMin = new Point3i( 0, 0, 0 );
		Extent extnt = new Extent( sd.getX(), sd.getY(), sd.getZ() );
		BoundingBox bbox = new BoundingBox(crnrMin, extnt);
		
		BBoxSetOnPlane set = new BBoxSetOnPlane();
		set.getExtnt().setX( sd.getX() );
		set.getExtnt().setY( sd.getY() );
		set.getExtnt().setZ( sd.getZ() );
		set.add(bbox);
		return set;
	}

}
