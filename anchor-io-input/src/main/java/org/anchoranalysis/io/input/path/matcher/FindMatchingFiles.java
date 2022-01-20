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
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressIncrement;
import org.anchoranalysis.io.input.bean.path.matcher.FilePathMatcher; // NOSONAR

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
 * <p>TODO guarantee that only relevant paths are passed to the predicates, which allows removal of
 * logic to calculate the relative-paths in {@link FilePathMatcher}.
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
     * Searches a {@code directory} for files that match the {@code constraints} - without a logger.
     *
     * @param directory the directory to search.
     * @param constraints the constraints applied to the paths.
     * @return a newly created list containing all files in {@code directory} that match the
     *     constraints.
     * @throws FindFilesException if a fatal error is encountered during the search.
     */
    public List<File> search(Path directory, PathMatchConstraints constraints)
            throws FindFilesException {
        return search(directory, constraints, Optional.empty());
    }

    /**
     * Searches a {@code directory} for files that match the {@code constraints} - with an optional
     * logger.
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
                constraints.getPredicates().matchingLeafDirectories(traversal.getLeafDirectories());

        Optional<ProgressIncrement> progressIncrement =
                progress.map(
                        progressReporter ->
                                createAndOpenProgress(
                                        progressReporter, leafDirectories.size() + 1));

        // We first check the files that we remembered from our folder search
        constraints.getPredicates().consumeMatchingFiles(traversal.getFiles(), out::add);

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

    private static ProgressIncrement createAndOpenProgress(Progress progress, int numberElements) {
        ProgressIncrement progressIncrement = new ProgressIncrement(progress);
        progressIncrement.open();
        progressIncrement.setMin(0);
        progressIncrement.setMax(numberElements);
        return progressIncrement;
    }
}
