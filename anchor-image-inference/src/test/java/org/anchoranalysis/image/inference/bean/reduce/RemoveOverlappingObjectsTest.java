/*-
 * #%L
 * anchor-plugin-image
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
package org.anchoranalysis.image.inference.bean.reduce;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.core.time.ExecutionTimeRecorderIgnore;
import org.anchoranalysis.image.inference.segment.LabelledWithConfidence;
import org.anchoranalysis.image.inference.segment.ReductionOutcome;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;
import org.anchoranalysis.spatial.box.Extent;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link RemoveOverlappingObjects}.
 *
 * @author Owen Feehan
 */
class RemoveOverlappingObjectsTest {

    private static final Extent EXTENT = new Extent(100, 100, 100);

    @Test
    void testWithoutInvert() {
        doTest(false);
    }

    @Test
    void testWithInvert() {
        doTest(true);
    }

    private void doTest(boolean invert) {
        RemoveOverlappingObjects remove = new RemoveOverlappingObjects();
        ReductionOutcome<LabelledWithConfidence<ObjectMask>> outcome =
                remove.reduce(allObjects(invert), EXTENT, ExecutionTimeRecorderIgnore.instance());
        assertEquals(3, outcome.sizeAfter());
    }

    /**
     * All the objects that are reduced during the test.
     *
     * @param invert if true, the object's <i>on</i> pixels are 0. if false, they are 255.
     * @return a list of 4 objects to be used in the test.
     */
    private static List<LabelledWithConfidence<ObjectMask>> allObjects(boolean invert) {
        LabelledWithConfidence<ObjectMask> object1 = object(10, 10, 0.9, invert);
        LabelledWithConfidence<ObjectMask> object2 = object(11, 10, 0.8, invert);
        LabelledWithConfidence<ObjectMask> object3 = object(15, 10, 0.75, invert);
        LabelledWithConfidence<ObjectMask> object4 = object(40, 10, 0.7, invert);

        return Arrays.asList(object1, object2, object3, object4);
    }

    /**
     * Creates an {@link ObjectMask} in three-dimensions, corresponding to a box.
     *
     * @param coordinate the minimum point in the object-mask in all dimensions.
     * @param extent the size of the box that forms the object-mask in all dimensions.
     * @param confidence the confidence to associate with the object-mask
     * @param invert if true, the object's <i>on</i> pixels are 0. if false, they are 255.
     * @return a newly created {@link ObjectMask} with associated confidence.
     */
    private static LabelledWithConfidence<ObjectMask> object(
            int coordinate, int extent, double confidence, boolean invert) {
        ObjectMask object = new ObjectMask(BoundingBoxFactory.uniform3D(coordinate, extent));
        if (invert) {
            object = object.invert();
        } else {
            object.assignOn().toAll();
        }
        return new LabelledWithConfidence<>(object, confidence, "arbitraryLabel");
    }
}
