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

/**
 * Traverses files and subdirectories contained in a directory, in a way that is useful for tracking
 * progress of processing algorithms that operate on each file.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TraverseDirectoryForProgress {

    /**
     * Performs a breadth-first traversal of the subdirectories of a {@code path} <i>recursively</i>
     * until we get at least {@code minNumberDirectories} on a given level.
     *
     * <p>At this point, traversal no longer continues.
     *
     * <p>Traversal will also stop early if the depth of traversed subdirectories reaches {@code
     * maxDirectoryDepth}.
     *
     * @param path the path whose subdirectories will be traversed.
     * @param minNumberDirectories the minimum number of sub-directories that should exist before
     *     traversal is seopped.
     * @param matcherDirectory only subdirectories that match this predicate are considered
     * @param maxDirectoryDepth the maximum allowable depth of subdirectories to be traversed.
     *     Traversal will be complete to avoid exceeding it.
     * @return a newly created {@link TraversalResult} indicating which subdirectories and files
     *     have been traversed.
     * @throws IOException if any file or subdirectory cannot be accessed or traversed.
     */
    public static TraversalResult traverseRecursive(
            Path path,
            int minNumberDirectories,
            Predicate<Path> matcherDirectory,
            int maxDirectoryDepth)
            throws IOException {

        List<Path> filesOut = new ArrayList<>();
        List<Path> currentDirectories = new ArrayList<>();
        currentDirectories.add(path);

        int depth = 1;
        while (true) {
            List<Path> filesOutCurrent = new ArrayList<>();
            List<Path> definiteLeafsCurrent = new ArrayList<>();
            List<Path> subdirectories =
                    TraverseDirectoryForProgress.subdirectoriesFor(
                            currentDirectories,
                            definiteLeafsCurrent,
                            Optional.of(filesOutCurrent),
                            matcherDirectory);

            filesOut.addAll(filesOutCurrent);

            if (subdirectories.isEmpty()) {
                return new TraversalResult(new ArrayList<>(), filesOut, depth);
            } else if (subdirectories.size() > minNumberDirectories || depth == maxDirectoryDepth) {
                return new TraversalResult(subdirectories, filesOut, depth);
            }

            currentDirectories = subdirectories;

            depth++;
        }
    }

    /**
     * Traverses the subdirectories in {@code path} <i>without any recursion</i> into further
     * sub-directories.
     *
     * @param path the path whose subdirectories will be traversed.
     * @param matcherDirectory only subdirectories that match this predicate are considered
     * @return a newly created {@link TraversalResult} indicating which subdirectories and files
     *     have been traversed.
     * @throws IOException if any file or subdirectory cannot be accessed or traversed.
     */
    public static TraversalResult traverseNotRecursive(Path path, Predicate<Path> matcherDirectory)
            throws IOException {
        List<Path> filesOut = new ArrayList<>();
        subdirectoriesFor(path, Optional.empty(), Optional.of(filesOut), matcherDirectory);
        return new TraversalResult(new ArrayList<>(), filesOut, 1);
    }

    private static boolean subdirectoriesFor(
            Path parent,
            Optional<List<Path>> directoriesOut,
            Optional<List<Path>> filesOut,
            Predicate<Path> matcherDirectory)
            throws IOException {

        boolean addedDirectory = false;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent)) {
            for (Path file : stream) {

                if (matcherDirectory.test(file)
                        && addFileOrDirectory(file, directoriesOut, filesOut)) {
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

    private static List<Path> subdirectoriesFor(
            List<Path> parents,
            List<Path> definiteLeafs,
            Optional<List<Path>> filesOut,
            Predicate<Path> matcherDirectory)
            throws IOException {
        List<Path> out = new ArrayList<>();
        for (Path path : parents) {

            if (matcherDirectory.test(path)
                    && !subdirectoriesFor(path, Optional.of(out), filesOut, matcherDirectory)) {
                // If we fail to find any sub-folders, then we have a definite leaf,
                // which we treat separately so as to include it in our final list,
                // but not to recurse further on it.
                definiteLeafs.add(path);
            }
        }
        return out;
    }
}
