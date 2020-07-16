/* (C)2020 */
package org.anchoranalysis.io.filepath.prefixer;

import java.nio.file.Path;

public class FilePathPrefix implements FilePathCreator {

    private Path folderPath;
    private String filenamePrefix = "";

    public FilePathPrefix(Path folderPath) {
        super();
        setFolderPath(folderPath.normalize());
    }

    public FilePathPrefix(Path folderPath, String filenamePrefix) {
        this.folderPath = folderPath;
        this.filenamePrefix = filenamePrefix;
    }

    public Path getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(Path folderPath) {
        this.folderPath = folderPath.normalize();
    }

    public String getFilenamePrefix() {
        return filenamePrefix;
    }

    public void setFilenamePrefix(String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public Path getCombinedPrefix() {
        return getFolderPath().resolve(getFilenamePrefix());
    }

    @Override
    public Path outFilePath(String filePathRelative) {

        String combinedFilePath = filenamePrefix + filePathRelative;

        return folderPath.resolve(combinedFilePath);
    }

    @Override
    public Path relativePath(Path fullPath) {
        return folderPath.relativize(fullPath);
    }
}
