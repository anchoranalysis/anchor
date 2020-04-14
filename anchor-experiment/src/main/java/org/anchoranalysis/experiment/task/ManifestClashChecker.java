package org.anchoranalysis.experiment.task;

import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

class ManifestClashChecker {

	public static void throwExceptionIfClashes(
		ManifestRecorder manifestExperiment,
		BoundOutputManager boundOutput,
		Path pathForBinding
	) throws JobExecutionException {
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
