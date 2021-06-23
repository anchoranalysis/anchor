package org.anchoranalysis.core.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IndexRange}.
 *
 * @author Owen Feehan
 */
class IndexRangeFactoryTest {

    /** Tests without a colon. */
    @Test
    void testWithoutColon() throws OperationFailedException {
        test("-11", -11, -11);
    }

    /** Tests a colon sandwiched between two components. */
    @Test
    void testColonSandwiched() throws OperationFailedException {
        test("-7:11", -7, 11);
    }

    /** Tests a colon on the left hand side. */
    @Test
    void testColonLeft() throws OperationFailedException {
        test(":-6", 0, -6);
    }

    /** Tests a colon on the right hand side. */
    @Test
    void testColonRight() throws OperationFailedException {
        test("8:", 8, 8);
    }

    @Test
    void testTwoColons() throws OperationFailedException {
        testExpectException("8:11:19");
    }

    @Test
    void testNonNumericCharacter() throws OperationFailedException {
        testExpectException("8:a11");
    }

    @Test
    void testFloatingPoint() throws OperationFailedException {
        testExpectException("2.1:-3");
    }

    private static void testExpectException(String strToParse) {
        assertThrows(OperationFailedException.class, () -> IndexRangeFactoryTest.parse(strToParse));
    }

    private static void test(String strToParse, int expectedStart, int expectedEnd)
            throws OperationFailedException {
        assertEquals(new IndexRange(expectedStart, expectedEnd), parse(strToParse));
    }

    private static IndexRange parse(String strToParse) throws OperationFailedException {
        return IndexRangeFactory.parse(strToParse);
    }
}
