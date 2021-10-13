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

package org.anchoranalysis.spatial.box;

import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Methods for checking intersection between a particular bounding-box and others.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public final class BoundingBoxIntersection {

    private final BoundingBox box;

    /** 
     * Does intersection exist with another bounding-box?
     * 
     * @param other the other bounding-box to test intersection with.
     * @return true iff intersection exists.
     */
    public boolean existsWith(BoundingBox other) {
        return with(other, false).isPresent();
    }

    /** 
     * Does intersection exist with with any of the others in the list?
     *
     * @param others the other bounding-boxes to test intersection with.
     * @return true iff intersection exists with at least one box in {@code others}.
     */
    public boolean existsWithAny(List<BoundingBox> others) {

        for (BoundingBox other : others) {

            if (existsWith(other)) {
                return true;
            }
        }

        return false;
    }

    /** 
     * Finds the intersection with another bounding-box, if it exists.
     *
     * @param other the bounding-box to find intersection with.
     * @return a bounding-box describing the in the intersection, or {@link Optional#empty()} if no intersection exists.
     */
    public Optional<BoundingBox> with(BoundingBox other) {
        return with(other, true);
    }

    /** 
     * Finds the intersection and clamp to a a containing extent.
     *
     * @param other the bounding-box to find intersection with.
     * @param containingExtent the extent the intersection is clamped to.
     * @return a bounding-box describing the in the intersection, or {@link Optional#empty()} if no intersection exists.
     */
    public Optional<BoundingBox> withInside(BoundingBox other, Extent containingExtent) {
        // TODO what happens when the containing-extent does not contain the the intersection?
        return with(other).map(boundingBox -> boundingBox.clampTo(containingExtent));
    }

    /**
     * Determines if the bounding box intersects with another, and optionally creates the
     * bounding-box of intersection
     *
     * @param other the other bounding-box to check intersection with
     * @param createIntersectionBox iff true the bounding-box of the intersection is returned,
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
