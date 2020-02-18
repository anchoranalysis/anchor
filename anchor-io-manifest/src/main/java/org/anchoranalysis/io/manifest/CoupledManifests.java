package org.anchoranalysis.io.manifest;

import java.io.IOException;

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


import java.nio.file.Path;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.bean.input.descriptivename.LastFolders;
import org.anchoranalysis.io.filepath.prefixer.FilePathDifferenceFromFolderPath;
import org.anchoranalysis.io.input.InputFromManager;

// A file manifest together with the overall manifest for the experiment
public class CoupledManifests implements InputFromManager {
	
	private ManifestRecorder experimentManifest;
	private ManifestRecorderFile fileManifest;
	private String name;
	
	public CoupledManifests(
			ManifestRecorder experimentManifest,
			ManifestRecorderFile fileManifest) throws IOException {
		super();
		this.experimentManifest = experimentManifest;
		this.fileManifest = fileManifest;
		assert(experimentManifest!=null);
		name = generateName();
	}
	
	public CoupledManifests(
			ManifestRecorder experimentManifest, ManifestRecorderFile fileManifest, int numFoldersInDescription ) {
		super();
		this.experimentManifest = experimentManifest;
		this.fileManifest = fileManifest;
		name = generateNameFromFolders(numFoldersInDescription);
	}

	public ManifestRecorder getExperimentManifest() {
		return experimentManifest;
	}

	public ManifestRecorderFile getFileManifest() {
		return fileManifest;
	}
	
	private String generateName() throws IOException {
			
		if (experimentManifest==null) {
			return generateNameFromFolders(0);
		}
		
		//String fileRootFolder = getFileManifest().getRootFolder().calcPath();
		Path experimentRootFolder = getExperimentManifest().getRootFolder().calcPath();
		
		FilePathDifferenceFromFolderPath ff = new FilePathDifferenceFromFolderPath();
		ff.init(experimentRootFolder,fileManifest.getRootPath());

		return ff.getRemainderCombined().toString();
	}
	
	private String generateNameFromFolders( int numFoldersInDescription ) {
		LastFolders dnff = new LastFolders();
		dnff.setNumFoldersInDescription(numFoldersInDescription);
		dnff.setRemoveExtensionInDescription(false);
		return dnff.descriptiveNameFor( fileManifest.getRootPath().toFile(), "<unknown>" ).getDescriptiveName();
	}

	@Override
	public String descriptiveName() {
		return name;
	}

	@Override
	public Path pathForBinding() {
		return fileManifest.getRootPath();
	}

	@Override
	public void close(ErrorReporter errorReporter) {

	}
}