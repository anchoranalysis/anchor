/*-
 * #%L
 * anchor-io-input
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
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.functional.checked.CheckedPredicate;

/**
 * A {@code Predicate<Path} for both a file and a directory.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class DualPathPredicates {

    /** Only accepts files where the predicate returns true */
    private CheckedPredicate<Path, IOException> file;

    /** Only accepts any containing directories where the predicate returns true */
    @Getter private CheckedPredicate<Path, IOException> directory;

    /**
     * Whether the path to a particular file matches the predicate {@code file}?
     *
     * @param path the path.
     * @return true if matches the predicate.
     * @throws IOException if an error occurs testing the path.
     */
    public boolean matchFile(Path path) throws IOException {
        return file.test(path);
    }

    /**
     * Calls a {@link Consumer} on any path that matches the predicate {@code file}.
     *
     * @param paths the paths to test.
     * @param consumerMatching the consumer to call if a path matches.
     * @throws FindFilesException if an error occurs testing a path.
     */
    public void consumeMatchingFiles(List<Path> paths, Consumer<File> consumerMatching)
            throws FindFilesException {
        for (Path path : paths) {
            try {
                if (file.test(path)) {
                    consumerMatching.accept(path.normalize().toFile());
                }
            } catch (IOException e) {
                throw new FindFilesException(
                        "An error occurred evaluating the predicate on a file-path", e);
            }
        }
    }

    /**
     * Creates a new list of paths to leaf-directories that match the predicate {@code directory}.
     *
     * @param leafDirectories the paths to the directories to consider.
     * @return a newly created list containing the elements of {@code leafDirectories} that match
     *     {@code directory}.
     * @throws FindFilesException if an error occurs testing a path.
     */
    public List<Path> matchingLeafDirectories(List<Path> leafDirectories)
            throws FindFilesException {
        try {
            return FunctionalList.filterToList(leafDirectories, IOException.class, directory);
        } catch (IOException e) {
            throw new FindFilesException(
                    "An error occurred evaluating the predicate on a file-path", e);
        }
    }
}
