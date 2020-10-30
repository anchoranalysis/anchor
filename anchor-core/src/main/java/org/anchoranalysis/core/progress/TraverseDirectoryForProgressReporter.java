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
public class TraverseDirectoryForProgressReporter {

    public static class TraversalResult {

        /**
         * All the directories in the bottom-most depth that was traversed. These directories were
         * not traversed
         */
        private List<Path> leafDirectories;

        /**
         * All files in any directories that have been traversed.
         *
         * <p>Note:No files from leafDirectories are present as it was not yet traversed
         */
        private List<Path> files;

        /**
         * The depth of directories processed, where 1 = root directory traversed, 2 = root
         * directory + one further level etc.
         */
        private int depth;

        public TraversalResult(List<Path> leafDirectories, List<Path> files, int depth) {
            super();
            this.leafDirectories = leafDirectories;
            this.files = files;
            this.depth = depth;
        }

        public List<Path> getFiles() {
            return files;
        }

        public List<Path> getLeafDirectories() {
            return leafDirectories;
        }

        public int getDepth() {
            return depth;
        }
    }

    // Does a breadth first traversal of the sub-folders until we get at least minNumberDirectories on
    //  a given level.  These are then used as our progress markers.
    //
    // if we can't get minNumberDirectories, it returns an empty array
    public static TraversalResult traverseRecursive(
            Path parent, int minNumberDirectories, Predicate<Path> matcherDir, int maxDirDepth)
            throws IOException {

        List<Path> filesOut = new ArrayList<>();
        List<Path> currentDirectories = new ArrayList<>();
        currentDirectories.add(parent);

        int depth = 1;
        while (true) {
            List<Path> filesOutCurrent = new ArrayList<>();
            List<Path> definiteLeafsCurrent = new ArrayList<>();
            List<Path> subDirectories =
                    TraverseDirectoryForProgressReporter.subDirectoriesFor(
                            currentDirectories,
                            definiteLeafsCurrent,
                            Optional.of(filesOutCurrent),
                            matcherDir);

            filesOut.addAll(filesOutCurrent);

            if (subDirectories.isEmpty()) {
                return new TraversalResult(new ArrayList<>(), filesOut, depth);
            } else if (subDirectories.size() > minNumberDirectories || depth == maxDirDepth) {
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

                if (!matcherDir.test(file)) {
                    continue;
                }

                if (addFileOrDirectory(file, directoriesOut, filesOut)) {
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
            Predicate<Path> matcherDir)
            throws IOException {
        List<Path> out = new ArrayList<>();
        for (Path p : parents) {

            if (!matcherDir.test(p)) {
                continue;
            }

            if (!subDirectoriesFor(p, Optional.of(out), filesOut, matcherDir)) {
                // If we fail to find any sub-folders, then we have a definite leaf,
                // which we treat separately so as to include it in our final list,
                // but not to recurse further on it.
                definiteLeafs.add(p);
            }
        }
        return out;
    }
}
