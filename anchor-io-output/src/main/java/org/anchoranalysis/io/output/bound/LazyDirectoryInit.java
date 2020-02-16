package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;

import org.anchoranalysis.io.output.OutputManagerAlreadyExists;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import org.apache.commons.io.FileUtils;

/** 
 * Creates the output-directory lazily on the first occasion exec() is called
 * 
 * <p>Depending on settings, the initialization routine involves:</p>
 * <ul>
 * <li>checks if a directory already exists at the path, and throws an errror</li> 
 * <li>deletes existing directory contents</li>
 * <li>creates the directory and any intermediate paths</li>
 * <li>first calls an initiation routine on parent initializer</li>
 * </ul>
 * 
 **/
public class LazyDirectoryInit implements WriterExecuteBeforeEveryOperation {

	private boolean needsInit = true;
	
	private Path outputDirectory;
	private boolean delExistingFolder;
	private WriterExecuteBeforeEveryOperation parent;
	
	/**
	 * Constructor
	 * 
	 * @param outputDirectory the output-directory to be init
	 * @param delExistingFolder
	 * @param if non-NULL a parent whose exec() is called before our exec() is called. if NULL, ignored.
	 */
	public LazyDirectoryInit(Path outputDirectory, boolean delExistingFolder, WriterExecuteBeforeEveryOperation parent) {
		super();
		this.outputDirectory = outputDirectory;
		this.delExistingFolder = delExistingFolder;
		this.parent = parent;
	}

	@Override
	public synchronized void exec() {
		if (needsInit) {
			
			if (parent!=null) {
				parent.exec();
			}
			
			if (outputDirectory.toFile().exists()) {
				if (delExistingFolder) {
					FileUtils.deleteQuietly( outputDirectory.toFile() );
				} else {
					String line1 = "Output directory already exists.";
					String line3 = "Consider enabling delExistingFolder=\"true\" in experiment.xml";
					// Check if it exists already, and refuse to overwrite
					throw new OutputManagerAlreadyExists(
						String.format("%s%nBefore proceeding, please delete: %s%n%s", line1, outputDirectory, line3)
					);
				}
			}
			
			// We create any subdirectories as needed
			outputDirectory.toFile().mkdirs();
			needsInit = false;
		}
	}
}