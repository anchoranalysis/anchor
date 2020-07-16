/* (C)2020 */
package org.anchoranalysis.image.binary.values;

import static org.junit.Assert.*;

import org.junit.Test;

public class BinaryValuesTest {

    @Test
    public void testEquals() {
        BinaryValues bv1 = new BinaryValues(0, 255);
        BinaryValues bv2 = new BinaryValues(0, 255);
        assertTrue(bv1.equals(bv2));
    }

    @Test
    public void testInvert() {
        BinaryValues bv1 = new BinaryValues(0, 255);
        BinaryValues bv2 = new BinaryValues(255, 0);
        assertTrue(bv1.createInverted().equals(bv2));
    }
}
