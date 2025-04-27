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
