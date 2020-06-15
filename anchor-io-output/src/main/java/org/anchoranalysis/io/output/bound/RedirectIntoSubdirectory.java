package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;

import org.anchoranalysis.core.log.LogErrorReporter;


/**
 * Like an existing bound-context but redirects all output into a sub-folder
 * 
 * @author Owen Feehan
 *
 */
class RedirectIntoSubdirectory implements BoundIOContext {

	private BoundIOContext delegate;
	private BoundOutputManagerRouteErrors replacementOutputManager;
	
	public RedirectIntoSubdirectory(BoundIOContext delegate, String folderPath) {
		super();
		this.delegate = delegate;
		this.replacementOutputManager = delegate.getOutputManager().resolveFolder(folderPath);
	}

	@Override
	public BoundOutputManagerRouteErrors getOutputManager() {
		return replacementOutputManager;
	}

	@Override
	public Path getModelDirectory() {
		return delegate.getModelDirectory();
	}
	
	@Override
	public boolean isDebugEnabled() {
		return delegate.isDebugEnabled();
	}

	@Override
	public LogErrorReporter getLogger() {
		return delegate.getLogger();
	}		
	
}