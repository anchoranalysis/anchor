/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.core.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IndexRangeNegative}.
 *
 * @author Owen Feehan
 */
class IndexRangeNegativeTest {

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
        IndexRangeNegative range = new IndexRangeNegative(startIndex, endIndex);
        return range.extract(ELEMENTS);
    }
}
