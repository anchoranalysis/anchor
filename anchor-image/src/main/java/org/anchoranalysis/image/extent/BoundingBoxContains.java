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
 * Does a bounding box contain other objects? e.g. points, other bounding boxes etc.
 * 
 * @author Owen Feehan
 *
 */
public final class BoundingBoxContains {

	private final BoundingBox bbox;
	private final ReadableTuple3i crnrMax;
	
	public BoundingBoxContains(BoundingBox bbox) {
		super();
		this.bbox = bbox;
		this.crnrMax = bbox.calcCornerMax();
	}
	
	/** Is this value in the x-dimension within the bounding box range? */
	public boolean x( int x ) {
		return (x>= bbox.getCornerMin().getX()) && (x<=crnrMax.getX()); 
	}

	/** Is this value in the y-dimension within the bounding box range? */
	public boolean y( int y ) {
		return (y>= bbox.getCornerMin().getY()) && (y<=crnrMax.getY()); 
	}
	
	/** Is this value in the z-dimension within the bounding box range? */
	public boolean z( int z ) {
		return (z>= bbox.getCornerMin().getZ()) && (z<=crnrMax.getZ()); 
	}
	
	/** Is this point within the bounding-box? */
	public boolean point( ReadableTuple3i pnt ) {
		return x( pnt.getX() ) && y( pnt.getY() ) && z( pnt.getZ() );
	}

	/** Is this point within the bounding-box, but ignoring the z-dimension? */
	public boolean pointIgnoreZ( Point3i pnt )  {
		return x( pnt.getX() ) && y( pnt.getY() );
	}
	
	/** Is this other bounding-box FULLY contained within this bounding box? */ 
	public boolean box( BoundingBox maybeContainedInside ) {
		return point( maybeContainedInside.getCornerMin() ) && point( maybeContainedInside.calcCornerMax() );
	}
}
