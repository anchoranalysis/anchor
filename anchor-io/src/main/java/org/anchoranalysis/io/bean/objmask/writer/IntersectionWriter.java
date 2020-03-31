package org.anchoranalysis.io.bean.objmask.writer;

/*-
 * #%L
 * anchor-io
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

import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.stack.rgb.RGBStack;

class IntersectionWriter {

	// Writes only to the intersection of mask and stack (positioned at stackBBox)
	public static void writeRGBMaskIntersection( ObjMask mask, RGBColor color, RGBStack stack, BoundingBox stackBBox ) throws OperationFailedException {

		if( !stackBBox.hasIntersection( mask.getBoundingBox() )) {
			throw new OperationFailedException(
				String.format(
					"The bounding-box of the mask (%s) does not intersect with the stack (%s)",
					mask.getBoundingBox().toString(),
					stackBBox.toString()
				)
			);
		}
		// Intersection of the mask and stackBBox
		BoundingBox intersection = mask.getBoundingBox().intersectCreateNewNoClip(stackBBox);
		
		// Let's make the intersection relative to the stack
		intersection.getCrnrMin().sub( stackBBox.getCrnrMin() );
		mask.getBoundingBox().getCrnrMin().sub( stackBBox.getCrnrMin() );
		
		Point3i maxGlobal = intersection.calcCrnrMax();
		Point3i pntGlobal = new Point3i();
		
		assert(mask.sizesMatch());
		
		
		for (pntGlobal.setZ(intersection.getCrnrMin().getZ()); pntGlobal.getZ() <=maxGlobal.getZ(); pntGlobal.incrZ()) {
			int relZ = pntGlobal.getZ() - mask.getBoundingBox().getCrnrMin().getZ();
			stack.writeRGBMaskToSlice( mask, intersection, color, pntGlobal, relZ, maxGlobal);
		}
		
		mask.getBoundingBox().getCrnrMin().add( stackBBox.getCrnrMin() );
	}
}
