package org.anchoranalysis.io.bean.filepath.generator;

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


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;

// Generates an out string where $digit$ is replaced with the #digit group from a regex
public class FilePathGeneratorRegEx extends FilePathGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private String regEx = "";
	
	@BeanField
	private String outPath = "";
	// END BEAN PROPERTIES
	
	public static Matcher match(Path pathIn, String regEx) throws OperationFailedException {
		
		String pathInStr = Utilities.convertBackslashes(pathIn);
		
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher( pathInStr );
		
		if (!m.matches()) {
			throw new OperationFailedException( String.format("RegEx string '%s' does not match '%s'", regEx, pathInStr ));
		}
		
		return m;
	}
	
	@Override
	public Path outFilePath(Path pathIn, boolean debugMode) throws AnchorIOException {
		
		Matcher m;
		try {
			m = match(pathIn, regEx);
		} catch (OperationFailedException e) {
			throw new AnchorIOException("Cannot match against the regular expression", e);
		}
		
		String outStr = new String(outPath);
		
		// We loop through each possible group, and replace if its found, counting down, so that we don't mistake
		// 11 for 1 (for example)
		for( int g=m.groupCount(); g>0; g--) {
			outStr = outStr.replaceAll("\\$" + Integer.toString(g), m.group(g) );
		}

		return Paths.get( outStr );
	}

	public String getRegEx() {
		return regEx;
	}

	public void setRegEx(String regEx) {
		this.regEx = regEx;
	}

	public String getOutPath() {
		return outPath;
	}

	public void setOutPath(String outPath) {
		this.outPath = outPath;
	}
}
