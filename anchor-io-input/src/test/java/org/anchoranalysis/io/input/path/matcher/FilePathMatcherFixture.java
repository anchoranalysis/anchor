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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.path.matcher.FilePathMatcher;
import org.anchoranalysis.test.TestLoader;

/**
 * Looks for matches with a {@link FilePathMatcher} on two directories in the resources, and asserts
 * expectations.
 *
 * @author Owen Feehan
 */
public class FilePathMatcherFixture {

    private final DualDirectoryFixture dualDirectory;

    public FilePathMatcherFixture(TestLoader loader) {
        dualDirectory = new DualDirectoryFixture(loader);
    }

    /**
     * Looks for matches with the {@link FilePathMatcher} both recursively and non-recursively, and
     * checks the the number of files is as expected.
     *
     * @param expectedNumberNonRecursive the number of files expected to match the {@code
     *     predicates}, when run recursively.
     * @param expectedNumberNonRecursive the number of files expected to match the {@code
     *     predicates}, when run non-recursively.
     * @param matcher the matcher to use.
     * @parma assertMessagePrefix a prefix to add to the message used when asserting.
     * @throws FindFilesException if thrown by {@link FilePathMatcher#matchingFiles(Path, boolean)}.
     */
    public void doTest(
            int expectedNumberNonRecursive,
            int expectedNumberRecursive,
            FilePathMatcher matcher,
            String assertMessagePrefix)
            throws InputReadFailedException {
        Path directory = dualDirectory.multiplex(true);

        assertNumberMatchingFiles(
                expectedNumberNonRecursive,
                directory,
                matcher,
                false,
                assertMessagePrefix + "nonRecursive");
        assertNumberMatchingFiles(
                expectedNumberRecursive,
                directory,
                matcher,
                true,
                assertMessagePrefix + "recursive");
    }

    private void assertNumberMatchingFiles(
            int expectedNumber,
            Path directory,
            FilePathMatcher matcher,
            boolean recursive,
            String assertMessage)
            throws InputReadFailedException {
        List<File> foundFiles = matcher.matchingFiles(directory, recursive);
        assertEquals(assertMessage, expectedNumber, foundFiles.size());
    }
}
