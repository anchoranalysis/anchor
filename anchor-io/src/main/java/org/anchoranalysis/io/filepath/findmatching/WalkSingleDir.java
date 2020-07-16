/* (C)2020 */
package org.anchoranalysis.io.filepath.findmatching;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;

class WalkSingleDir {

    private WalkSingleDir() {}

    public static void apply(Path dir, PathMatchConstraints constraints, List<File> listOut)
            throws FindFilesException {

        try {
            Files.walkFileTree(
                    dir,
                    EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                    constraints.getMaxDirDepth(),
                    new AddFilesToList(
                            listOut, constraints.getMatcherFile(), constraints.getMatcherDir()));
        } catch (AccessDeniedException e) {
            throw new FindFilesException(String.format("Cannot access directory: %s", e.getFile()));
        } catch (FileSystemException e) {
            throw new FindFilesException(
                    String.format(
                            "An file-system error occurring accessing directory: %s", e.getFile()));
        } catch (IOException e) {
            throw new FindFilesException(
                    String.format("An IO error occurring accessing directory: %s", e.toString()));
        }
    }
}
