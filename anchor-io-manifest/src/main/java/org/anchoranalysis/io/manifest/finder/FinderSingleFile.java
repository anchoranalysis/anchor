package org.anchoranalysis.io.manifest.finder;

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
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;

public abstract class FinderSingleFile extends Finder {

	private FileWrite foundFile;
	
	private ErrorReporter errorReporter;
	
	// A simple method to override in each finder that is based upon finding a single file
	protected abstract FileWrite findFile( ManifestRecorder manifestRecorder ) throws MultipleFilesException;
	
	public FinderSingleFile( ErrorReporter errorReporter ) {
		this.errorReporter = errorReporter;
	}
	
	@Override
	public final boolean doFind( ManifestRecorder manifestRecorder ) {
		
		if (manifestRecorder==null) {
			return false;
		}
		
		try {
			foundFile = findFile(manifestRecorder);
			
			if (!exists()) {
				return false;
			}
			
			return true;
		} catch (MultipleFilesException e) {
			if (errorReporter!=null) {
				errorReporter.recordError(FinderSingleFile.class, e);
			}
			return false;
		}
	}
	
	@Override
	public final boolean exists() {
		return foundFile != null;
	}

	protected FileWrite getFoundFile() {
		return foundFile;
	}
}
