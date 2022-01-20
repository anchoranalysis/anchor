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
