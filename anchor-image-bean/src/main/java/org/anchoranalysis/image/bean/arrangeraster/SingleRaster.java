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

import org.anchoranalysis.image.bean.nonbean.arrangeraster.ArrangeRasterException;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.BBoxSetOnPlane;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class SingleRaster extends ArrangeRasterBean {

	@Override
	public BBoxSetOnPlane createBBoxSetOnPlane(
			Iterator<RGBStack> rasterIterator)
			throws ArrangeRasterException {
	
		if (!rasterIterator.hasNext()) {
			throw new ArrangeRasterException("iterator has no more rasters");
		}
		
		RGBStack stack = rasterIterator.next();

		Extent extent = stack.getChnl(0).getDimensions().getExtnt();
		
		return new BBoxSetOnPlane(
			extent,
			new BoundingBox(extent)
		);
	}

}
