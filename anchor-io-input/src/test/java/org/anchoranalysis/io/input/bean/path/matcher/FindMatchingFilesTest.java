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
package org.anchoranalysis.io.input.bean.path.matcher;

import java.nio.file.Path;
import org.anchoranalysis.io.input.path.matcher.DualPathPredicates;
import org.anchoranalysis.io.input.path.matcher.FindFilesException;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link FindMatchingFiles}.
 *
 * @author Owen Feehan
 */
class FindMatchingFilesTest {

    private FindMatchingFilesFixture fixture =
            new FindMatchingFilesFixture(TestLoader.createFromMavenWorkingDirectory());

    @Test
    void testFlatUnfiltered() throws FindFilesException {
        doTestBoth(4, 4, false, false);
    }

    @Test
    void testNestedUnfiltered() throws FindFilesException {
        doTestBoth(4, 12, true, false);
    }

    @Test
    void testFlatFiltered() throws FindFilesException {
        doTestBoth(2, 2, false, true);
    }

    @Test
    void testNestedFiltered() throws FindFilesException {
        doTestBoth(2, 6, true, true);
    }

    /**
     * Executes the finder both recursively and non-recursively, and checks the the number of files
     * is as expected.
     *
     * @param expectedNumberNonRecursive the number of files expected, when run recursively.
     * @param expectedNumberNonRecursive the number of files expected, when run non-recursively.
     * @param nested if true, the nested subdirectory is used, otherwise the flat subdirectory.
     * @param filtered if true, a filter is applied to only detect files that end in ".txt". if
     *     false, no filter is applied.
     * @throws FindFilesException
     */
    private void doTestBoth(
            int expectedNumberNonRecursive,
            int expectedNumberRecursive,
            boolean nested,
            boolean filtered)
            throws FindFilesException {

        DualPathPredicates predicates =
                new DualPathPredicates(
                        filtered ? FindMatchingFilesTest::endsWithTxt : path -> true, path -> true);

        fixture.assertNumberFoundFiles(
                expectedNumberNonRecursive, expectedNumberRecursive, nested, predicates);
    }

    /** Whether the path ends with a text extension, including leading period. */
    private static boolean endsWithTxt(Path path) {
        return path.toString().toLowerCase().endsWith(".txt");
    }
}
