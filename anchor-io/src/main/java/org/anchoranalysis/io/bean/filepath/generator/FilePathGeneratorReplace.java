package org.anchoranalysis.io.bean.filepath.generator;

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
import java.nio.file.Paths;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.error.AnchorIOException;

/**
 * Generates a file-path by replacing all occurrences of a string-pattern with another
 * 
 * Paths always use forward-slashes regardless of operating system.
 *
 */
public class FilePathGeneratorReplace extends FilePathGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN FIELDS
	/** Regular expression to match against string */
	@BeanField
	private String regex;
	
	/** What to replace in the path */
	@BeanField
	private String replacement;
	// END BEAN FIELDS
	
	@Override
	public Path outFilePath(Path pathIn, boolean debugMode) throws AnchorIOException {
	
		String outStr = Utilities.convertBackslashes(pathIn);
		
		String replaced = outStr.replaceAll(regex, replacement);
		
		return Paths.get( replaced );
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
}
