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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.xml.error.BeanCombinableException;
import org.anchoranalysis.core.file.FileMatcher;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.params.InputContextParams;

@SuppressWarnings("unused")
public class FileSet extends FileProviderWithDirectory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1232904558425899995L;
	
	// START BEAN PROPERTIES
	@BeanField
	private String fileFilter="*";
	
	@BeanField
	private boolean recursive = false;
	
	@BeanField
	private boolean regEx = false;
	
	/** A directory in which to look for files.
	 * 
	 *  If empty, first the bean will try to use any input-dir set in the input context if it exists, or otherwise use the current working-directory
	 * */
	@BeanField @AllowEmpty
	private String directory = "";
	
	/** If true, the filter is applied to the path as a whole, not just the filename, and it is always considered a Regular-Expression
	 *    using UNIX directory separators
	 */
	@BeanField
	private boolean applyFilterToPath = false;
	
	/** If non-negative the max depth of directories */
	@BeanField 
	private int maxDirectoryDepth = 0;
	
	/** if TRUE, case is ignored in the pattern matching. Otherwise the system-default is used
	 *  i.e. Windows ingores case, Linux doesn't
	 */
	@BeanField
	private boolean ignoreHidden = true;
	// END BEAN PROPERTIES
	
	public FileSet() {
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
	
	// Matching files
	@Override
	public Collection<File> matchingFiles(ProgressReporter progressReporter, InputContextParams inputContext) throws IOException {
		
		// If we don't have an absolute Directory(), we combine it with the localizedPath
		Path dir = makeAbsolutePathIfNecessary( getDirectoryAsPath(inputContext) );
		
		FileMatcher matcher = new FileMatcher( applyFilterToPath, ignoreHidden );
		if (maxDirectoryDepth>0) {
			matcher.setMaxDirDepth(maxDirectoryDepth);
		}
		return matcher.matchingFiles(dir, regEx, fileFilter, recursive, progressReporter);
	}
	
	public Path getDirectoryMaybeAbsolute( InputContextParams inputContext ) {
		return makeAbsolutePathIfNecessary( getDirectoryAsPath(inputContext) );
	}
	
	
	/**
	 * Sets both the fileFilter and the Directory from a combinedFileFilter string
	 * 
	 * This is a glob matching e.g.  somefilepath/*.tif  or somefilepath\*.tif
	 * 
	 * @param combinedFileFilter
	 */
	public void setFileFilterAndDirectory( Path combinedFileFilter ) {
		
		String pathStr = SingleFile.replaceBackslashes(combinedFileFilter.toString() );
		int finalSlash = pathStr.lastIndexOf("/");
		
		if (finalSlash==-1) {
			setDirectory("");
			setFileFilter(pathStr);
		} else {
			setDirectory( pathStr.substring(0, finalSlash+1 ));
			setFileFilter( pathStr.substring(finalSlash+1));
		}
	}
	
	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public String getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(String fileFilter) {
		this.fileFilter = fileFilter;
	}
	
	public String getDirectory() {
		return directory;
	}

	@Override
	public Path getDirectoryAsPath(InputContextParams inputContext) {
		
		// If no directory is explicitly specified, and the input-context provides an input-directory
		//  then we use this
		//
		// This is a convenient way for the ExperimentLauncher to parameterize the input-directory
		if (directory.isEmpty() && inputContext.hasInputDir()) {
			return makeAbsolutePathIfNecessary( inputContext.getInputDir() );
		}
		
		return Paths.get(directory).toAbsolutePath();
	}
			
	public void setDirectory(String directory) {
		this.directory = SingleFile.replaceBackslashes(directory);
	}
	
	@Override
	public void setDirectory(Path path) {
		this.directory = path.toString();
	}
	
	public boolean isApplyFilterToPath() {
		return applyFilterToPath;
	}


	public void setApplyFilterToPath(boolean applyFilterToPath) {
		this.applyFilterToPath = applyFilterToPath;
	}


	public boolean isRegEx() {
		return regEx;
	}


	public void setRegEx(boolean regEx) {
		this.regEx = regEx;
	}

	public int getMaxDirectoryDepth() {
		return maxDirectoryDepth;
	}

	public void setMaxDirectoryDepth(int maxDirectoryDepth) {
		this.maxDirectoryDepth = maxDirectoryDepth;
	}

	public boolean isIgnoreHidden() {
		return ignoreHidden;
	}

	public void setIgnoreHidden(boolean ignoreHidden) {
		this.ignoreHidden = ignoreHidden;
	}
}
