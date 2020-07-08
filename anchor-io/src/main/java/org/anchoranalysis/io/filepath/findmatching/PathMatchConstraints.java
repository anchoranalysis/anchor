package org.anchoranalysis.io.filepath.findmatching;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import java.util.function.Predicate;

import com.google.common.base.Preconditions;

/**
 * Some constraints on which paths to match
 *
 */
public class PathMatchConstraints {

	/** Only accepts files where the predicate returns TRUE */
	private Predicate<Path> matcherFile;
	
	/** Only accepts any containing directories where the predicate returns TRUE */
	private Predicate<Path> matcherDir;
	
	/** Limits on the depth of how many sub-directories are recursed */
	private int maxDirDepth;
	
	public PathMatchConstraints(Predicate<Path> matcherFile, Predicate<Path> matcherDir, int maxDirDepth) {
		Preconditions.checkArgument(maxDirDepth>= 0);
		this.matcherFile = matcherFile;
		this.matcherDir = matcherDir;
		this.maxDirDepth = maxDirDepth;
	}
	
	public PathMatchConstraints replaceMaxDirDepth( int replacementMaxDirDepth ) {
		return new PathMatchConstraints(matcherFile, matcherDir, replacementMaxDirDepth);
	}

	public Predicate<Path> getMatcherFile() {
		return matcherFile;
	}

	public Predicate<Path> getMatcherDir() {
		return matcherDir;
	}

	public int getMaxDirDepth() {
		return maxDirDepth;
	}
}
