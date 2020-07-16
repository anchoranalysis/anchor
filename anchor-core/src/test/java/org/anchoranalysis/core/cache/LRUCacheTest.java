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
