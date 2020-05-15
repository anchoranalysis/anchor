package org.anchoranalysis.io.output.bean;

import java.nio.file.Path;
import java.util.Optional;

/*-
 * #%L
 * anchor-io-output
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.filepath.prefixer.FilePathPrefixer;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.prefixer.FilePathDifferenceFromFolderPath;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.ExperimentFileFolder;
import org.anchoranalysis.io.manifest.operationrecorder.NullWriteOperationRecorder;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.LazyDirectoryFactory;

public abstract class OutputManagerWithPrefixer extends OutputManager {

	// BEAN PROPERTIES
	@BeanField
	private FilePathPrefixer filePathPrefixer = null;
	
	/**  
	 * If true, if an existing output folder (at intended path) is deleted
	 * If false, an error is thrown if the folder already exists
	 */
	@BeanField
	private boolean delExistingFolder = true;

	@BeanField
	private OutputWriteSettings outputWriteSettings = new OutputWriteSettings();
	// END BEAN PROPERTIES
		
	// Binds the output to be connected to a particular file and experiment
	@Override
	public FilePathPrefix prefixForFile(
		InputFromManager input,
		String expIdentifier,
		Optional<ManifestRecorder> manifestRecorder,
		Optional<ManifestRecorder> experimentalManifestRecorder,
		FilePathPrefixerParams context
	) throws AnchorIOException {
		
		// Calculate a prefix from the incoming file, and create a file path generator
		FilePathPrefix fpp = filePathPrefixer.outFilePrefix( input, expIdentifier, context );
		
		FilePathDifferenceFromFolderPath fpd = new FilePathDifferenceFromFolderPath();
		fpd.init(
			this.filePathPrefixer.rootFolderPrefix(expIdentifier, context).getCombinedPrefix(),
			fpp.getCombinedPrefix()
		);
		
		experimentalManifestRecorder.ifPresent(
			mr -> writeManifestExperimentFolder(mr, fpd.getRemainderCombined())
		);

		manifestRecorder.ifPresent(
			mr -> mr.init( fpp.getFolderPath() )	
		);
		
		return fpp;
	}
	
	private static void writeManifestExperimentFolder( ManifestRecorder manifestRecorderExperiment, Path rootPath ) {
		manifestRecorderExperiment.getRootFolder().writeFolder(
			rootPath,
			new ManifestFolderDescription(), 
			new ExperimentFileFolder()
		);	
	}
	
	@Override
	public BoundOutputManager bindRootFolder( String expIdentifier, ManifestRecorder writeOperationRecorder, FilePathPrefixerParams context ) throws AnchorIOException {

		FilePathPrefix prefix = filePathPrefixer.rootFolderPrefix( expIdentifier, context );
		
		if (writeOperationRecorder!=null) {
			writeOperationRecorder.init(prefix.getFolderPath());
			return new BoundOutputManager( this, prefix, getOutputWriteSettings(), writeOperationRecorder.getRootFolder(), lazyDirectoryFactory(), null );
		} else {
			return new BoundOutputManager( this, prefix, getOutputWriteSettings(), new NullWriteOperationRecorder(), lazyDirectoryFactory(), null );
		}
	}
	
	private LazyDirectoryFactory lazyDirectoryFactory() {
		return new LazyDirectoryFactory(delExistingFolder);
	}

	
	// START BEAN getters and setters
	public FilePathPrefixer getFilePathPrefixer() {
		return filePathPrefixer;
	}


	public void setFilePathPrefixer(FilePathPrefixer filePathPrefixer) {
		this.filePathPrefixer = filePathPrefixer;
	}
	

	public boolean isDelExistingFolder() {
		return delExistingFolder;
	}


	public void setDelExistingFolder(boolean delExistingFolder) {
		this.delExistingFolder = delExistingFolder;
	}
	
	
	public OutputWriteSettings getOutputWriteSettings() {
		return outputWriteSettings;
	}

	public void setOutputWriteSettings(OutputWriteSettings outputWriteSettings) {
		this.outputWriteSettings = outputWriteSettings;
	}	
	// END BEAN getters and setters
}
