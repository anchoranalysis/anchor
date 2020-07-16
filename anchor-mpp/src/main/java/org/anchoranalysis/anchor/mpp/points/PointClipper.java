/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
/* (C)2020 */
package org.anchoranalysis.anchor.mpp.points;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.ImageDimensions;

/** Ensures a point has values contained inside image-dimensions */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointClipper {

    public static Point3i clip(Point3i point, ImageDimensions dimensions) {
        point = clipLow(point);
        point = clipHigh(point, dimensions);
        return point;
    }

    public static Point3d clip(Point3d point, ImageDimensions dimensions) {
        point = clipLow(point);
        point = clipHigh(point, dimensions);
        return point;
    }

    private static Point3i clipLow(Point3i point) {
        return point.max(0);
    }

    private static Point3d clipLow(Point3d point) {
        return point.max(0);
    }

    private static Point3i clipHigh(Point3i point, ImageDimensions dimensions) {
        return point.min(dimensions.getExtent().createMinusOne());
    }

    private static Point3d clipHigh(Point3d point, ImageDimensions dimensions) {
        return point.min(dimensions.getExtent().createMinusOne());
    }
}
