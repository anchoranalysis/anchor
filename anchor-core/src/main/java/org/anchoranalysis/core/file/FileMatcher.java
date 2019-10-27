package org.anchoranalysis.core.file;

/*
 * #%L
 * anchor-core
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.anchoranalysis.core.file.findmatching.FindMatchingFiles;
import org.anchoranalysis.core.file.findmatching.FindMatchingFilesWithProgressReporter;
import org.anchoranalysis.core.file.findmatching.FindMatchingFilesWithoutProgressReporter;
import org.anchoranalysis.core.file.findmatching.PathMatchConstraints;
import org.anchoranalysis.core.progress.ProgressReporter;

/**
 * Matches filepaths against patterns
 * 
 * @author Owen
 *
 */
public class FileMatcher {

	private boolean applyFilterToPath = false;
	private int maxDirDepth = Integer.MAX_VALUE;	// maxDepth of directories searches
	private HiddenPathChecker hiddenPathChecker;
	
	public FileMatcher() {
		this(false,true);
	}
	
	public FileMatcher(boolean applyFilterToPath, boolean ignoreHidden) {
		super();
		this.applyFilterToPath = applyFilterToPath;
		this.hiddenPathChecker = new HiddenPathChecker(ignoreHidden);
	}


	// Matching files
	public Collection<File> matchingFiles(Path dir, boolean regEx, String fileFilter, boolean recursive, ProgressReporter progressReporter) throws IOException {
		
		if (!Files.exists(dir) || !Files.isDirectory(dir)) {
			throw new FileNotFoundException( String.format("Directory '%s' does not exist", dir) );
		}
		
		return createMatchingFiles(progressReporter, recursive).apply(
			dir,
			new PathMatchConstraints(
				createMatcherFile(regEx, dir, fileFilter),
				createMatcherDir(),
				maxDirDepth
			)
		);
	}
	
	private FindMatchingFiles createMatchingFiles( ProgressReporter progressReporter, boolean recursive ) {
		if (progressReporter==null) {
			return new FindMatchingFilesWithoutProgressReporter();
		} else {
			return new FindMatchingFilesWithProgressReporter(recursive, progressReporter);
		}
	}
	
	private Predicate<Path> createMatcherDir() {
		return path -> hiddenPathChecker.includePath(path);
	}
	
	private Predicate<Path> createMatcherFile( boolean regEx, Path dir, String fileFilter ) {
		
		if (applyFilterToPath) {
			Pattern pattern = Pattern.compile(fileFilter);
			return hiddenPathChecker.addHiddenCheck(
				p -> acceptPathViaRegEx(p, pattern)
			);
		} else {
			String filterType = regEx ? "regex" : "glob";
			PathMatcher matcher = dir.getFileSystem().getPathMatcher(filterType + ":" + fileFilter);	
			return hiddenPathChecker.addHiddenCheck(
				p->acceptPathViaMatcher(p, matcher)
			);
		}
	}
		
	private static boolean acceptPathViaRegEx(Path path, Pattern pattern) {
		return pattern.matcher(
			pathAsString(path)
		).matches();
	}
	
	private static boolean acceptPathViaMatcher(Path path, PathMatcher matcher) {
		return matcher.matches( path.getFileName() );
	}
	
	private static String pathAsString(Path p) {
		return PathUtilities.toStringUnixStyle(
			p.toFile().toString()
		);
	}

	public int getMaxDirDepth() {
		return maxDirDepth;
	}

	public void setMaxDirDepth(int maxDirDepth) {
		this.maxDirDepth = maxDirDepth;
	}
}
