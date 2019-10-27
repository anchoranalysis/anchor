package org.anchoranalysis.io.output.bound;

/*
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.bean.output.allowed.OutputAllowed;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.writer.AlwaysAllowed;
import org.anchoranalysis.io.output.writer.CheckIfAllowed;
import org.anchoranalysis.io.output.writer.Writer;


public class BoundOutputManager {

	private OutputManager outputManager = null;

	private FilePathPrefix boundFilePathPrefix = null;
	private OutputWriteSettings outputWriteSettings;
	private IWriteOperationRecorder writeOperationRecorder;
	
	private Writer writerAlwaysAllowed = new AlwaysAllowed(this);
	private Writer writerCheckIfAllowed = new CheckIfAllowed(this, writerAlwaysAllowed);
	
	// Constructor
	public BoundOutputManager( OutputManager outputManager, FilePathPrefix boundFilePathPrefix, OutputWriteSettings outputWriteSettings, IWriteOperationRecorder writeOperationRecorder ) throws IOException {
		
		this.boundFilePathPrefix = boundFilePathPrefix;
		this.outputManager = outputManager;
		this.outputWriteSettings = outputWriteSettings;
		this.writeOperationRecorder = writeOperationRecorder;
		assert(writeOperationRecorder!=null);
		
		// Make sure any supporting directories are present
		// Removed on 21.03.2019 to prevent ghost directories
		//
		// The Assumption is that FilePathPrefix.outFilePath will always be called, before
		//   any new file is created. It in turn calls PathUtilities.createNecessaryDirs()
		//
		// If no problems are shown over time, then we can fully remove this comment
		//
		//Files.createDirectories(boundFilePathPrefix.getFolderPath());
	}

	public boolean isOutputAllowed(String outputName) {
		return outputManager.isOutputAllowed(outputName);	
	}
	
	public OutputAllowed outputAllowedSecondLevel(String key) {
		return outputManager.outputAllowedSecondLevel(key);
	}

	public OutputWriteSettings getOutputWriteSettings() {
		return outputWriteSettings;
	}

	
	public Path getOutputFolderPath() {
		return boundFilePathPrefix.getFolderPath();
	}



	public Writer getWriterAlwaysAllowed() {
		return writerAlwaysAllowed;
	}

	public Writer getWriterCheckIfAllowed() {
		return writerCheckIfAllowed;
	}

	public IWriteOperationRecorder getWriteOperationRecorder() {
		return writeOperationRecorder;
	}

	public OutputManager getOutputManager() {
		return outputManager;
	}

	public FilePathPrefix getBoundFilePathPrefix() {
		return boundFilePathPrefix;
	}

	// Creates a new outputManager by appending a relative folder-path to the current boundoutputmanager
	public BoundOutputManager resolveFolder( String folderPath, FolderWrite folderWrite ) throws IOException {
		
		Path folderPathNew = boundFilePathPrefix.getFolderPath().resolve(folderPath);
		
		Files.createDirectories(folderPathNew);
		
		FilePathPrefix fppNew = new FilePathPrefix( folderPathNew );
		fppNew.setFilenamePrefix( boundFilePathPrefix.getFilenamePrefix() );
		
		return new BoundOutputManager(outputManager,fppNew,outputWriteSettings, folderWrite);
	}
}
