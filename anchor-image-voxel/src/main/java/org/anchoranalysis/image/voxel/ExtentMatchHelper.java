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
package org.anchoranalysis.image.voxel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Checks if the {@link Extent}s of two {@link BoundingBox}es are identical.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtentMatchHelper {

    /**
     * Checks that the {@link Extent}s of two bounding boxes are identical.
     *
     * <p>An exception is thrown if they are not identical, otherwise nothing occurs.
     *
     * @param box1 the first box.
     * @param box2 the second box.
     */
    public static void checkExtentMatch(BoundingBox box1, BoundingBox box2) {
        Extent extent1 = box1.extent();
        Extent extent2 = box2.extent();
        if (!extent1.equals(extent2)) {
            throw new IllegalArgumentException(
                    String.format(
                            "The extents of the two bounding-boxes are not identical: %s vs %s",
                            extent1, extent2));
        }
    }
}
