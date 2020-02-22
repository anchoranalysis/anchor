package org.anchoranalysis.io.bean.file.matcher;

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
import java.util.Collection;
import java.util.function.Predicate;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.file.findmatching.FindMatchingFiles;
import org.anchoranalysis.core.file.findmatching.FindMatchingFilesWithProgressReporter;
import org.anchoranalysis.core.file.findmatching.FindMatchingFilesWithoutProgressReporter;
import org.anchoranalysis.core.file.findmatching.PathMatchConstraints;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Matches filepaths against patterns
 * 
 * @author Owen
 *
 */
public abstract class FileMatcher extends AnchorBean<FileMatcher> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Finds a collection of files that match particular conditions
	 * 
	 * @param dir root directory to search
	 * @param recursive whether to recursively search
	 * @param ignoreHidden whether to ignore hidden files/directories or not 
	 * @param maxDirDepth a maximum depth in directories to search
	 * @param inputContext parameters providing input-context
	 * @param progressReporter progress-reporter
	 * @return a collection of files matching the conditions
	 * @throws IOException if an error occurrs reading/writing or interacting with the filesystem
	 */
	public Collection<File> matchingFiles(Path dir, boolean recursive, boolean ignoreHidden, int maxDirDepth, InputContextParams inputContext, ProgressReporter progressReporter) throws IOException {
		
		if (!Files.exists(dir) || !Files.isDirectory(dir)) {
			throw new FileNotFoundException( String.format("Directory '%s' does not exist", dir) );
		}
		
		// Many checks are possible on a file, including whether it is hidden or not
		Predicate<Path> filePred = maybeAddIgnoreHidden( ignoreHidden, createMatcherFile(dir, inputContext) );
		
		// The only check on a directory is (maybe) whether it is hidden or not
		Predicate<Path> dirPred = maybeAddIgnoreHidden( ignoreHidden, p->true ); 
		
		return createMatchingFiles(progressReporter, recursive).apply(
			dir,
			new PathMatchConstraints(
				filePred,
				dirPred,
				maxDirDepth
			)
		);
	}
	
	private Predicate<Path> maybeAddIgnoreHidden( boolean ignoreHidden, Predicate<Path> pred ) {
		if (ignoreHidden) {
			return p -> pred.test(p) && HiddenPathChecker.includePath(p);
		} else {
			return pred;
		}
	}
	
	protected abstract Predicate<Path> createMatcherFile( Path dir, InputContextParams inputContext );
	
	private FindMatchingFiles createMatchingFiles( ProgressReporter progressReporter, boolean recursive ) {
		if (progressReporter==null) {
			return new FindMatchingFilesWithoutProgressReporter();
		} else {
			return new FindMatchingFilesWithProgressReporter(recursive, progressReporter);
		}
	}
}
