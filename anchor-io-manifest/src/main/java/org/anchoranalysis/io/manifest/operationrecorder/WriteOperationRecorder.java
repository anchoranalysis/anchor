/* (C)2020 */
package org.anchoranalysis.io.manifest.operationrecorder;

import java.nio.file.Path;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;

public interface WriteOperationRecorder {

    /**
     * Writes a new file to the manifest
     *
     * @param outputName the "output name" used to generate the file
     * @param manifestDescription a description of the directory
     * @param outFilePath the path it's wrriten to relative to the folder
     * @param index an index, if it's part of a set of files
     */
    void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index);

    /**
     * Writes a new sub-directory to the manifest
     *
     * @param relativeFolderPath the path of the directory relative to the parent
     * @param manifestDescription a description of the directory
     * @param folderWrite the folder object to write
     * @return
     */
    WriteOperationRecorder writeFolder(
            Path relativeFolderPath,
            ManifestFolderDescription manifestDescription,
            FolderWriteWithPath folderWrite);
}
