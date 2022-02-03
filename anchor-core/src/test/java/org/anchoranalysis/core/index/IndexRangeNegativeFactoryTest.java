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

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.range.IndexRangeNegative;
import org.anchoranalysis.core.index.range.IndexRangeNegativeFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IndexRangeNegativeFactory}.
 *
 * @author Owen Feehan
 */
class IndexRangeNegativeFactoryTest {

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
        assertThrows(OperationFailedException.class, () -> IndexRangeNegativeFactoryTest.parse(strToParse));
    }

    private static void test(String strToParse, int expectedStart, int expectedEnd)
            throws OperationFailedException {
        assertEquals(new IndexRangeNegative(expectedStart, expectedEnd), parse(strToParse));
    }

    private static IndexRangeNegative parse(String strToParse) throws OperationFailedException {
        return IndexRangeNegativeFactory.parse(strToParse);
    }
}
