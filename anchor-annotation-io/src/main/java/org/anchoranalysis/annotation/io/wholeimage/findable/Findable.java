package org.anchoranalysis.annotation.io.wholeimage.findable;

import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * An object that can be Found or Not-Found
 * @author owen
 *
 * @param <T> object-type
 */
public abstract class Findable<T> {

	/**
	 * Logs a message describing what went wrong if a file was not found
	 * 
	 * @param name
	 * @param logErrorReporter
	 * 
	 * @return true if sucessful, false if not-found
	 */
	public abstract boolean logIfFailure(
		String name,
		LogErrorReporter logErrorReporter		
	);
	
	
	/** Returns the object if found, otherwise null */
	public abstract T getOrNull();
}
