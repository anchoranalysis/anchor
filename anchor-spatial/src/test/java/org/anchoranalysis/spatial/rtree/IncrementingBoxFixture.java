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

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;

/**
 * Creates lists of bounding-boxes, constructed from identical bounding-boxes that incrementally
 * shift their position.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class IncrementingBoxFixture {

    /** The width, height, and depth of each box that is created. */
    private static final int BOX_SIZE = 20;

    /**
     * This is added to the minimum corner of the previous box, to give the next box in a sequence.
     */
    private static final int POSITION_SHIFT = 3;

    /**
     * Creates a list of boxes, partitioned into two disjoint sets.
     *
     * @param numberFirst the number of boxes in the <i>first</i> set.
     * @param numberSecond the number of boxes in the <i>second</i> set.
     * @return a newly created {@link List} containing {@code numberBoxes}, incrementally shifted,
     *     and in two disjoint sets.
     */
    public static List<BoundingBox> createDisjointIncrementing(int numberFirst, int numberSecond) {
        List<BoundingBox> boxes = createIncrementingBoxes(numberFirst, 0);

        // Make sure the second set starts after sufficient space from the last set
        int startSecond = ((numberFirst + 2) * BOX_SIZE);
        boxes.addAll(createIncrementingBoxes(numberSecond, startSecond));
        return boxes;
    }

    /**
     * Creates a list of identically-sized boxes, incrementing shifting their position.
     *
     * @param numberBoxes the number of boxes to create.
     * @param initialPosition the corner position for the first box.
     * @return a newly created {@link List} containing {@code numberBoxes}, incrementally shifted.
     */
    public static List<BoundingBox> createIncrementingBoxes(int numberBoxes, int initialPosition) {
        List<BoundingBox> out = new ArrayList<BoundingBox>();
        int position = initialPosition;
        for (int i = 0; i < numberBoxes; i++) {
            out.add(BoundingBoxFactory.uniform3D(position, BOX_SIZE));
            position += POSITION_SHIFT;
        }
        return out;
    }
}
