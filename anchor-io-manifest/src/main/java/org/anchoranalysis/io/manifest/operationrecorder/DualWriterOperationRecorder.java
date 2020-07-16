/* (C)2020 */
package org.anchoranalysis.io.manifest.operationrecorder;

import java.nio.file.Path;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;

/**
 * Allows two IWriteOperationRecorder function together as if they are one
 *
 * <p>Every operation is applied to both.
 *
 * @author Owen Feehan
 */
public class DualWriterOperationRecorder implements WriteOperationRecorder {

    private WriteOperationRecorder recorder1;
    private WriteOperationRecorder recorder2;

    public DualWriterOperationRecorder(
            WriteOperationRecorder recorder1, WriteOperationRecorder recorder2) {
        super();
        this.recorder1 = recorder1;
        this.recorder2 = recorder2;
    }

    @Override
    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        recorder1.write(outputName, manifestDescription, outFilePath, index);
        recorder2.write(outputName, manifestDescription, outFilePath, index);
    }

    @Override
    public WriteOperationRecorder writeFolder(
            Path relativeFolderPath,
            ManifestFolderDescription manifestDescription,
            FolderWriteWithPath folderWrite) {
        WriteOperationRecorder folder1 =
                recorder1.writeFolder(relativeFolderPath, manifestDescription, folderWrite);
        WriteOperationRecorder folder2 =
                recorder2.writeFolder(relativeFolderPath, manifestDescription, folderWrite);
        return new DualWriterOperationRecorder(folder1, folder2);
    }
}
