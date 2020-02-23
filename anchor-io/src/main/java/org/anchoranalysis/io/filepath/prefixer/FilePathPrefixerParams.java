package org.anchoranalysis.io.filepath.prefixer;

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

import java.io.IOException;
import java.nio.file.Path;

public class FilePathPrefixerParams {

	private boolean debugMode;
	
	/**
	 * A directory indicating where inputs can be located
	 */
	private Path outputDirectory;

	public FilePathPrefixerParams(boolean debugMode, Path outputDirectory) throws IOException {
		super();
		this.debugMode = debugMode;
		this.outputDirectory = outputDirectory;
		checkAbsolutePath(outputDirectory);
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public Path getOutputDirectory() {
		return outputDirectory;
	}
		
	private static void checkAbsolutePath(Path outputDirectory) throws IOException {
		if (outputDirectory!=null && !outputDirectory.isAbsolute()) {
			throw new IOException(
				String.format("An non-absolute path was passed to FilePathPrefixerParams of %s", outputDirectory)
			);
		}
	}
}
