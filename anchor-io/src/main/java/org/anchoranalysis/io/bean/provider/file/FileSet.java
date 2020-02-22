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
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.xml.error.BeanCombinableException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.bean.file.matcher.FileMatcher;
import org.anchoranalysis.io.bean.file.matcher.MatchGlob;
import org.anchoranalysis.io.params.InputContextParams;

@SuppressWarnings("unused")
public class FileSet extends FileProviderWithDirectory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1232904558425899995L;
	
	// START BEAN PROPERTIES
	@BeanField
	private FileMatcher matcher;
	
	@BeanField
	private boolean recursive = false;
	
	/** A directory in which to look for files.
	 * 
	 *  If empty, first the bean will try to use any input-dir set in the input context if it exists, or otherwise use the current working-directory
	 * */
	@BeanField @AllowEmpty
	private String directory = "";
	
	/** If non-negative the max depth of directories. If -1, then there is no maximum depth. */
	@BeanField
	private int maxDirectoryDepth = -1;
	
	/** if TRUE, case is ignored in the pattern matching. Otherwise the system-default is used
	 *  i.e. Windows ignores case, Linux doesn't
	 */
	@BeanField
	private boolean ignoreHidden = true;
	// END BEAN PROPERTIES
	
	public FileSet() {
	}
	
	// Matching files
	@Override
	public Collection<File> matchingFiles(ProgressReporter progressReporter, InputContextParams inputContext) throws IOException {
		
		// If we don't have an absolute Directory(), we combine it with the localizedPath
		Path dir = makeAbsolutePathIfNecessary( getDirectoryAsPath(inputContext) );

		int maxDirDepth = maxDirectoryDepth>=0 ? maxDirectoryDepth : Integer.MAX_VALUE;	// maxDepth of directories searches
		return matcher.matchingFiles(dir, recursive, ignoreHidden, maxDirDepth, inputContext, progressReporter);
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
		
		MatchGlob matcherGlob = new MatchGlob();
		if (finalSlash==-1) {
			setDirectory("");
			matcherGlob.setGlob(pathStr);
		} else {
			setDirectory( pathStr.substring(0, finalSlash+1 ));
			matcherGlob.setGlob( pathStr.substring(finalSlash+1));
		}
		this.matcher = matcherGlob;
	}
	
	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
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

	public FileMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(FileMatcher matcher) {
		this.matcher = matcher;
	}
}
