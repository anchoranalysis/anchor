package org.anchoranalysis.io.manifest.operationrecorder;

/*-
 * #%L
 * anchor-io-manifest
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.nio.file.Path;

import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;

/**
 * Allows two IWriteOperationRecorder function together as if they are one
 * 
 * <p>Every operation is applied to both.</p>
 * 
 * @author Owen Feehan
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
