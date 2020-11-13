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

package org.anchoranalysis.core.progress;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TraverseDirectoryForProgress {

    /**
     * Performs a breadth-first traversal of the sub-folders until we get at least {@code minNumberDirectories}.
     * on a given level.  These are then used as our progress markers.
     *  
     * @param parent
     * @param minNumberDirectories
     * @param matcherDirectory
     * @param maxDirectoryDepth
     * @return
     * @throws IOException
     */
    public static TraversalResult traverseRecursive(
            Path parent, int minNumberDirectories, Predicate<Path> matcherDirectory, int maxDirectoryDepth)
            throws IOException {

        List<Path> filesOut = new ArrayList<>();
        List<Path> currentDirectories = new ArrayList<>();
        currentDirectories.add(parent);

        int depth = 1;
        while (true) {
            List<Path> filesOutCurrent = new ArrayList<>();
            List<Path> definiteLeafsCurrent = new ArrayList<>();
            List<Path> subDirectories =
                    TraverseDirectoryForProgress.subDirectoriesFor(
                            currentDirectories,
                            definiteLeafsCurrent,
                            Optional.of(filesOutCurrent),
                            matcherDirectory);

            filesOut.addAll(filesOutCurrent);

            if (subDirectories.isEmpty()) {
                return new TraversalResult(new ArrayList<>(), filesOut, depth);
            } else if (subDirectories.size() > minNumberDirectories || depth == maxDirectoryDepth) {
                return new TraversalResult(subDirectories, filesOut, depth);
            }

            currentDirectories = subDirectories;

            depth++;
        }
    }

    public static TraversalResult traverseNotRecursive(Path parent, Predicate<Path> matcherDir)
            throws IOException {
        List<Path> filesOut = new ArrayList<>();
        subDirectoriesFor(parent, Optional.empty(), Optional.of(filesOut), matcherDir);
        return new TraversalResult(new ArrayList<>(), filesOut, 1);
    }

    private static boolean subDirectoriesFor(
            Path parent,
            Optional<List<Path>> directoriesOut,
            Optional<List<Path>> filesOut,
            Predicate<Path> matcherDir)
            throws IOException {

        boolean addedDirectory = false;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent)) {
            for (Path file : stream) {

                if (matcherDir.test(file) && addFileOrDirectory(file, directoriesOut, filesOut)) {
                    addedDirectory = true;
                }
            }
        }

        return addedDirectory;
    }

    /**
     * Adds a path to either the directoriesOut or filesOut
     *
     * @param directoriesOut directories-added (if present)
     * @param filesOut files-added (if present)
     * @return true if directory is added, false otherwise
     */
    private static boolean addFileOrDirectory(
            Path file, Optional<List<Path>> directoriesOut, Optional<List<Path>> filesOut) {
        if (file.toFile().isDirectory()) {
            directoriesOut.ifPresent(list -> list.add(file));
            return true;

        } else {
            filesOut.ifPresent(list -> list.add(file));
            return false;
        }
    }

    private static List<Path> subDirectoriesFor(
            List<Path> parents,
            List<Path> definiteLeafs,
            Optional<List<Path>> filesOut,
            Predicate<Path> matcherDirectory)
            throws IOException {
        List<Path> out = new ArrayList<>();
        for (Path path : parents) {

            if (matcherDirectory.test(path) && !subDirectoriesFor(path, Optional.of(out), filesOut, matcherDirectory)) {
                // If we fail to find any sub-folders, then we have a definite leaf,
                // which we treat separately so as to include it in our final list,
                // but not to recurse further on it.
                definiteLeafs.add(path);
            }
        }
        return out;
    }
}
