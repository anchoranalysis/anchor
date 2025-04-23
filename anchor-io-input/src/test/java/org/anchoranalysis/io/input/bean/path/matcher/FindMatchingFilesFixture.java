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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.io.input.path.matcher.DualDirectoryFixture;
import org.anchoranalysis.io.input.path.matcher.DualPathPredicates;
import org.anchoranalysis.io.input.path.matcher.FindFilesException;
import org.anchoranalysis.test.TestLoader;

/**
 * Executes a search on a {@link FindMatchingFiles} on two directories in the resources, and asserts
 * expectations.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class FindMatchingFilesFixture {

    /** How to access the two directories. */
    private final DualDirectoryFixture dualDirectory;

    /**
     * Create with a {@link TestLoader}.
     *
     * @param loader for determining the path to the two directories in the resources.
     */
    public FindMatchingFilesFixture(TestLoader loader) {
        this.dualDirectory = new DualDirectoryFixture(loader);
    }

    /**
     * Executes the finder both recursively and non-recursively, and checks the the number of files
     * is as expected.
     *
     * @param expectedNumberNonRecursive the number of files expected to match the {@code
     *     predicates}, when run non-recursively.
     * @param expectedNumberRecursive the number of files expected to match the {@code predicates},
     *     when run recursively.
     * @param nested if true, the nested subdirectory is used, otherwise the flat subdirectory.
     * @param predicates if true, a filter is applied to only detect files that end in ".txt". if
     *     false, no filter is applied.
     * @throws FindFilesException if thrown by {@link FindMatchingFiles#search(Path,
     *     DualPathPredicates, boolean, Optional)}.
     */
    public void assertNumberFoundFiles(
            int expectedNumberNonRecursive,
            int expectedNumberRecursive,
            boolean nested,
            DualPathPredicates predicates)
            throws FindFilesException {

        Path directory = dualDirectory.multiplex(nested);

        doTestSingle(expectedNumberNonRecursive, directory, predicates, false, "non-recursive");
        doTestSingle(expectedNumberRecursive, directory, predicates, true, "recursive");
    }

    /**
     * Executes the finder with a single configuration, and checks the the number of files is as
     * expected.
     */
    private void doTestSingle(
            int expectedNumberFound,
            Path directory,
            DualPathPredicates predicates,
            boolean recursive,
            String assertMessage)
            throws FindFilesException {

        List<File> files =
                FindMatchingFiles.search(directory, predicates, recursive, Optional.empty());

        // Check we found the expected number of files
        assertEquals(expectedNumberFound, files.size(), assertMessage);
    }
}
