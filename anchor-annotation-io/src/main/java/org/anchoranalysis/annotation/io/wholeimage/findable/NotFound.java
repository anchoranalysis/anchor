package org.anchoranalysis.annotation.io.wholeimage.findable;

import java.nio.file.Path;

import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * A negative-result when an object is NOT found at a particular location
 * 
 * @author owen
 *
 * @param <T>
 */
public class NotFound<T> extends Findable<T> {

	private Path path;
	private String reason;

	/**
	 * Constructor
	 * 
	 * @param path the path an object was not found at.
	 */
	public NotFound(Path path, String reason) {
		super();
		this.path = path;
		this.reason = reason;
	}

	public Path getPath() {
		return path;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public boolean logIfFailure(String name, LogErrorReporter logErrorReporter) {

		logErrorReporter.getLogReporter().logFormatted(
			"Cannot find %s: %s at %s",
			name,
			reason,
			path
		);
		
		return false;
	}

	@Override
	public T getOrNull() {
		return null;
	}
	
	
}
