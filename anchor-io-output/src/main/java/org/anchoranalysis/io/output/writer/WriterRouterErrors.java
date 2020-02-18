package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;

import org.anchoranalysis.core.cache.Operation;

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


import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.output.OutputWriteFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;

public class WriterRouterErrors {

	private Writer delegate;
	private ErrorReporter errorReporter;
	
	public WriterRouterErrors(Writer delegate, ErrorReporter errorReporter) {
		super();
		this.delegate = delegate;
		this.errorReporter = errorReporter;
	}

	public BoundOutputManagerRouteErrors bindAsSubFolder(String outputName,
			ManifestFolderDescription manifestDescription,
			FolderWriteWithPath folder) {
		try {
			return new BoundOutputManagerRouteErrors(
				delegate.bindAsSubFolder(outputName, manifestDescription, folder),
				errorReporter
			);
		} catch (OutputWriteFailedException e) {
			errorReporter.recordError( BoundOutputManagerRouteErrors.class, e);
			return null;
		}			
	}

	public <T> void writeSubfolder(String outputName,
			Operation<WritableItem> collectionGenerator)	{
		try {
			delegate.writeSubfolder(outputName, collectionGenerator);
		} catch (OutputWriteFailedException e) {
			errorReporter.recordError( BoundOutputManagerRouteErrors.class, e);
		}			
	}

	public int write(IndexableOutputNameStyle outputNameStyle,
			Operation<WritableItem> generator, String index) {
		try {
			return delegate.write(outputNameStyle, generator, index);
		} catch (OutputWriteFailedException e) {
			errorReporter.recordError( BoundOutputManagerRouteErrors.class, e);
			return -1;
		}				
	}

	public int write(IndexableOutputNameStyle outputNameStyle,
			Operation<WritableItem> generator, int index) {
		try {		
			return delegate.write(outputNameStyle, generator, index);
		} catch (OutputWriteFailedException e) {
			errorReporter.recordError( BoundOutputManagerRouteErrors.class, e);
			return -1;
		}				
	}

	public void write(OutputNameStyle outputNameStyle, Operation<WritableItem> generator) {
		try {
			delegate.write(outputNameStyle, generator);
		} catch (OutputWriteFailedException e) {
			errorReporter.recordError( BoundOutputManagerRouteErrors.class, e);
		}						
	}

	public void write(String outputName, Operation<WritableItem> generator) {
		try {
			delegate.write(outputName, generator);
		} catch (OutputWriteFailedException e) {
			errorReporter.recordError( BoundOutputManagerRouteErrors.class, e);
		}						
	}

	public Path writeGenerateFilename(String outputName, String extension,
			ManifestDescription manifestDescription, String outputNamePrefix,
			String outputNameSuffix, String index) throws OutputWriteFailedException {
		return delegate.writeGenerateFilename(outputName, extension,
				manifestDescription, outputNamePrefix, outputNameSuffix, index);
	}
	
}
