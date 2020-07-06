package org.anchoranalysis.io.bean.provider.file;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.FileProviderException;
import org.anchoranalysis.io.params.InputContextParams;

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


public abstract class FileProviderWithDirectory extends FileProvider {
	
	public abstract Path getDirectoryAsPath(InputContextParams inputContext);
	
	@Override
	public final Collection<File> create(InputManagerParams params) throws FileProviderException {
		return matchingFilesForDirectory(
			getDirectoryAsPath(params.getInputContext()),
			params
		);
	}
	
	public abstract Collection<File> matchingFilesForDirectory(
		Path directory,
		InputManagerParams params
	) throws FileProviderException;
	

	/** Like getDirectory as Path but converts any relative path to absolute one */
	public Path getDirectoryAsPathEnsureAbsolute(InputContextParams inputContext) {
		Path path = getDirectoryAsPath(inputContext);
		return makeAbsolutePathIfNecessary(path);
	}
	
	/**
	 * If path is absolute, it's returned as-is
	 * If path is relative, and the 'makeAbsolute' option is activated, it's added to the localizedPath 
	 * 
	 * @param path
	 * @return
	 */
	private Path makeAbsolutePathIfNecessary( Path path ) {
		if (path.isAbsolute()) {
			return path;
		} else {
			Path parent = getLocalPath().getParent();
			return parent.resolve(path);
		}
	}
}
