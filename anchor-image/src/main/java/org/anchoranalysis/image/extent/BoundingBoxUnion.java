package org.anchoranalysis.image.extent;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;


/**
 * Performs union of a bounding-box with other entities
 * 
 * @author Owen Feehan
 *
 */
public class BoundingBoxUnion {

private final BoundingBox bbox;
	
	public BoundingBoxUnion(BoundingBox bbox) {
		super();
		this.bbox = bbox;
	}
	
	/**
	 * Performs a union with another box (immutably)
	 * 
	 * @param other the other bounding box
	 * @return a new bounding-box that is union of both bounding boxes
	 */
	public BoundingBox with( BoundingBox other ) {
		
		ReadableTuple3i crnrMin = bbox.getCornerMin();
		ReadableTuple3i crnrMinOther = other.getCornerMin();
		
		ReadableTuple3i crnrMax = bbox.calcCornerMax();
		ReadableTuple3i crnrMaxOthr = other.calcCornerMax();
		
		ExtentBoundsComparer meiX = ExtentBoundsComparer.createMax(crnrMin, crnrMinOther, crnrMax, crnrMaxOthr, p->p.getX() );
		ExtentBoundsComparer meiY = ExtentBoundsComparer.createMax(crnrMin, crnrMinOther, crnrMax, crnrMaxOthr, p->p.getY() );
		ExtentBoundsComparer meiZ = ExtentBoundsComparer.createMax(crnrMin, crnrMinOther, crnrMax, crnrMaxOthr, p->p.getZ() );

		return new BoundingBox(
			new Point3i(
				meiX.getMin(),
				meiY.getMin(),
				meiZ.getMin()
			),
			new Extent(
				meiX.getExtnt(),
				meiY.getExtnt(),
				meiZ.getExtnt()
			)
		);
	}
}
