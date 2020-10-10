/*-
 * #%L
 * anchor-io-manifest
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

package org.anchoranalysis.io.manifest.sequencetype;

import static org.junit.Assert.*;

import org.junit.Test;

public class IncreasingIntegersTest {

    @Test
    public void testNextIndex() throws SequenceTypeException {

        IncreasingIntegers sequence = new IncreasingIntegers();

        sequence.update(0);
        sequence.update(5);
        sequence.update(9);
        sequence.update(13);

        IncompleteElementRange range = sequence.elementRange();
        assertTrue(range.nextIndex(5) == 9);
        assertTrue(range.nextIndex(0) == 5);
        assertTrue(range.nextIndex(13) == -1);
    }

    @Test
    public void testPreviousIndex() throws SequenceTypeException {

        IncreasingIntegers sequence = new IncreasingIntegers();

        sequence.update(0);
        sequence.update(5);
        sequence.update(9);
        sequence.update(13);

        IncompleteElementRange range = sequence.elementRange();
        assertTrue(range.previousIndex(5) == 0);
        assertTrue(range.previousIndex(0) == -1);
        assertTrue(range.previousIndex(13) == 9);
    }
}
