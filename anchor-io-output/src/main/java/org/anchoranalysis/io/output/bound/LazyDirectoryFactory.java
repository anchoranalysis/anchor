package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;

/**
 * Memoizes LazyDirectoryInit creation for particular outputDirectory (normalized)
 * 
 *  <p>The {@code parent} parameter is always assumed to be uniform for a given outputDirectory
 *  
 * @author owen
 *
 */
public class LazyDirectoryFactory {

	private boolean delExistingFolder;
		
	public LazyDirectoryFactory(boolean delExistingFolder) {
		super();
		this.delExistingFolder = delExistingFolder;
	}

	// Cache all directories created by Path
	private Map<Path, WriterExecuteBeforeEveryOperation> map = new HashMap<>();
	
	public synchronized WriterExecuteBeforeEveryOperation createOrReuse( Path outputDirectory, WriterExecuteBeforeEveryOperation parent ) {
		// So that we are always referring to a canonical output-directory path
		Path outputDirectoryNormalized = outputDirectory.normalize();
		return map.computeIfAbsent(
			outputDirectoryNormalized,
			path -> new LazyDirectoryInit(path, delExistingFolder, parent)
		);
	}
}
