/* (C)2020 */
package org.anchoranalysis.io.manifest.sequencetype;

import static org.junit.Assert.*;

import org.junit.Test;

public class ChangeSequenceTypeTest {

    @Test
    public void testNextIndex() throws SequenceTypeException {

        ChangeSequenceType cst = new ChangeSequenceType();

        cst.update("0");
        cst.update("5");
        cst.update("9");
        cst.update("13");

        assertTrue(cst.nextIndex(5) == 9);
        assertTrue(cst.nextIndex(0) == 5);
        assertTrue(cst.nextIndex(13) == -1);
    }

    @Test
    public void testPreviousIndex() throws SequenceTypeException {

        ChangeSequenceType cst = new ChangeSequenceType();

        cst.update("0");
        cst.update("5");
        cst.update("9");
        cst.update("13");

        assertTrue(cst.previousIndex(5) == 0);
        assertTrue(cst.previousIndex(0) == -1);
        assertTrue(cst.previousIndex(13) == 9);
    }
}
