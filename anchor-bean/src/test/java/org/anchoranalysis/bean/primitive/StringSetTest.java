/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2025 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.bean.primitive;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import org.junit.jupiter.api.Test;

class StringSetTest {

    @Test
    void testVarargsConstructor() {
        StringSet set = new StringSet("apple", "banana", "cherry");

        assertEquals(3, set.set().size());
        assertTrue(set.contains("apple"));
        assertTrue(set.contains("banana"));
        assertTrue(set.contains("cherry"));
        assertFalse(set.contains("date"));

        // Verify the natural ordering of elements
        Iterator<String> iterator = set.iterator();
        assertEquals("apple", iterator.next());
        assertEquals("banana", iterator.next());
        assertEquals("cherry", iterator.next());
    }

    @Test
    void testDuplicateBean() {
        StringSet original = new StringSet("apple", "banana", "cherry");
        StringSet duplicate = original.duplicateBean();

        assertNotSame(original, duplicate);
        assertEquals(original, duplicate);

        // Modify the duplicate to ensure it's a deep copy
        duplicate.add("date");
        assertNotEquals(original, duplicate);
        assertFalse(original.contains("date"));
        assertTrue(duplicate.contains("date"));
    }

    @Test
    void testNoDuplicateValues() {
        StringSet set = new StringSet("apple", "banana", "cherry");

        // Try to add duplicate values
        set.add("apple");
        set.add("banana");
        set.add("cherry");

        // Check that the size remains 3 and all original elements are present
        assertEquals(3, set.set().size());
        assertTrue(set.contains("apple"));
        assertTrue(set.contains("banana"));
        assertTrue(set.contains("cherry"));

        // Verify the natural ordering of elements
        Iterator<String> iterator = set.iterator();
        assertEquals("apple", iterator.next());
        assertEquals("banana", iterator.next());
        assertEquals("cherry", iterator.next());
    }

    @Test
    void testIterator() {
        StringSet set = new StringSet("apple", "banana", "cherry");

        Iterator<String> iterator = set.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("apple", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("banana", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("cherry", iterator.next());

        assertFalse(iterator.hasNext());
    }
}
