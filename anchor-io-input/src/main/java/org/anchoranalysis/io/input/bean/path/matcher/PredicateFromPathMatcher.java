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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedPredicate;
import org.anchoranalysis.core.system.path.PathDifference;
import org.anchoranalysis.core.system.path.PathDifferenceException;

/**
 * Creates a predicate that performs matching, using a {@link PathMatcher} to perform the matching.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PredicateFromPathMatcher {

    /**
     * Creates a predicate that performs matching, using a {@link PathMatcher} to perform the
     * matching.
     *
     * @param directory the directory in which the search commenced.
     * @param filterType the string before the : in {@code FileSystem:getPathMatcher} i.e. glob or
     *     regex
     * @param fileFilter the string after the : in {@code FileSystem:getPathMatcher} i.e. the
     *     expression for a glob or regex
     * @param filenameOnly if true, the match only occurs against the filename (ignoring the rest of
     *     the path). if false, it is applied to the entire relative-path between {@code directory}
     *     and the path being considered, including the filename.
     * @return a newly created predicate that, given a path, returns true if matches, and false
     *     otherwise.
     */
    public static CheckedPredicate<Path, IOException> create(
            Path directory, String filterType, String fileFilter, boolean filenameOnly) {
        PathMatcher matcher =
                directory.getFileSystem().getPathMatcher(filterType + ":" + fileFilter);
        return predicateForPathMatcher(directory, matcher::matches, filenameOnly);
    }

    /**
     * Creates a predicate that performs matching, using internally a {@code Predicate<Path>} for the final check of a path.
     *
     * <p>This wraps an existing predicate that is designed to work with a simple path, to a more advanced predicate
     * that ensures an appropriate relative-path or filename is passed, as appropriate.
     *
     * @param directory the directory in which the search commenced.
     * @param filterType the string before the : in {@code FileSystem:getPathMatcher} i.e. glob or regex
     * @param fileFilter the string after the : in {@code FileSystem:getPathMatcher} i.e. the expression for a glob or regex
     * @param filenameOnly if true, the match only occurs against the filename (ignoring the rest of the path). if false, it is applied to the entire relative-path between {@code directory} and the path being considered, including the filename.
     * @return a newly created predicate that, given a path, returns true if matches, and false otherwise.
     */
    private static CheckedPredicate<Path, IOException> predicateForPathMatcher(
            Path directory, Predicate<Path> predicate, boolean filenameOnly) {
        if (filenameOnly) {
            return path -> matchFilename(path, predicate);
        } else {
            return path -> matchRelativePath(path, directory, predicate);
        }
    }

    /** Match against the relative path from {@code directory} to {@code path}. */
    private static boolean matchRelativePath(Path path, Path directory, Predicate<Path> predicate)
            throws IOException {
        try {
            PathDifference difference = PathDifference.differenceFrom(directory, path);
            return predicate.test(difference.combined());
        } catch (PathDifferenceException e) {
            throw new IOException(e);
        }
    }

    /** Match against the filename only in {@code path}. */
    private static boolean matchFilename(Path path, Predicate<Path> predicate) {
        return predicate.test(path.getFileName());
    }
}
