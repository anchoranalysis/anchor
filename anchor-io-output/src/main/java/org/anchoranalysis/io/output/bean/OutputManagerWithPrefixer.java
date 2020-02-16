package org.anchoranalysis.io.output.bean;

/*-
 * #%L
 * anchor-io-output
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.filepath.prefixer.FilePathPrefixer;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.filepath.prefixer.FilePathDifferenceFromFolderPath;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.ExperimentFileFolder;
import org.anchoranalysis.io.manifest.operationrecorder.NullWriteOperationRecorder;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.apache.commons.io.FileUtils;

public abstract class OutputManagerWithPrefixer extends OutputManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	public BoundOutputManager bindFile( Path infilePath, String expIdentifier, ManifestRecorder manifestRecorder, ManifestRecorder experimentalManifestRecorder, boolean debugMode ) throws IOException {
		
		// Calculate a prefix from the incoming file, and create a file path generator
		FilePathPrefix fpp = filePathPrefixer.outFilePrefix( infilePath, expIdentifier, debugMode );

		FilePathDifferenceFromFolderPath fpd = new FilePathDifferenceFromFolderPath();
		fpd.init(
			this.filePathPrefixer.rootFolderPrefix(expIdentifier, debugMode).getCombinedPrefix(),
			fpp.getCombinedPrefix()
		);
		
		//fpr.setInPathPrefix( fpp.g)
		
		experimentalManifestRecorder.getRootFolder().writeFolder( fpd.getRemainderCombined(), new ManifestFolderDescription(), 
				new ExperimentFileFolder() );
		
		manifestRecorder.init( fpp.getFolderPath() );
		
		return new BoundOutputManager( this, fpp, getOutputWriteSettings(), manifestRecorder.getRootFolder() );
	}
	
	public BoundOutputManager bindRootFolder( String expIdentifier, ManifestRecorder writeOperationRecorder, boolean debugMode ) throws IOException {

		FilePathPrefix prefix = filePathPrefixer.rootFolderPrefix( expIdentifier, debugMode );
		
		//System.out.printf("filePathPrefixer.getFolderPath()=%s\n", prefix.getFolderPath() );
		
		if (writeOperationRecorder!=null) {
			writeOperationRecorder.init(prefix.getFolderPath());
			return new BoundOutputManager( this, prefix, getOutputWriteSettings(), writeOperationRecorder.getRootFolder() );
		} else {
			return new BoundOutputManager( this, prefix, getOutputWriteSettings(), new NullWriteOperationRecorder() );
		}
	}

	@Override
	public void deleteExstExpQuietly( String expIdentifier, boolean debugMode ) throws IOException {

		Path expPath = filePathPrefixer.rootFolderPrefix(expIdentifier, debugMode).getFolderPath();
		
		if (expPath.toFile().exists()) {
			if (delExistingFolder) {
				FileUtils.deleteQuietly( expPath.toFile() );
			} else {
				String line1 = "Experiment output folder already exists.";
				String line3 = "Consider enabling delExistingFolder=\"true\" in experiment.xml";
				// Check if it exists already, and refuse to overwrite
				throw new IOException(
					String.format("%s%nBefore proceeding, please delete: %s%n%s", line1, expPath, line3)
				);
			}
		}
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
