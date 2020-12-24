/*-
 * #%L
 * anchor-bean
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

package org.anchoranalysis.bean.permute.property;

import static org.junit.Assert.*;

import org.junit.Test;

public class IntegerRangeTest {

    @Test
    public void test1() {
        SequenceIntegerIterator range = new SequenceIntegerIterator(2, 8, 3);
        assertHasNext(range);
        assertNextValue(2, range);
        assertHasNext(range);
        assertNextValue(5, range);
        assertHasNext(range);
        assertNextValue(8, range);
        assertDoesNotHaveNext(range);
    }

    @Test
    public void test2() {
        SequenceIntegerIterator range = new SequenceIntegerIterator(-12, -4, 2);
        assertHasNext(range);
        assertNextValue(-12, range);
        range.next();
        range.next();
        range.next();
        assertNextValue(-4, range);
        assertDoesNotHaveNext(range);
    }
    
    private static void assertNextValue(int expectedValue, SequenceIntegerIterator range) {
        assertEquals(expectedValue, range.next().intValue());
    }
    
    private static void assertHasNext(SequenceIntegerIterator range) {
        assertTrue(range.hasNext());
    }
    
    private static void assertDoesNotHaveNext(SequenceIntegerIterator range) {
        assertFalse(range.hasNext());
    }
}
