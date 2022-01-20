package org.anchoranalysis.io.input.path.matcher;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.test.TestLoader;
import org.mockito.InOrder;

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
     *     predicates}, when run recursively.
     * @param expectedNumberNonRecursive the number of files expected to match the {@code
     *     predicates}, when run non-recursively.
     * @param nested if true, the nested subdirectory is used, otherwise the flat subdirectory.
     * @param predicates if true, a filter is applied to only detect files that end in ".txt". if
     *     false, no filter is applied.
     * @throws FindFilesException if thrown by {@link FindMatchingFiles#search(Path,
     *     PathMatchConstraints)}.
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

        Progress progress = mock(Progress.class);

        FindMatchingFiles finder = new FindMatchingFiles(recursive, Optional.of(progress));
        List<File> files = finder.search(directory, constraints);

        // Check we found the expected number of files
        assertEquals(assertMessage, expectedNumberFound, files.size());
        verifyProgress(progress);
    }

    /** Verify the methods were called on {@link Progress} roughly as expected. */
    private static void verifyProgress(Progress progress) {
        // Check that progress was handled as expected
        InOrder inOrder = inOrder(progress);
        inOrder.verify(progress, times(1)).open();
        inOrder.verify(progress, times(1)).setMin(anyInt());
        inOrder.verify(progress, times(1)).setMax(anyInt());
        inOrder.verify(progress, atLeastOnce()).update(anyInt());
        inOrder.verify(progress, times(1)).close();
        inOrder.verifyNoMoreInteractions();
    }
}
