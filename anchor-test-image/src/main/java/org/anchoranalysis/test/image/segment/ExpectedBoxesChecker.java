/*-
 * #%L
 * anchor-plugin-opencv
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
package org.anchoranalysis.test.image.segment;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.IntersectionOverUnion;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExpectedBoxesChecker {

    /**
     * Minimum intersection-over-union score required to be considered a successful match to the
     * target box.
     */
    private static final double THRESHOLD_SCORE = 0.40;

    public static void assertExpectedBoxes(ObjectCollection objects, BoundingBox targetBox) {
        assertTrue(
                atLeastOneObjectOverlaps(objects, targetBox),
                "at least one object has box: " + targetBox.toString());
    }

    private static boolean atLeastOneObjectOverlaps(ObjectCollection objects, BoundingBox box) {
        return objects.stream()
                .anyMatch(
                        object ->
                                IntersectionOverUnion.forBoxes(object.boundingBox(), box)
                                        > THRESHOLD_SCORE);
    }
}
