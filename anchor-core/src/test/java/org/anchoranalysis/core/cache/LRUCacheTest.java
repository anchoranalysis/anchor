/* (C)2020 */
package org.anchoranalysis.core.cache;

import static org.junit.Assert.*;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.junit.Test;

public class LRUCacheTest {

    private static final String KEY1 = "apple";
    private static final String KEY2 = "orange";
    private static final String KEY3 = "pear";
    private static final String KEY4 = "grapes";

    /** Adds some keys and sees if what is evicted is sensible */
    @Test
    public void testSimpleEviction() throws GetOperationFailedException {

        LRUCache<String, String> cache =
                new LRUCache<>(
                        2, // Cache size
                        a -> a.toUpperCase());

        cache.get(KEY1);
        cache.get(KEY2);
        assertTrue("KEY1 is still there when there's two items", cache.has(KEY1));
        cache.get(KEY3);
        assertTrue("KEY3 is there as the most recently added.", cache.has(KEY3));
        cache.get(KEY2);
        cache.get(KEY4);
        assertTrue(
                "KEY2 remains after fourth added after being most recently used", cache.has(KEY2));
        assertFalse("KEY1 is removed as being least recently used", cache.has(KEY1));
    }
}
