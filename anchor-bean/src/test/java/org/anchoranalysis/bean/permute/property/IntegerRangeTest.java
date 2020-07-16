/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import static org.junit.Assert.*;

import org.junit.Test;

public class IntegerRangeTest {

    @Test
    public void test1() {
        SequenceIntegerIterator range = new SequenceIntegerIterator(2, 8, 3);
        assertTrue(range.hasNext() == true);
        assertTrue(range.next() == 2);
        assertTrue(range.hasNext() == true);
        assertTrue(range.next() == 5);
        assertTrue(range.hasNext() == true);
        assertTrue(range.next() == 8);
        assertTrue(range.hasNext() == false);
    }

    @Test
    public void test2() {
        SequenceIntegerIterator range = new SequenceIntegerIterator(-12, -4, 2);
        assertTrue(range.hasNext() == true);
        assertTrue(range.next() == -12);
        range.next();
        range.next();
        range.next();
        assertTrue(range.next() == -4);
        assertTrue(range.hasNext() == false);
    }
}
