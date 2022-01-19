/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.input.path.matcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.functional.checked.CheckedPredicate;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressIncrement;
import org.anchoranalysis.core.progress.TraversalResult;
import org.anchoranalysis.core.progress.TraverseDirectoryForProgress;

/**
 * Finds files in a {@code directory} that satisfy certain constraints.
 *
 * <p>It may be searched recursively or not.
 *
 * <p>It is designed to keep a {@link Progress} approximately up to date, so it can be visually
 * communicated how much of the search has progressed. It achieves this <b>very approximately</b> by
 * searching for top-level directories, and considering that each represents a similar block of
 * progress.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class FindMatchingFiles {

    /** Whether to recursively iterate through directories. */
    private final boolean recursive;

    /** The progress reporter. */
    private final Optional<Progress> progress;

    /**
     * Create indicating whether to search recursively or not.
     *
     * @param recursive whether to search recursively.
     */
    public FindMatchingFiles(boolean recursive) {
        this.recursive = recursive;
        this.progress = Optional.empty();
    }

    /**
     * Searches a {@code directory} for files that match the {@code constraints}.
     *
     * @param directory the directory to search.
     * @param constraints the constraints applied to the paths.
     * @param logger logs unexpected non-fatal issues that are encountered.
     * @return a newly created list containing all files in {@code directory} that match the
     *     constraints.
     * @throws FindFilesException if a fatal error is encountered during the search.
     */
    public List<File> search(
            Path directory, PathMatchConstraints constraints, Optional<Logger> logger)
            throws FindFilesException {

        try {
            TraversalResult traversal;
            if (recursive) {
                traversal =
                        TraverseDirectoryForProgress.traverseRecursive(
                                directory,
                                20,
                                constraints.getPredicates().getDirectory(),
                                constraints.getMaxDirectoryDepth());
            } else {
                traversal =
                        TraverseDirectoryForProgress.traverseNotRecursive(
                                directory, constraints.getPredicates().getDirectory());
            }
            return convertToList(traversal, constraints, logger);

        } catch (IOException e) {
            throw new FindFilesException("A failure occurred searching a directory for files", e);
        }
    }

    private List<File> convertToList(
            TraversalResult traversal, PathMatchConstraints constraints, Optional<Logger> logger)
            throws FindFilesException {

        List<File> out = new LinkedList<>();

        List<Path> leafDirectories =
                filterLeafDirectories(
                        traversal.getLeafDirectories(), constraints.getPredicates().getDirectory());

        Optional<ProgressIncrement> progressIncrement =
                progress.map(
                        progressReporter ->
                                createAndOpenProgress(
                                        progressReporter, leafDirectories.size() + 1));

        // We first check the files that we remembered from our folder search
        filesFromDirectorySearch(traversal.getFiles(), constraints.getPredicates().getFile(), out);

        progressIncrement.ifPresent(ProgressIncrement::update);

        int remainingDirectoryDepth = constraints.getMaxDirectoryDepth() - traversal.getDepth() + 1;
        assert remainingDirectoryDepth >= 1;
        otherFiles(
                leafDirectories,
                constraints.replaceMaxDirectoryDepth(remainingDirectoryDepth),
                out,
                progressIncrement,
                logger);

        progressIncrement.ifPresent(ProgressIncrement::close);

        return out;
    }

    private static ProgressIncrement createAndOpenProgress(Progress progress, int numberElements) {
        ProgressIncrement progressIncrement = new ProgressIncrement(progress);
        progressIncrement.open();
        progressIncrement.setMin(0);
        progressIncrement.setMax(numberElements);
        return progressIncrement;
    }

    private static List<Path> filterLeafDirectories(
            List<Path> leafDirectories, CheckedPredicate<Path, IOException> directoryMatcher)
            throws FindFilesException {
        try {
            return FunctionalList.filterToList(
                    leafDirectories, IOException.class, directoryMatcher);
        } catch (IOException e) {
            throw new FindFilesException(
                    "An error occurred evaluating the predicate on a file-path", e);
        }
    }

    private static void filesFromDirectorySearch(
            List<Path> filesOut, CheckedPredicate<Path, IOException> matcher, List<File> listOut)
            throws FindFilesException {
        for (Path path : filesOut) {
            try {
                if (matcher.test(path)) {
                    listOut.add(path.normalize().toFile());
                }
            } catch (IOException e) {
                throw new FindFilesException(
                        "An error occurred evaluating the predicate on a file-path", e);
            }
        }
    }

    /**
     * @param progressDirectories
     * @param pathMatchConstraints
     * @param listOut
     * @param progressIncrement
     * @param logger if defined, any directory errors are written to this, instead of throwing an
     *     exception
     * @throws FindFilesException if logger is not defined, and a directory error occurs
     */
    private void otherFiles(
            List<Path> progressDirectories,
            PathMatchConstraints pathMatchConstraints,
            List<File> listOut,
            Optional<ProgressIncrement> progressIncrement,
            Optional<Logger> logger)
            throws FindFilesException {
        // Then every other folder is treated as a bucket
        for (Path directoryProgress : progressDirectories) {

            try {
                WalkSingleDirectory.apply(directoryProgress, pathMatchConstraints, listOut);
            } catch (FindFilesException e) {
                if (logger.isPresent()) {
                    logger.get().errorReporter().recordError(FindMatchingFiles.class, e);
                } else {
                    // Rethrow the exception
                    throw e;
                }
            } finally {
                progressIncrement.ifPresent(ProgressIncrement::update);
            }
        }
    }
}
