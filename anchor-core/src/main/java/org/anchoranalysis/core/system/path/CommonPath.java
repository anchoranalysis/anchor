/*-
 * #%L
 * anchor-core
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
package org.anchoranalysis.core.system.path;

import com.owenfeehan.pathpatternfinder.commonpath.FindCommonPathElements;
import com.owenfeehan.pathpatternfinder.commonpath.PathElements;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;

/**
 * Finds the common root directory of a set of paths.
 *
 * <p>This is the maximal leftmost common part of all paths (when treated in a canonical way).
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonPath {

    /**
     * Finds the common root directory of several {@link Path}s.
     *
     * @param paths the paths
     * @return the common root directory, if it exists.
     */
    public static Optional<Path> fromPaths(Iterable<Path> paths) {
        return FindCommonPathElements.findForFilePaths(paths).map(PathElements::toPath);
    }

    /**
     * Finds the common root directory of several paths, encoded in {@link String}s.
     *
     * @param paths the paths
     * @return the common root directory, if it exists.
     */
    public static Optional<Path> fromStrings(Collection<String> paths) {
        return convert(paths, Paths::get);
    }

    /**
     * Finds the common root directory of the paths to several {@link File}s.
     *
     * @param files the files
     * @return the common root directory, if it exists.
     */
    public static Optional<Path> fromFiles(Collection<File> files) {
        return convert(files, File::toPath);
    }

    private static <T> Optional<Path> convert(Collection<T> paths, Function<T, Path> convert) {
        List<Path> converted = FunctionalList.mapToList(paths, convert);
        return fromPaths(converted);
    }
}
