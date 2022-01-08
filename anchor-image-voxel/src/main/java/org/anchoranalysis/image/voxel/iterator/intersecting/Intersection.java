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

package org.anchoranalysis.image.voxel.iterator.intersecting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * A bounding box where intersection occurs of two boxes, together with relative position of the
 * second box to the first.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Value
class Intersection {

    /** Bounding-box of the intersection relative to box1 */
    private BoundingBox intersectingBox;

    /** Relative position of {@code box2} to the {@code box1}. */
    private Point3i relative;

    public static Intersection create(
            BoundingBox box1, BoundingBox box2, BoundingBox boxIntersect) {

        Point3i relativeIntersectionToBox1 = boxIntersect.relativePositionTo(box1);

        Point3i relativeBox2ToBox1 = Point3i.immutableSubtract(box1.cornerMin(), box2.cornerMin());

        return new Intersection(
                BoundingBox.createReuse(relativeIntersectionToBox1, boxIntersect.extent()),
                relativeBox2ToBox1);
    }
}
