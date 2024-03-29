/*-
 * #%L
 * anchor-plugin-opencv
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
package org.anchoranalysis.spatial.box;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Utility methods to create a {@link BoundingBox}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoundingBoxFactory {

    /**
     * Short-hand factory method for creating a 2D bounding-box.
     *
     * @param x left-corner in x-dimension
     * @param y left-corner in y-dimension
     * @param width bounding-box width (extent in x-dimension)
     * @param height bounding-box width (extent in y-dimension)
     * @return the newly created bounding-box
     */
    public static BoundingBox at(int x, int y, int width, int height) {
        return BoundingBox.createReuse(new Point3i(x, y, 0), new Extent(width, height));
    }

    /**
     * Short-hand factory method for creating a 3D bounding-box.
     *
     * @param x left-corner in x-dimension
     * @param y left-corner in y-dimension
     * @param z left-corner in z-dimension
     * @param width bounding-box width (extent in x-dimension)
     * @param height bounding-box width (extent in y-dimension)
     * @param depth bounding-box width (extent in z-dimension)
     * @return the newly created bounding-box
     */
    public static BoundingBox at(int x, int y, int z, int width, int height, int depth) {
        return BoundingBox.createReuse(new Point3i(x, y, z), new Extent(width, height, depth));
    }

    /**
     * Short-hand factory method for creating a 3D bounding-box, where a coordinate is identical in
     * each dimension.
     *
     * @param corner left-corner in every dimension
     * @param extent extent in every dimension
     * @return the newly created bounding-box
     */
    public static BoundingBox uniform3D(int corner, int extent) {
        return BoundingBoxFactory.at(corner, corner, corner, extent, extent, extent);
    }
}
