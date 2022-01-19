package org.anchoranalysis.io.input.path.matcher;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
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

    private DualDirectoryFixture dualDirectory =
            new DualDirectoryFixture(TestLoader.createFromMavenWorkingDirectory());

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
        Path directory = dualDirectory.multiplex(true);

        MatchGlob matcher = new MatchGlob();
        matcher.setGlob(glob);

        assertNumberMatchingFiles(
                expectedNumberNonRecursive, directory, matcher, false, "nonRecursive");
        assertNumberMatchingFiles(expectedNumberRecursive, directory, matcher, true, "recursive");
    }

    private void assertNumberMatchingFiles(
            int expectedNumber,
            Path directory,
            MatchGlob matcher,
            boolean recursive,
            String assertMessage)
            throws InputReadFailedException {
        List<File> foundFiles = matcher.matchingFiles(directory, recursive);
        assertEquals(assertMessage, expectedNumber, foundFiles.size());
    }
}
