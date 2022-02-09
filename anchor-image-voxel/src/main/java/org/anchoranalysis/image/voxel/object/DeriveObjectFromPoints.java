/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.object;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.spatial.box.PointRange;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Derive an {@link ObjectMask} by incrementally adding points.
 *
 * @author Owen Feehan
 */
public class DeriveObjectFromPoints {

    /** Tracks the minimum and maximum along each dimension of the added points. */
    private PointRange range = new PointRange();

    /** Remembers each point that has been added. */
    private List<Point3i> points = new LinkedList<>();

    /**
     * Adds a point to the object.
     *
     * @param point the point to add.
     */
    public void add(ReadableTuple3i point) {
        range.add(point);
        points.add(new Point3i(point));
    }

    /**
     * Derives an {@link ObjectMask} that includes all points that were previously added.
     *
     * <p>The bounding-box of the created {@link ObjectMask} will fit the points as tightly as
     * possible.
     *
     * @return a newly created {@link ObjectMask} if at least one point was added. Otherwise {@link
     *     Optional#empty()}.
     */
    public Optional<ObjectMask> deriveObject() {
        if (!range.isEmpty()) {
            // Assign all the ON voxels to the object
            ObjectMask object = new ObjectMask(range.toBoundingBoxNoCheck());
            for (Point3i point : points) {
                object.assignOn().toVoxel(point);
            }
            return Optional.of(object);
        } else {
            return Optional.empty();
        }
    }
}
