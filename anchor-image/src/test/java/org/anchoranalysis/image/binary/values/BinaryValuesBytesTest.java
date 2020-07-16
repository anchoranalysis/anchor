/* (C)2020 */
package org.anchoranalysis.image.binary.values;

import static org.junit.Assert.*;

import org.junit.Test;

public class BinaryValuesBytesTest {

    @Test
    public void testEquals() {
        BinaryValuesByte bv1 = new BinaryValuesByte(0, 255);
        BinaryValuesByte bv2 = new BinaryValuesByte(0, 255);
        assertTrue(bv1.equals(bv2));
    }
}
