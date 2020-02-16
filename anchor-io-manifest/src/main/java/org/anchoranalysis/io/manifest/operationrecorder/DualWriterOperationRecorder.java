package org.anchoranalysis.io.manifest.operationrecorder;

import java.nio.file.Path;

import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;

/**
 * Allows two IWriteOperationRecorder function together as if they are one
 * 
 * <p>Every operation is applied to both.</p>
 * 
 * @author owen
 *
 */
public class DualWriterOperationRecorder implements IWriteOperationRecorder {

	private IWriteOperationRecorder recorder1;
	private IWriteOperationRecorder recorder2;
	
	public DualWriterOperationRecorder(IWriteOperationRecorder recorder1, IWriteOperationRecorder recorder2) {
		super();
		this.recorder1 = recorder1;
		this.recorder2 = recorder2;
	}

	@Override
	public void write(String outputName, ManifestDescription manifestDescription, Path outFilePath, String index) {
		recorder1.write(outputName, manifestDescription, outFilePath, index);
		recorder2.write(outputName, manifestDescription, outFilePath, index);
	}

	@Override
	public IWriteOperationRecorder writeFolder(Path relativeFolderPath, ManifestFolderDescription manifestDescription,
			FolderWriteWithPath folderWrite) {
		IWriteOperationRecorder folder1 = recorder1.writeFolder(relativeFolderPath, manifestDescription, folderWrite);
		IWriteOperationRecorder folder2 = recorder2.writeFolder(relativeFolderPath, manifestDescription, folderWrite);
		return new DualWriterOperationRecorder(folder1, folder2);
	}

}
