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

import java.util.function.ToIntFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Performs union of a bounding-box with other entities
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class BoundingBoxUnion {

    private final BoundingBox box;

    /**
     * Performs a union with another box (immutably).
     *
     * @param other the other bounding box.
     * @return a new bounding-box that is union of both bounding boxes.
     */
    public BoundingBox with(BoundingBox other) {

        ReadableTuple3i cornerMin = box.cornerMin();
        ReadableTuple3i cornerMinOther = other.cornerMin();

        ReadableTuple3i cornerMax = box.calculateCornerMaxInclusive();
        ReadableTuple3i cornerMaxOther = other.calculateCornerMaxInclusive();

        ExtentBoundsComparer comparerX =
                ExtentBoundsComparer.createMax(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOther, ReadableTuple3i::x);
        ExtentBoundsComparer comparerY =
                ExtentBoundsComparer.createMax(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOther, ReadableTuple3i::y);
        ExtentBoundsComparer comparerZ =
                ExtentBoundsComparer.createMax(
                        cornerMin, cornerMinOther, cornerMax, cornerMaxOther, ReadableTuple3i::z);

        return BoundingBox.createReuse(
                extractPoint(comparerX, comparerY, comparerZ, ExtentBoundsComparer::min),
                extractPoint(comparerX, comparerY, comparerZ, ExtentBoundsComparer::max));
    }

    private static Point3i extractPoint(
            ExtentBoundsComparer comparerX,
            ExtentBoundsComparer comparerY,
            ExtentBoundsComparer comparerZ,
            ToIntFunction<ExtentBoundsComparer> extract) {
        return new Point3i(
                extract.applyAsInt(comparerX),
                extract.applyAsInt(comparerY),
                extract.applyAsInt(comparerZ));
    }
}
