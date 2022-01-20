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

package org.anchoranalysis.io.input.bean.path.matcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.functional.OptionalFactory;
import org.anchoranalysis.core.functional.checked.CheckedPredicate;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.io.input.InputContextParameters;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.io.input.bean.InputManagerParameters;
import org.anchoranalysis.io.input.path.matcher.DualPathPredicates;
import org.anchoranalysis.io.input.path.matcher.FindFilesException;
import org.anchoranalysis.io.input.path.matcher.FindMatchingFiles;
import org.anchoranalysis.io.input.path.matcher.PathMatchConstraints;

/**
 * Matches file-paths against some kind of pattern.
 *
 * <p>Search operations can be executed against this pattern, to find all files in a directory that
 * match.
 *
 * @author Owen Feehan
 */
public abstract class FilePathMatcher extends AnchorBean<FilePathMatcher> {

    /**
     * Like {@link #matchingFiles(Path, boolean, boolean, boolean, Optional, Optional)} but uses
     * sensible defaults.
     *
     * <p>Hidden files are ignored.
     *
     * <p>Continues even when a directory-access-error occurs, without throwing an exception.
     *
     * <p>No maximum directory depth is imposed.
     *
     * <p>No parameters are applied.
     *
     * @param directory root directory to search.
     * @param recursive whether to recursively search.
     * @return a collection of files matching the conditions.
     * @throws InputReadFailedException if an error occurrs reading/writing or interacting with the
     *     filesystem.
     */
    public List<File> matchingFiles(Path directory, boolean recursive)
            throws InputReadFailedException {
        return matchingFiles(directory, recursive, false, true, Optional.empty(), Optional.empty());
    }
    /**
     * Finds a collection of files that match particular conditions on their paths.
     *
     * @param directory root directory to search.
     * @param recursive whether to recursively search.
     * @param ignoreHidden whether to ignore hidden files/directories or not.
     * @param acceptDirectoryErrors if true, continues when a directory-access-error occurs (logging
     *     it), otherwise throws an exception.
     * @param maxDirectoryDepth a maximum depth in directories to search.
     * @param parameters parameters providing input-context
     * @return a collection of files matching the conditions.
     * @throws InputReadFailedException if an error occurs reading/writing or interacting with the
     *     filesystem.
     */
    public List<File> matchingFiles(
            Path directory,
            boolean recursive,
            boolean ignoreHidden,
            boolean acceptDirectoryErrors,
            Optional<Integer> maxDirectoryDepth,
            Optional<InputManagerParameters> parameters)
            throws InputReadFailedException {

        checkDirectoryPreconditions(directory);

        DualPathPredicates predicates =
                createPredicates(
                        directory,
                        ignoreHidden,
                        parameters.map(InputManagerParameters::getInputContext));

        Optional<Logger> logger =
                OptionalFactory.create(
                        acceptDirectoryErrors && parameters.isPresent(),
                        () -> parameters.get().getLogger()); // NOSONAR
        Optional<Progress> progress = parameters.map(InputManagerParameters::getProgress);

        PathMatchConstraints constraints = new PathMatchConstraints(predicates, maxDirectoryDepth);
        try {
            return new FindMatchingFiles(recursive && canMatchSubdirectories(), progress)
                    .search(directory, constraints, logger);
        } catch (FindFilesException e) {
            throw new InputReadFailedException("Cannot find matching files", e);
        }
    }

    /**
     * Create a predicate to be used for matching against path.
     *
     * @param directory the directory being searched. Only paths in this directory (or its
     *     subdirectories) will ever be passed to the predicate.
     * @param inputContext the input-context.
     * @return a predicate that can be used to accept or reject a path (contained in {@code
     *     directory}.
     * @throws InputReadFailedException if the testing of the predicate fails.
     */
    protected abstract CheckedPredicate<Path, IOException> createMatcherFile(
            Path directory, Optional<InputContextParameters> inputContext)
            throws InputReadFailedException;

    /**
     * Determines if it possible to match a file in a subdirectory.
     *
     * <p>If it impossible to match a subdirectory, this allows us to disable any recursive search,
     * as it is pointless effort.
     *
     * @return true if its possible for the predicate returned by {@code createMatcherFile} to match
     *     a file in a subdirectory, false otherwise.
     */
    protected abstract boolean canMatchSubdirectories();

    private DualPathPredicates createPredicates(
            Path directory, boolean ignoreHidden, Optional<InputContextParameters> parameters)
            throws InputReadFailedException {

        // Many checks are possible on a file, including whether it is hidden or not
        CheckedPredicate<Path, IOException> fileMatcher =
                maybeAddIgnoreHidden(ignoreHidden, createMatcherFile(directory, parameters));

        // The only check on a directory is (maybe) whether it is hidden or not
        CheckedPredicate<Path, IOException> directoryMatcher =
                maybeAddIgnoreHidden(ignoreHidden, path -> true);

        return new DualPathPredicates(fileMatcher, directoryMatcher);
    }

    private CheckedPredicate<Path, IOException> maybeAddIgnoreHidden(
            boolean ignoreHidden, CheckedPredicate<Path, IOException> predicate) {
        if (ignoreHidden) {
            return path -> predicate.test(path) && HiddenPathChecker.includePath(path);
        } else {
            return predicate;
        }
    }

    /** Checks that the directory path satisifies preconditions. */
    private static void checkDirectoryPreconditions(Path directory)
            throws InputReadFailedException {
        if (directory.toString().isEmpty()) {
            throw new InputReadFailedException(
                    "The directory is unspecified (an empty string) which is not allowed. Consider using '.' for the current working directory");
        }

        if (!directory.toFile().exists() || !directory.toFile().isDirectory()) {
            throw new InputReadFailedException(
                    String.format(
                            "Directory '%s' does not exist",
                            directory.toAbsolutePath().normalize()));
        }
    }
}
