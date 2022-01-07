package org.anchoranalysis.image.bean.spatial.arrange.align;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.point.Point3i;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Center}.
 *
 * @author Owen Feehan
 */
class TestCenter {

    @Test
    void testAlign() throws OperationFailedException {
        BoxAlignerTester.doTest(
                new Center(), new Point3i(60, 115, 15), BoxAlignerTester.SMALLER.extent());
    }

    /** Test incompatible z-dimension values. */
    @Test
    void testInvalidZ() {
        assertThrows(
                OperationFailedException.class,
                () ->
                        new Center()
                                .align(
                                        BoxAlignerTester.SMALLER,
                                        BoxAlignerTester.SMALLER.changeExtentZ(5)));
    }
}
