package org.anchoranalysis.io.params;

/*-
 * #%L
 * anchor-io
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

import java.io.IOException;
import java.nio.file.Path;

/**
 * Additional paramaters that provide context for many beans that provide input-functions
 * 
 * @author Owen Feehan
 *
 */
public class InputContextParams {

	/** Iff non-NULL, a directory which can be used by beans to find input */
	private Path inputDir = null;

	/** Whether an experiment is executing in debug mode or not */
	private boolean debugMode = false;
	
	/** Whether an experiment is executing in GUI mode or not */
	private boolean guiMode = false;
	
	public boolean isGuiMode() {
		return guiMode;
	}

	public void setGuiMode(boolean guiMode) {
		this.guiMode = guiMode;
	}
	
	public boolean hasInputDir() {
		return inputDir!=null;
	}

	public Path getInputDir() {
		return inputDir;
	}

	// This should always be ab absolute path, never a relative one
	public void setInputDir(Path inputDir) throws IOException {
		
		if (inputDir!=null) {
			checkAbsolutePath(inputDir);
		}
		
		this.inputDir = inputDir;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
		
	private static void checkAbsolutePath(Path inputDir) throws IOException {
		if (!inputDir.isAbsolute()) {
			throw new IOException(
				String.format("An non-absolute path was passed to setInputDir() of %s", inputDir)
			);
		}
	}
}
