/*-
 * #%L
 * anchor-spatial
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.spatial.rtree;

import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;

/**
 * Paritcular instances of {@link BoundingBox} that can be used for testing.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class SpecificBoxFixture {

    /** First bounding box that doesn't intersect with any other. */
    public static final BoundingBox BOX1 = BoundingBoxFactory.at(3, 4, 5, 6, 7, 8);

    /** Second bounding box that intersects with {@code BOX3}. */
    public static final BoundingBox BOX2 = BoundingBoxFactory.at(13, 14, 15, 6, 7, 8);

    /** Third bounding box that intersects with {@code BOX2} */
    public static final BoundingBox BOX3 = BoundingBoxFactory.at(15, 16, 17, 6, 7, 8);

    /**
     * An additional box that may be queried or added, and which intersects with {@code BOX2} and
     * {@code BOX3}.
     */
    public static final BoundingBox BOX_ADDITIONAL = BoundingBoxFactory.at(14, 15, 17, 6, 7, 8);

    /** A bounding box that intersects with no other. */
    public static final BoundingBox BOX_WITHOUT_INTERSECTION =
            BoundingBoxFactory.at(1, 1, 1, 1, 1, 1);

    /**
     * All the instances of {@link BoundingBox} specified in this class.
     *
     * @return a newly-created list of all the instances.
     */
    public static List<BoundingBox> allBoxes() {
        return Arrays.asList(BOX1, BOX2, BOX3, BOX_ADDITIONAL, BOX_WITHOUT_INTERSECTION);
    }
}
