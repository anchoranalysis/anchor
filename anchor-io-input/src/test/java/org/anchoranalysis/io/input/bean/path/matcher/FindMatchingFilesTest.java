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
