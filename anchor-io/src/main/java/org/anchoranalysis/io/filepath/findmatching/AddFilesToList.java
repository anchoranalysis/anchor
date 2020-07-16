/* (C)2020 */
package org.anchoranalysis.io.filepath.findmatching;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.Predicate;

class AddFilesToList extends SimpleFileVisitor<Path> {

    private List<File> list;
    private Predicate<Path> matcherFile;
    private Predicate<Path> matcherDir;

    public AddFilesToList(
            List<File> list, Predicate<Path> matcherFile, Predicate<Path> matcherDir) {
        super();
        this.list = list;
        this.matcherFile = matcherFile;
        this.matcherDir = matcherDir;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        if (attrs.isRegularFile() && !attrs.isDirectory() && matcherFile.test(file)) {
            list.add(file.normalize().toFile());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
            throws IOException {

        if (!matcherDir.test(dir)) {
            return FileVisitResult.SKIP_SUBTREE;
        }

        return super.preVisitDirectory(dir, attrs);
    }
}
