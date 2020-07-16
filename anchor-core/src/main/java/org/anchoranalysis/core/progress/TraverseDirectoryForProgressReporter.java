/* (C)2020 */
package org.anchoranalysis.core.progress;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TraverseDirectoryForProgressReporter {

    private TraverseDirectoryForProgressReporter() {}

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

    // Does a breadth first traversal of the sub-folders until we get at least minNumFolders on
    //  a given level.  These are then used as our progress markers.
    //
    // if we can't get minNumFolders, it returns an empty array
    public static TraversalResult traverseRecursive(
            Path parent, int minNumDirectories, Predicate<Path> matcherDir, int maxDirDepth)
            throws IOException {

        List<Path> filesOut = new ArrayList<>();
        List<Path> currentFolders = new ArrayList<>();
        currentFolders.add(parent);

        int depth = 1;
        while (true) {
            List<Path> filesOutCurrent = new ArrayList<>();
            List<Path> definiteLeafsCurrent = new ArrayList<>();
            List<Path> subDirectories =
                    TraverseDirectoryForProgressReporter.subDirectoriesFor(
                            currentFolders,
                            definiteLeafsCurrent,
                            Optional.of(filesOutCurrent),
                            matcherDir);

            filesOut.addAll(filesOutCurrent);

            if (subDirectories.isEmpty()) {
                return new TraversalResult(new ArrayList<>(), filesOut, depth);
            } else if (subDirectories.size() > minNumDirectories || depth == maxDirDepth) {
                return new TraversalResult(subDirectories, filesOut, depth);
            }

            currentFolders = subDirectories;

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
                // If we fail to find any sub-folders, then we have a definite leaf, which we treat
                // seperately
                //   so as to include it in our final list, but not to recurse further on it
                definiteLeafs.add(p);
            }
        }
        return out;
    }
}
