/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.io.input;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.system.path.FilePathToUnixStyleConverter;
import org.anchoranalysis.io.input.file.NamedFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * Find all files in the input directory are not used as inputs.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FindNonInputFiles {

    /**
     * Find all files in the input directory are not used as inputs.
     *
     * @param inputDirectory the input-directory
     * @param inputs the inputs contained in {@code inputDirectory} whose paths should not be
     *     returned.
     * @param <T> input-type
     * @return the files, with an identifier derived relative to the input-directory
     * @throws OperationFailedException if a file exists that isn't contained the input-directory
     *     (or it's sub-directories)
     */
    public static <T extends InputFromManager> Collection<NamedFile> from(
            Path inputDirectory, List<T> inputs) throws OperationFailedException {

        Set<Path> set = convertToSet(findAllFilesInDirectory(inputDirectory));

        removeAnyInputPaths(set, inputs);

        return FunctionalList.mapToList(
                set, OperationFailedException.class, file -> addIdentifier(file, inputDirectory));
    }

    private static Collection<File> findAllFilesInDirectory(Path directory) {
        return FileUtils.listFiles(
                directory.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    }

    /** Convert (normalized absolute) paths of the files into an ordered set. */
    private static Set<Path> convertToSet(Collection<File> files) {
        return new TreeSet<>(FunctionalList.mapToList(files, FindNonInputFiles::makeAbsolute));
    }

    /** Remove any paths from a set which are associated with any of thei nputs. */
    private static <T extends InputFromManager> void removeAnyInputPaths(
            Set<Path> setToRemoveFrom, List<T> inputs) {
        for (T input : inputs) {
            input.allAssociatedPaths().forEach(path -> setToRemoveFrom.remove(makeAbsolute(path)));
        }
    }

    /**
     * Derive an identifier for each file.
     *
     * <p>A check also occurs that the file is located in the directory.
     *
     * @param path a path to a file
     * @param directory the path to the directory, in which file should be contained
     * @return the file with an identifier (the relative-path to the directory) assigned.
     * @throws OperationFailedException if {@code pathFile} is not located in the directory (or its
     *     sub-directories).
     */
    private static NamedFile addIdentifier(Path path, Path directory)
            throws OperationFailedException {
        directory = makeAbsolute(directory);
        if (path.startsWith(directory)) {
            String identifier =
                    FilePathToUnixStyleConverter.toStringUnixStyle(directory.relativize(path));
            return new NamedFile(identifier, path.toFile());
        } else {
            throw new OperationFailedException(
                    String.format(
                            "An input file exists outside the input-directory: %s with input-directory %s",
                            path, directory));
        }
    }

    private static Path makeAbsolute(File file) {
        return makeAbsolute(file.toPath());
    }

    private static Path makeAbsolute(Path path) {
        return path.normalize().toAbsolutePath();
    }
}
