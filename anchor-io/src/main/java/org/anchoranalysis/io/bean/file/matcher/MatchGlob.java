package org.anchoranalysis.io.bean.file.matcher;

/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.bean.annotation.BeanField;

public class MatchGlob extends FileMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN FIELDS
	@BeanField
	private String glob;
	// END BEAN FIELDS
	
	public MatchGlob() {
		
	}
	
	public MatchGlob(String glob) {
		this.glob = glob;
	}

	@Override
	protected Predicate<Path> createMatcherFile(Path dir) {
		return PathMatcherUtilities.filter(dir, "glob", glob);
	}

	public String getGlob() {
		return glob;
	}

	public void setGlob(String glob) {
		this.glob = glob;
	}
}
