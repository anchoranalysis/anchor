package org.anchoranalysis.experiment.task;

/*
 * #%L
 * anchor-experiment
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

import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

class HelperBindOutputManager {

	// If pathForBinding is null, we bind to the root folder instead
	public static BoundOutputManager createOutputManagerForTask(
		InputFromManager input,
		ManifestRecorder manifestTask,
		ParametersExperiment params
	) throws JobExecutionException {
		try {
			if (input.pathForBinding()!=null) {
				BoundOutputManager boundOutput = params.getOutputManager().bindFile( 
					input,
					params.getExperimentIdentifier(),
					manifestTask,
					params.getExperimentalManifest(),
					params.getExperimentArguments().createParamsContext()
				);
				throwExceptionIfClashes( params.getExperimentalManifest(), boundOutput, input.pathForBinding() );
				return boundOutput;
			} else {
				// If pathForBinding is null, we reuse the existing root output-manager (but adding a recorder for manifestTask)
				manifestTask.init( params.getOutputManager().getOutputFolderPath() );
				params.getOutputManager().addOperationRecorder( manifestTask.getRootFolder() );
				return params.getOutputManager().getDelegate();
			}
		} catch (AnchorIOException e) {
			throw new JobExecutionException(
				String.format(
					"Cannot bind the outputManager to the specific task with pathForBinding=%s and experimentIdentifier='%s'%n%s",
					input.pathForBinding()!=null ? quoteString(input.pathForBinding().toString()) : "null",
					params.getExperimentIdentifier(),
					e.toString()
				),
				e
			);
		}
	}

	private static void throwExceptionIfClashes( ManifestRecorder manifestExperiment, BoundOutputManager boundOutput, Path pathForBinding ) throws JobExecutionException {
		// Now we do a check, to ensure that our experimentalManifest and manifest are going to write files
		//  to the same folder (without at least having some kind of prefix, to prevent files overwriting each other)
		Path experimentalRoot = manifestExperiment.getRootFolder().getRelativePath();
		if (wouldClashWithExperimentRoot(experimentalRoot,boundOutput.getBoundFilePathPrefix())) {
			throw new JobExecutionException(
				String.format(
					"There is a clash between the bound-prefixer the root experiment directory.%n   Path for binding: %s%n   Root experiment directory: %s%nThey are writing to the same directory, without any prefix, and outputs may collide.",
					pathForBinding,
					experimentalRoot.toString()
				)
			);
		}
	}
	
	private static String quoteString( String input ) {
		return String.format("'%s'", input);
	}
	
	
	/**
	 * Checks to see if this prefixer would clash with a particular experimentalDirectory by
	 * writing files to the same locations that an experiment might do
	 * 
	 * This is particular dangerous for conflicting messagelog.txt
	 * 
	 * To avoid this happening, the prefixer should be either:
	 * 1. writing out to a differerent directory
	 * 2. have a prefix, which is prepended to all files, and keeps them unique
	 * 
	 * @param experimentalDirectory the root directory associated with the experiment
	 * @return TRUE if it would clash, FALSE otherwise
	 * @throws IOException 
	 */
	private static boolean wouldClashWithExperimentRoot( Path experimentalDirectory, FilePathPrefix fpp ) {
		if (!fpp.getFilenamePrefix().isEmpty()) {
			// We're safe if there's a non-empty filename prefix
			return false;
		}
		
		Path pathPrefixDir = fpp.getFolderPath();

		// We're also safe if they are different directories
		return (pathPrefixDir.normalize().equals(experimentalDirectory.normalize()));
	}
}
