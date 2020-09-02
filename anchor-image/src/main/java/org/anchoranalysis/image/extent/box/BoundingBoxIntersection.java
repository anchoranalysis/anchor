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

package org.anchoranalysis.image.extent.box;

import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.Extent;

/**
 * Methods for checking intersection between a particular bounding-box and others
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public final class BoundingBoxIntersection {

    private final BoundingBox box;

    /** Does this bounding box intersect with another? */
    public boolean existsWith(BoundingBox other) {
        return with(other, false).isPresent();
    }

    /** Does this bounding box intersection with any of the others in the list? */
    public boolean existsWithAny(List<BoundingBox> others) {

        for (BoundingBox other : others) {

            if (existsWith(other)) {
                return true;
            }
        }

        return false;
    }

    public Optional<BoundingBox> with(BoundingBox other) {
        return with(other, true);
    }

    /** Find the intersection and clip to a a containing extent */
    public Optional<BoundingBox> withInside(BoundingBox other, Extent containingExtent) {
        return with(other).map(boundingBox -> boundingBox.clipTo(containingExtent));
    }

    /**
     * Determines if the bounding box intersects with another, and optionally creates the
     * bounding-box of intersection
     *
     * @param other the other bounding-box to check intersection with
     * @param createIntersectionBox iff TRUE the bounding-box of the intersection is returned,
     *     otherwise the existing (source) bounding-box is returned
     * @return a bounding-box if there is intersection (where box depends on {@link
     *     #createIntersection} or empty() if there is no intersection.
     */
    private Optional<BoundingBox> with(BoundingBox other, boolean createIntersectionBox) {

        CreateComparer createComparer = new CreateComparer(box, other);

        Optional<ExtentBoundsComparer> compareX = createComparer.createMin(ReadableTuple3i::x);
        Optional<ExtentBoundsComparer> compareY = createComparer.createMin(ReadableTuple3i::y);
        Optional<ExtentBoundsComparer> compareZ = createComparer.createMin(ReadableTuple3i::z);

        if (!compareX.isPresent() || !compareY.isPresent() || !compareZ.isPresent()) {
            return Optional.empty();
        }

        if (createIntersectionBox) {
            return Optional.of(createIntersection(compareX.get(), compareY.get(), compareZ.get()));
        } else {
            return Optional.of(box);
        }
    }

    private BoundingBox createIntersection(
            ExtentBoundsComparer compareX,
            ExtentBoundsComparer compareY,
            ExtentBoundsComparer compareZ) {
        return new BoundingBox(
                new Point3i(compareX.min(), compareY.min(), compareZ.min()),
                new Extent(compareX.extent(), compareY.extent(), compareZ.extent()));
    }

    /** Helps creates a comparer for each dimension of the box */
    private static class CreateComparer {

        private final ReadableTuple3i cornerMin1;
        private final ReadableTuple3i cornerMin2;

        private final ReadableTuple3i cornerMax1;
        private final ReadableTuple3i cornerMax2;

        public CreateComparer(BoundingBox box1, BoundingBox box2) {
            cornerMin1 = box1.cornerMin();
            cornerMin2 = box2.cornerMin();

            cornerMax1 = box1.calculateCornerMax();
            cornerMax2 = box2.calculateCornerMax();
        }

        public Optional<ExtentBoundsComparer> createMin(ToIntFunction<ReadableTuple3i> extract) {
            return ExtentBoundsComparer.createMin(
                    cornerMin1, cornerMin2, cornerMax1, cornerMax2, extract);
        }
    }
}
