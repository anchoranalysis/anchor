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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Additional paramaters that provide context for many beans that provide input-functions
 * 
 * @author Owen Feehan
 *
 */
public class InputContextParams {

	/** Iff non-NULL, a directory which can be used by beans to find input */
	private Path inputDir = null;
	
	/** A glob that can be used by beans to filter input */
	private String inputFilterGlob = "*.*";
	
	/** A list of extensions that can be used filter inputs */
	private Set<String> inputFilterExtensions = defaultFilterExtensions();
	
	/** Whether an experiment is executing in debug mode or not */
	private boolean debugMode = false;
	
	/** Whether an experiment is executing in GUI mode or not */
	private boolean guiMode = false;
	
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

	public void setInputFilterGlob(String inputFilterGlob) {
		this.inputFilterGlob = inputFilterGlob;
	}
	
	private static void checkAbsolutePath(Path inputDir) throws IOException {
		if (!inputDir.isAbsolute()) {
			throw new IOException(
				String.format("An non-absolute path was passed to setInputDir() of %s", inputDir)
			);
		}
	}
	
	private Set<String> defaultFilterExtensions() {
		return new HashSet<>(Arrays.asList("jpg", "png", "tif", "tiff"));
	}

	public Set<String> getInputFilterExtensions() {
		return inputFilterExtensions;
	}

	public void setInputFilterExtensions(Set<String> inputFilterExtensions) {
		this.inputFilterExtensions = inputFilterExtensions;
	}
	
	
	public boolean isGuiMode() {
		return guiMode;
	}

	public void setGuiMode(boolean guiMode) {
		this.guiMode = guiMode;
	}
	

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public String getInputFilterGlob() {
		return inputFilterGlob;
	}
}
