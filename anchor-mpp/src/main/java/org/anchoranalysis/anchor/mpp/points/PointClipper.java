package org.anchoranalysis.anchor.mpp.points;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.ImageDim;

/**
 * Ensures a point has values contained inside image-dimensions
 *
 */
public class PointClipper {
	
	public static Point3i clip( Point3i pnt, ImageDim dim ) {
		pnt = clipLow(pnt);
		pnt = clipHigh(pnt, dim);
		return pnt;
	}
	
	public static Point3d clip( Point3d pnt, ImageDim dim ) {
		pnt = clipLow(pnt);
		pnt = clipHigh(pnt, dim);
		return pnt;
	}
	
	private static Point3i clipLow( Point3i pnt ) {
		return pnt.max(0);	
	}
	
	private static Point3d clipLow( Point3d pnt ) {
		return pnt.max(0);	
	}
	
	private static Point3i clipHigh( Point3i pnt, ImageDim dim ) {
		return pnt.min(
			dim.getExtnt().createMinusOne().asTuple()
		);
	}
	
	private static Point3d clipHigh( Point3d pnt, ImageDim dim ) {
		return pnt.min(
			dim.getExtnt().createMinusOne().asTuple()
		);
	}
}
