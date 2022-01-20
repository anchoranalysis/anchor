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
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.io.input.path.matcher.DualPathPredicates;
import org.anchoranalysis.io.input.path.matcher.FindFilesException;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FindMatchingFiles {

    /**
     * Searches a {@code directory} for files that match the {@code constraints} - with an optional
     * logger.
     *
     * <p>Assign 1 to {@code maxDirectoryDepth} to consider only the immediate files in {@code
     * directory}.
     *
     * @param directory the directory to search.
     * @param constraints the constraints applied to the paths.
     * @param recursive whether to search recursively.
     * @param maxDirectoryDepth limits on the depth of how many sub-directories are to be recursed.
     *     If unassigned, there is no limit.
     * @param logger logs unexpected non-fatal issues that are encountered.
     * @return a newly created list containing all files in {@code directory} that match the
     *     constraints.
     * @throws FindFilesException if a fatal error is encountered during the search.
     */
    public static List<File> search(
            Path directory,
            DualPathPredicates predicates,
            boolean recursive,
            Optional<Integer> maxDirectoryDepth,
            Optional<Logger> logger)
            throws FindFilesException {

        List<File> out = new LinkedList<>();
        try {
            Files.walkFileTree(
                    directory,
                    EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                    recursive ? maxDirectoryDepth.orElse(Integer.MAX_VALUE) : 1,
                    new ConsumeMatchingFilesVisitor(predicates, out::add));
        } catch (AccessDeniedException e) {
            throw new FindFilesException(String.format("Cannot access directory: %s", e.getFile()));
        } catch (FileSystemException e) {
            throw new FindFilesException(
                    String.format(
                            "An filesystem error occurring accessing directory: %s", e.getFile()));
        } catch (IOException e) {
            throw new FindFilesException(
                    String.format("An IO error occurring accessing directory: %s", e.toString()));
        }
        return out;
    }
}
