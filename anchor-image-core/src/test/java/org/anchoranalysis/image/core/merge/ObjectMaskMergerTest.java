/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.merge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ObjectMaskMerger}.
 *
 * @author Owen Feehan
 */
class ObjectMaskMergerTest {

    private static final ObjectMask OBJECT1 = object(10, 20);
    private static final ObjectMask OBJECT2 = object(15, 25);

    @Test
    void testMerge() {
        ObjectMask merged = ObjectMaskMerger.merge(OBJECT1, OBJECT2);
        assertEquals(20250, merged.numberVoxelsOn());
    }

    private static ObjectMask object(int coordinate, int extent) {
        ObjectMask object = new ObjectMask(BoundingBoxFactory.uniform3D(coordinate, extent));
        object = object.invert();
        return object;
    }
}
