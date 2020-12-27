/*-
 * #%L
 * anchor-core
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

package org.anchoranalysis.spatial.point;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests different types of {@code Point} classes, and particularly their interaction.
 * 
 * @author Owen Feehan
 *
 */
class PointTest {

    /** Is a point equal to another object of <b><i>same type</i> with same values</b>? */
    @Test
    void testEqualsSameType() {
        assertEquals(create2d(), create2d());
        assertEquals(create2f(), create2f());
        assertEquals(create3d(), create3d());
        assertEquals(create3f(), create3f());
        assertEquals(create3i(), create3i());
        assertEquals(create2i(), create2i());
    }

    /** Is a point equal to another object of <b><i>different</i> type with same values</b>? */
    @Test
    void testEqualsDifferentType() {
        // At present, different types are not allowed be equal
        assertNotEquals(create2d(), create2f());
        assertNotEquals(create3d(), create3f());
        
        assertEquals(create2d().toFloat(), create2f());
        assertEquals(PointConverter.floatFromDouble(create3d()), create3f());        
    }

    private static Point2f create2f() {
        return new Point2f(-3.1f, 4.2f);
    }
    
    private static Point2d create2d() {
        return new Point2d(-3.1, 4.2);
    }
    
    private static Point2i create2i() {
        return new Point2i(-3, 4);
    }
    
    private static Point3d create3d() {
        return new Point3d(-3.1, 4.2, 6.3);
    }
    
    private static Point3i create3i() {
        return new Point3i(-3, 4, 6);
    }
    
    private static Point3f create3f() {
        return new Point3f(-3.1f, 4.2f, 6.3f);
    }
}
