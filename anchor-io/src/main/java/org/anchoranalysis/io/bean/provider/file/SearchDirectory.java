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
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.core.error.combinable.AnchorCombinableException;
import org.anchoranalysis.io.bean.file.matcher.FileMatcher;
import org.anchoranalysis.io.bean.file.matcher.MatchGlob;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.glob.GlobExtractor;
import org.anchoranalysis.io.glob.GlobExtractor.GlobWithDirectory;
import org.anchoranalysis.io.params.InputContextParams;

@SuppressWarnings("unused")
public class SearchDirectory extends FileProviderWithDirectoryString {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1232904558425899995L;
	
	// START BEAN PROPERTIES
	@BeanField
	private FileMatcher matcher;
	
	@BeanField
	private boolean recursive = false;
	
	/** If non-negative the max depth of directories. If -1, then there is no maximum depth. */
	@BeanField
	private int maxDirectoryDepth = -1;
	
	/** if TRUE, case is ignored in the pattern matching. Otherwise the system-default is used
	 *  i.e. Windows ignores case, Linux doesn't
	 */
	@BeanField
	private boolean ignoreHidden = true;
	
	/**
	 * if TRUE, continues when a directory-access-error occurs (logging it), otherwise throws an exception
	 */
	@BeanField
	private boolean acceptDirectoryErrors = false;
	// END BEAN PROPERTIES
	
	// Matching files
	@Override
	public Collection<File> matchingFilesForDirectory( Path directory, InputManagerParams params ) throws AnchorIOException {

		int maxDirDepth = maxDirectoryDepth>=0 ? maxDirectoryDepth : Integer.MAX_VALUE;	// maxDepth of directories searches
		return matcher.matchingFiles(directory, recursive, ignoreHidden, acceptDirectoryErrors, maxDirDepth, params);
	}
	
	
	/**
	 * Sets both the fileFilter and the Directory from a combinedFileFilter string
	 * 
	 * This is a glob matching e.g.  somefilepath/*.tif  or somefilepath\*.tif
	 * 
	 * @param combinedFileFilter
	 */
	public void setFileFilterAndDirectory( Path combinedFileFilter ) {
		
		GlobWithDirectory gwd = GlobExtractor.extract(combinedFileFilter.toString());
		
		
		MatchGlob matcherGlob = new MatchGlob();
		matcherGlob.setGlob( gwd.getGlob() );
		
		if (gwd.getDirectory()!=null) {
			setDirectory( gwd.getDirectory() );
		} else {
			setDirectory("");	
		}
		
		this.matcher = matcherGlob;
	}
	
	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
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

	public FileMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(FileMatcher matcher) {
		this.matcher = matcher;
	}

	public boolean isAcceptDirectoryErrors() {
		return acceptDirectoryErrors;
	}

	public void setAcceptDirectoryErrors(boolean acceptDirectoryErrors) {
		this.acceptDirectoryErrors = acceptDirectoryErrors;
	}
}
