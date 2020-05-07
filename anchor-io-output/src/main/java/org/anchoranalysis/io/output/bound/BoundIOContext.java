package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.log.LogReporter;

/**
 * Certain parameters that are exposed after any file-system binding for inputs and outputs has occurred.
 * 
 * @author Owen Feehan
 *
 */
public interface BoundIOContext {
	
	Path getModelDirectory();
	
	BoundOutputManagerRouteErrors getOutputManager();
		
	boolean isDebugEnabled();
	
	LogErrorReporter getLogger();
	
	default ErrorReporter getErrorReporter() {
		return getLogger().getErrorReporter();
	}
	
	default LogReporter getLogReporter() {
		return getLogger().getLogReporter();
	}
}
