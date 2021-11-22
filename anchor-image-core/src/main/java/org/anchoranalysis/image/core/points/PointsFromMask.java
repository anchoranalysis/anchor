/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.core.points;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Extracts list of points from a {@link Mask}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointsFromMask {

    /**
     * All points that have <i>on</i> state in a {@link Mask} as a list with type {@link Point3i}.
     *
     * @param mask the mask.
     * @return a newly created list with the points.
     */
    public static List<Point3i> listFrom3i(Mask mask) {
        return PointsFromVoxels.listFrom3i(mask.binaryVoxels());
    }

    /**
     * All points that have <i>on</i> state in a {@link Mask} as a list with type {@link Point2i}.
     *
     * @param mask the mask.
     * @return a newly created list with the points.
     * @throws CreateException if any of the points in the mask are 3D i.e. have non-zero z-value.
     */
    public static List<Point2i> listFrom2i(Mask mask) throws CreateException {
        return PointsFromVoxels.listFrom2i(mask.binaryVoxels());
    }
}
