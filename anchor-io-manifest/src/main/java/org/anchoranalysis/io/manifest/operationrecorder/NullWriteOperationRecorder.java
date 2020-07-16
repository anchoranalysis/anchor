/* (C)2020 */
package org.anchoranalysis.io.manifest.operationrecorder;

import java.nio.file.Path;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;

public class NullWriteOperationRecorder implements WriteOperationRecorder {

    @Override
    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        // NOTHING TO DO
    }

    @Override
    public WriteOperationRecorder writeFolder(
            Path relativeFolderPath,
            ManifestFolderDescription manifestDescription,
            FolderWriteWithPath folderWrite) {
        return this;
    }
}
