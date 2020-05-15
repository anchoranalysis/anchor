package org.anchoranalysis.anchor.mpp.mark.conic;

import org.anchoranalysis.anchor.mpp.mark.conic.bounds.EllipsoidBounds;

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
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.orientation.Orientation3DEulerAngles;

public class EllipsoidRandomizer {

	public static Orientation3DEulerAngles randomizeRot(EllipsoidBounds bounds, RandomNumberGenerator re, ImageRes sr) {
		Orientation3DEulerAngles orientation3DEuler = new Orientation3DEulerAngles();
		orientation3DEuler.setRotXRadians(  randomizeRot(bounds, re, sr, 0) );
		orientation3DEuler.setRotYRadians(  randomizeRot(bounds, re, sr, 1) );
		orientation3DEuler.setRotYRadians(  randomizeRot(bounds, re, sr, 2) );
		return orientation3DEuler;
	}
	
	public static Point3d randomizeRadii(EllipsoidBounds bounds, RandomNumberGenerator re, ImageRes sr) {
		Point3d radii = new Point3d();
		radii.setX( randomizeRadius(bounds, re,sr) );
		radii.setY( randomizeRadius(bounds, re,sr) );
		radii.setZ( randomizeRadius(bounds, re,sr) );
		return radii;
	}
	
	private static double randomizeRadius(EllipsoidBounds bounds, RandomNumberGenerator re, ImageRes sr ) {
		return bounds.getRadius().rslv(sr, true).randOpen( re );
	}
	
	private static double randomizeRot( EllipsoidBounds bounds, RandomNumberGenerator re, ImageRes sr, int dim) {
		return bounds.getRotation(dim).rslv(sr, true).randOpen( re );
	}
}
