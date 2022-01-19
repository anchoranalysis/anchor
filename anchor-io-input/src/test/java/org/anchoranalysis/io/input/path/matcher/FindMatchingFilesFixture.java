package org.anchoranalysis.io.input.path.matcher;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
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
     * @param expectedNumberNonRecursive the number of files expected, when run recursively.
     * @param expectedNumberNonRecursive the number of files expected, when run non-recursively.
     * @param nested if true, the nested subdirectory is used, otherwise the flat subdirectory.
     * @param predicates if true, a filter is applied to only detect files that end in ".txt". if
     *     false, no filter is applied.
     * @throws FindFilesException
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

        PathMatchConstraints constraints = new PathMatchConstraints(predicates);

        FindMatchingFiles finder = new FindMatchingFiles(recursive);
        List<File> files = finder.search(directory, constraints, Optional.empty());

        assertEquals(assertMessage, expectedNumberFound, files.size());
    }
}
