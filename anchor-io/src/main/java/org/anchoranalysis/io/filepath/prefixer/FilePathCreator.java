/* (C)2020 */
package org.anchoranalysis.io.filepath.prefixer;

import java.nio.file.Path;

/**
 * Generates file-paths
 *
 * @author Owen Feehan
 */
public interface FilePathCreator {

    /**
     * Generates a full path, given the final part of a filePath
     *
     * <p>The prefix is added to final-part to generate a full path.
     *
     * <p>All sub-directories are created if needed to ensure it's possible to write to the
     * fullPath.
     *
     * @param filePathRelative the final part of the filePath
     * @return a resolved path containing the folderPath of FilePathPrefix, the filenamePrefix of
     *     FilePathPrefix and filePathRelative
     */
    Path outFilePath(String filePathRelative);

    /**
     * Extracts a relative-file path (to the folderPath of the FilePathPrefix) from an absolute path
     *
     * <p>This relative-path includes any filenamePrefix added by the FilePathPrefix
     *
     * @param fullPath
     * @return the relative-path
     */
    Path relativePath(Path fullPath);
}
