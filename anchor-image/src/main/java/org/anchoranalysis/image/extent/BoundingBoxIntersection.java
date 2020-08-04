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

package org.anchoranalysis.image.extent;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;

/**
 * Methods for checking intersection between a particular bounding-box and others
 *
 * @author Owen Feehan
 */
public final class BoundingBoxIntersection {

    private final BoundingBox box;

    public BoundingBoxIntersection(BoundingBox box) {
        super();
        this.box = box;
    }

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
     * @return a bounding-box if there is intersection (which box depends on {@link
     *     createIntersectionBox} or empty() if there is no intersection.
     */
    private Optional<BoundingBox> with(BoundingBox other, boolean createIntersectionBox) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMinOther = other.cornerMin();

        ReadableTuple3i cornerMax = box.calcCornerMax();
        ReadableTuple3i cornerMaxOthr = other.calcCornerMax();

        Optional<ExtentBoundsComparer> meiX =
                ExtentBoundsComparer.createMin(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::x);
        Optional<ExtentBoundsComparer> meiY =
                ExtentBoundsComparer.createMin(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::y);
        Optional<ExtentBoundsComparer> meiZ =
                ExtentBoundsComparer.createMin(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::z);

        if (!meiX.isPresent() || !meiY.isPresent() || !meiZ.isPresent()) {
            return Optional.empty();
        }

        if (createIntersectionBox) {
            return Optional.of(
                    new BoundingBox(
                            new Point3i(
                                    meiX.get().min(), meiY.get().min(), meiZ.get().min()),
                            new Extent(
                                    meiX.get().extent(),
                                    meiY.get().extent(),
                                    meiZ.get().extent())));
        } else {
            return Optional.of(box);
        }
    }
}
