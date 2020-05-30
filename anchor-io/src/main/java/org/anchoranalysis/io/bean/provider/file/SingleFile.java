package org.anchoranalysis.io.bean.provider.file;

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


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.params.InputContextParams;

public class SingleFile extends FileProviderWithDirectory {

	// START BEAN PROPERTIES
	@BeanField
	private String path;
	// END BEAN PROPERTIES
	
	// Optionally changes the directory of the path
	private Path directory;

	public SingleFile() {
		
	}
	
	public SingleFile( String path ) {
		this.path = path;
	}
	
	@Override
	public Collection<File> matchingFilesForDirectory( Path directory, InputManagerParams params ) {
		
		File file = new File(path);
		
		if (hasDirectory()) {
			file = directory.resolve( file.getName() ).toFile();
		}

		return Collections.singletonList(file);
	}

	public String getPath() {
		return path;
	}


	public static String replaceBackslashes( String str ) {
		return str.replace('\\', '/');
	}
	
	public void setPath(String path) {
		// Make everything a forward slash
		this.path = replaceBackslashes(path);
	}
	
	private boolean hasDirectory() {
		return directory!=null;
	}


	@Override
	public Path getDirectoryAsPath(InputContextParams inputContext) {

		if (hasDirectory()) {
			return directory;
		} else {
			// We infer the directory if it isn't set
			return Paths.get(path).getParent();
		}
	}
}
