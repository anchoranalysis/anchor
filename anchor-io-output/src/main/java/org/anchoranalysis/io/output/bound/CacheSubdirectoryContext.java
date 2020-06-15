package org.anchoranalysis.io.output.bound;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Caches sub-directories as they are created, so as to reuse the BoundIOContext, without creating duplicate manifest entries.
 * 
 * @author Owen Feehan
 *
 */
public class CacheSubdirectoryContext {

	private Map<Optional<String>, BoundIOContext> mapOutputManagers = new HashMap<>();
	
	private BoundIOContext parentContext;
	
	/**
	 * Constructor
	 * 
	 * @param parentContext the context of the directory in which subdirectories may be created
	 */
	public CacheSubdirectoryContext(BoundIOContext parentContext) {
		super();
		this.parentContext = parentContext;
	}		
	
	/**
	 * Gets (from the cache if it's already there) subdirectory for a given-name
	 * 
	 * @param subdirectoryName the sub-directory name. if not set, then the parentContext is returned instead.
	 * @return
	 */
	public BoundIOContext get(Optional<String> subdirectoryName) {
		return mapOutputManagers.computeIfAbsent(
			subdirectoryName,
			key -> parentContext.maybeSubdirectory(key)
		);
	}
}