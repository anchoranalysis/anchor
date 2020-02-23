package org.anchoranalysis.io.output.bound;

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
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.folder.FolderWritePhysical;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.writer.WriterRouterErrors;

public class BoundOutputManagerRouteErrors {

	private BoundOutputManager delegate;

	private ErrorReporter errorReporter;
	
	public BoundOutputManagerRouteErrors(BoundOutputManager delegate, ErrorReporter errorReporter) {
		super();
		assert( delegate != null );
		this.delegate = delegate;
		this.errorReporter = errorReporter;
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public OutputWriteSettings getOutputWriteSettings() {
		return delegate.getOutputWriteSettings();
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean isOutputAllowed(String outputName) {
		return delegate.isOutputAllowed(outputName);
	}
	
	public OutputAllowed outputAllowedSecondLevel(String key) {
		return delegate.outputAllowedSecondLevel(key);
	}	

	public String toString() {
		return delegate.toString();
	}

	public BoundOutputManager getDelegate() {
		return delegate;
	}
	
	public WriterRouterErrors getWriterAlwaysAllowed() {
		return new WriterRouterErrors( delegate.getWriterAlwaysAllowed(), errorReporter );
	}

	public WriterRouterErrors getWriterCheckIfAllowed() {
		return new WriterRouterErrors( delegate.getWriterCheckIfAllowed(), errorReporter );
	}

	public Path getOutputFolderPath() {
		return delegate.getOutputFolderPath();
	}
	
	public BoundOutputManagerRouteErrors resolveFolder(String folderPath) {
		return resolveFolder(folderPath, new FolderWritePhysical() );
	}
	
	private BoundOutputManagerRouteErrors resolveFolder(String folderPath, FolderWrite folderWrite) {
		try {
			return new BoundOutputManagerRouteErrors( delegate.resolveFolder(folderPath, folderWrite), errorReporter );
		} catch (OutputWriteFailedException e) {
			errorReporter.recordError(BoundOutputManagerRouteErrors.class, e);
			return null;
		}
	}

	public FilePathPrefix getBoundFilePathPrefix() {
		return delegate.getBoundFilePathPrefix();
	}

	public ErrorReporter getErrorReporter() {
		return errorReporter;
	}

	public void addOperationRecorder(IWriteOperationRecorder toAdd) {
		delegate.addOperationRecorder(toAdd);
	}

	public BoundOutputManager bindFile(InputFromManager input, String expIdentifier, ManifestRecorder manifestRecorder,
			ManifestRecorder experimentalManifestRecorder, FilePathPrefixerParams context) throws IOException {
		return delegate.bindFile(input, expIdentifier, manifestRecorder, experimentalManifestRecorder, context);
	}
}
