package org.anchoranalysis.anchor.mpp.mark.conic;

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;


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
import org.anchoranalysis.image.extent.ImageResolution;

/**
 * Utility functions for generating random radii for {@link MarkConic}
 * 
 * @author Owen Feehan
 *
 */
public class RadiiRandomizer {

	private RadiiRandomizer() {}
	
	public static Point3d randomizeRadii(
		Bound bound,
		RandomNumberGenerator re,
		ImageResolution sr,
		boolean do3D
	) {
		return new Point3d(
			randomizeRadius(bound,re,sr),
			randomizeRadius(bound,re,sr),
			do3D ? randomizeRadius(bound,re,sr) : 0
		);
	}
	
	private static double randomizeRadius(Bound radiusBound, RandomNumberGenerator re, ImageResolution sr ) {
		return radiusBound.resolve(sr, true).randOpen( re );
	}
}
