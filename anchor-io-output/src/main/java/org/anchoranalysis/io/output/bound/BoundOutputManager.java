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
import java.nio.file.Path;
import java.util.Optional;

import org.anchoranalysis.io.bean.filepath.prefixer.PathWithDescription;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.manifest.operationrecorder.DualWriterOperationRecorder;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.manifest.operationrecorder.NullWriteOperationRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.writer.AlwaysAllowed;
import org.anchoranalysis.io.output.writer.CheckIfAllowed;
import org.anchoranalysis.io.output.writer.Writer;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;


public class BoundOutputManager {

	private OutputManager outputManager = null;

	private FilePathPrefix boundFilePathPrefix = null;
	private OutputWriteSettings outputWriteSettings;
	private IWriteOperationRecorder writeOperationRecorder;
	private LazyDirectoryFactory lazyDirectoryFactory;
	
	private Writer writerAlwaysAllowed;
	private Writer writerCheckIfAllowed;
	
	private WriterExecuteBeforeEveryOperation initIfNeeded;
	
	/**
	 * Constructor
	 * 
	 * @param outputManager
	 * @param boundFilePathPrefix
	 * @param outputWriteSettings
	 * @param writeOperationRecorder
	 * @param delExistingFolder
	 * @param parentInit if non-NULL, parent initializer to call, before our own initializer is called
	 * @throws IOException
	 */
	public BoundOutputManager(
		OutputManager outputManager,
		FilePathPrefix boundFilePathPrefix,
		OutputWriteSettings outputWriteSettings,
		IWriteOperationRecorder writeOperationRecorder,
		LazyDirectoryFactory lazyDirectoryFactory,
		WriterExecuteBeforeEveryOperation parentInit
	) {
		
		this.boundFilePathPrefix = boundFilePathPrefix;
		this.outputManager = outputManager;
		this.outputWriteSettings = outputWriteSettings;
		this.writeOperationRecorder = writeOperationRecorder;
		this.lazyDirectoryFactory = lazyDirectoryFactory;
		assert(writeOperationRecorder!=null);
		
		initIfNeeded = lazyDirectoryFactory.createOrReuse(boundFilePathPrefix.getFolderPath(), parentInit);
		writerAlwaysAllowed = new AlwaysAllowed(this, initIfNeeded);
		writerCheckIfAllowed = new CheckIfAllowed(this, initIfNeeded, writerAlwaysAllowed);
	}
	
	/** Adds an additional operation recorder alongside any existing recorders */
	public void addOperationRecorder( IWriteOperationRecorder toAdd ) {
		this.writeOperationRecorder = new DualWriterOperationRecorder( writeOperationRecorder, toAdd );
	}

	/** Creates a new outputManager by appending a relative folder-path to the current boundoutputmanager */
	public BoundOutputManager resolveFolder( String folderPath, FolderWrite folderWrite ) throws OutputWriteFailedException {
		
		Path folderPathNew = boundFilePathPrefix.getFolderPath().resolve(folderPath);
		
		FilePathPrefix fppNew = new FilePathPrefix( folderPathNew );
		fppNew.setFilenamePrefix( boundFilePathPrefix.getFilenamePrefix() );
		
		return new BoundOutputManager(outputManager,fppNew,outputWriteSettings, folderWrite, lazyDirectoryFactory, initIfNeeded);
	}
	
	/** Derives a BoundOutputManager from a file that is somehow relative to the root directory */
	public BoundOutputManager bindFile( PathWithDescription input, String expIdentifier, Optional<ManifestRecorder> manifestRecorder, Optional<ManifestRecorder> experimentalManifestRecorder, FilePathPrefixerParams context ) throws BindFailedException {
		try {
			FilePathPrefix fpp = outputManager.prefixForFile(
				input,
				expIdentifier,
				manifestRecorder,
				experimentalManifestRecorder,
				context
			);
			return new BoundOutputManager(
				outputManager,
				fpp,
				outputWriteSettings,
				writeRecorder(manifestRecorder),
				lazyDirectoryFactory,
				initIfNeeded
			);
		} catch (FilePathPrefixerException e) {
			throw new BindFailedException(e);
		}
	}
	
	/**
	 * Writes a file-entry to the operation recorder
	 * 
	 * @param outputName output-name
	 * @param path path of the file
	 * @param manifestDescription the manifest description
	 * @param index an index associated with this item (there may be other items with the same name, but not the same index)
	 */
	public void writeFileToOperationRecorder(String outputName, Path path, ManifestDescription manifestDescription, String index) {
		writeOperationRecorder.write(
			outputName,
			manifestDescription,
			boundFilePathPrefix.relativePath(path),
			index
		);
	}
	
	/**
	 * Writes a folder-entry to the operation-recorder (if it exists)
	 * 
	 * @param path path of the folder that is to be written
	 * @param manifestDescription the manifest-description
	 * @param manifestFolder the associated folder in the manifest
	 * @return a write recorder for the sub folder (if it exists) or otherwise the write recorder associated with the output manager
	 */
	public IWriteOperationRecorder writeFolderToOperationRecorder( Path path, ManifestFolderDescription manifestDescription, Optional<FolderWriteWithPath> manifestFolder ) {
		if (manifestFolder.isPresent()) {
			// Assume the folder are writing to has no path
			assert(manifestFolder.get().calcPath()==null);
			Path relativePath = boundFilePathPrefix.relativePath(path);
			
			return writeOperationRecorder.writeFolder( relativePath, manifestDescription, manifestFolder.get());
		} else {
			return writeOperationRecorder;
		}
	}
	
	private static IWriteOperationRecorder writeRecorder( Optional<ManifestRecorder> manifestRecorder ) {
		Optional<IWriteOperationRecorder> opt = manifestRecorder
			.map( mr->mr.getRootFolder() );
		return opt.orElse(
			new NullWriteOperationRecorder()
		);
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
	
	public Path outFilePath(String filePathRelative) {
		return boundFilePathPrefix.outFilePath(filePathRelative);
	}
	
	public OutputManager getOutputManager() {
		return outputManager;
	}

	public FilePathPrefix getBoundFilePathPrefix() {
		return boundFilePathPrefix;
	}

	public LazyDirectoryFactory getLazyDirectoryFactory() {
		return lazyDirectoryFactory;
	}

	
}
