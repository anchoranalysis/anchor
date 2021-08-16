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

package org.anchoranalysis.mpp.mark;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;

/** Ensures a point has values contained inside image-dimensions */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointClamper {

    public static Point3i clamp(Point3i point, Dimensions dimensions) {
        point = clampLow(point);
        point = clampHigh(point, dimensions);
        return point;
    }

    public static Point3d clamp(Point3d point, Dimensions dimensions) {
        point = clampLow(point);
        point = clampHigh(point, dimensions);
        return point;
    }

    private static Point3i clampLow(Point3i point) {
        return point.max(0);
    }

    private static Point3d clampLow(Point3d point) {
        return point.max(0);
    }

    private static Point3i clampHigh(Point3i point, Dimensions dimensions) {
        return point.min(dimensions.extent().createMinusOne());
    }

    private static Point3d clampHigh(Point3d point, Dimensions dimensions) {
        return point.min(dimensions.extent().createMinusOne());
    }
}
