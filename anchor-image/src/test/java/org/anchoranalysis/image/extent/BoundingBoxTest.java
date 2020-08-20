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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BoundingBoxTest {

    @Test
    public void testIntersect1() {

        BoundingBox obj1 = BoundingBoxFixture.of(154, 58, 3, 12, 30, 16);
        BoundingBox obj2 = BoundingBoxFixture.of(46, 62, 5, 26, 24, 17);

        assertTrue(!obj1.intersection().existsWith(obj2));
    }

    @Test
    public void testIntersect2() {

        BoundingBox obj1 = BoundingBoxFixture.of(0, 0, 0, 1024, 1024, 1);
        BoundingBox obj2 = BoundingBoxFixture.of(433, 95, 1, 1, 1, 1);

        assertTrue(!obj1.intersection().existsWith(obj2));
    }

    @Test
    public void testUnion() {

        BoundingBox box1 = BoundingBoxFixture.of(156, 56, 0, 139, 139, 1);
        BoundingBox box2 = BoundingBoxFixture.of(94, 94, 0, 117, 117, 1);

        BoundingBox boxUnion = box1.union().with(box2);

        assertEquals(94, boxUnion.cornerMin().x());
        assertEquals(56, boxUnion.cornerMin().y());
        assertEquals(201, boxUnion.extent().x());
        assertEquals(155, boxUnion.extent().y());
    }
}
