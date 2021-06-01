package org.anchoranalysis.core.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IndexRange}.
 *
 * @author Owen Feehan
 */
class IndexRangeTest {

    private static final List<String> ELEMENTS = Arrays.asList("a", "b", "c", "d", "e");

    /**
     * Two positive indexes, correctly ordered.
     *
     * @throws OperationFailedException
     */
    @Test
    void test_BothPositive_CorrectOrder() throws OperationFailedException {
        testExpectSuccess(Arrays.asList("b", "c", "d"), 1, 3);
    }

    /**
     * Two positive indexes, incorrectly ordered.
     *
     * @throws OperationFailedException
     */
    @Test
    void test_BothPositive_IncorrectOrder() throws OperationFailedException {
        testExpectException(3, 1);
    }

    /**
     * Two negative indexes, correctly ordered.
     *
     * @throws OperationFailedException
     */
    @Test
    void test_BothNegative_CorrectOrder() throws OperationFailedException {
        testExpectSuccess(Arrays.asList("c", "d", "e"), -3, -1);
    }

    /**
     * Two negative indexes, incorrectly ordered.
     *
     * @throws OperationFailedException
     */
    @Test
    void test_BothNegative_IncorrectOrder() throws OperationFailedException {
        testExpectException(-1, -3);
    }

    /**
     * Tests with one index being larger than the size of the list.
     *
     * @throws OperationFailedException
     */
    @Test
    void test_IndexTooLarge() throws OperationFailedException {
        testExpectException(1, 9);
    }

    /**
     * Tests with one index being larger (negatively) than the size of the list.
     *
     * @throws OperationFailedException
     */
    @Test
    void test_IndexTooSmall() throws OperationFailedException {
        testExpectException(-11, -2);
    }

    private static void testExpectSuccess(List<String> expected, int startIndex, int endIndex)
            throws OperationFailedException {
        assertEquals(expected, performOperation(startIndex, endIndex));
    }

    private static void testExpectException(int startIndex, int endIndex) {
        assertThrows(OperationFailedException.class, () -> performOperation(startIndex, endIndex));
    }

    private static List<String> performOperation(int startIndex, int endIndex)
            throws OperationFailedException {
        IndexRange range = new IndexRange(startIndex, endIndex);
        return range.extract(ELEMENTS);
    }
}
