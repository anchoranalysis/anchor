/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.input.path.matcher;

import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.path.matcher.MatchGlob;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MatchGlob}.
 *
 * @author Owen Feehan
 */
class MatchGlobTest {

    private FilePathMatcherFixture fixture =
            new FilePathMatcherFixture(TestLoader.createFromMavenWorkingDirectory());

    @Test
    void testSingleWildcard() throws InputReadFailedException {
        doTest(2, 2, "*.txt");
    }

    @Test
    void testDoubleWildcard() throws InputReadFailedException {
        doTest(2, 6, "**.txt");
    }

    @Test
    void testFilesWithDotSingle() throws InputReadFailedException {
        doTest(3, 3, "*.*");
    }

    @Test
    void testFilesWithDotDouble() throws InputReadFailedException {
        doTest(3, 9, "**.*");
    }

    private void doTest(int expectedNumberNonRecursive, int expectedNumberRecursive, String glob)
            throws InputReadFailedException {
        MatchGlob matcher = new MatchGlob();
        matcher.setGlob(glob);
        fixture.doTest(expectedNumberNonRecursive, expectedNumberRecursive, matcher, "");
    }
}
