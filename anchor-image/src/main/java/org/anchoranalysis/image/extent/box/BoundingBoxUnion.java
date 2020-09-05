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

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;

/**
 * Performs union of a bounding-box with other entities
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class BoundingBoxUnion {

    private final BoundingBox box;

    /**
     * Performs a union with another box (immutably)
     *
     * @param other the other bounding box
     * @return a new bounding-box that is union of both bounding boxes
     */
    public BoundingBox with(BoundingBox other) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMinOther = other.cornerMin();

        ReadableTuple3i cornerMax = box.calculateCornerMax();
        ReadableTuple3i cornerMaxOthr = other.calculateCornerMax();

        ExtentBoundsComparer meiX =
                ExtentBoundsComparer.createMax(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::x);
        ExtentBoundsComparer meiY =
                ExtentBoundsComparer.createMax(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::y);
        ExtentBoundsComparer meiZ =
                ExtentBoundsComparer.createMax(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOthr, ReadableTuple3i::z);

        return new BoundingBox(
                new Point3i(meiX.min(), meiY.min(), meiZ.min()),
                new Point3i(meiX.max(), meiY.max(), meiZ.max()));
    }
}