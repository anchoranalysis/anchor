package org.anchoranalysis.core.file;

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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

/**
 * Manipulation of Path objects
 * 
 * @author Owen Feehan
 *
 */
public class PathUtilities {

	/**
	 * Converts a path to a string using Unix-style seperators (forward-slashes)
	 * @param path with either Windows-style (blackslashes) or Unix-style separators (forward slashes)
	 * @return identical path but with Unix-style separators
	 */
	public static String toStringUnixStyle( Path path ) {
		return FilenameUtils.separatorsToUnix(path.toString());
	}
	
	/**
	 * Converts a path to a string using Unix-style separators (forward-slashes)
	 * 
	 * @param path with either Windows-style (blackslashes) or Unix-style separators (forward slashes)
	 * @return identical path but with Unix-style separators
	 */
	public static String toStringUnixStyle( String path ) {
		return FilenameUtils.separatorsToUnix(path);
	}
	
	
	/**
	 * Determines the path to the current jar directory (or folder with class files) so we can resolve
	 *   a properties file
	 * 
	 * @param c the class which was used to launch the application (or another class with the same codeSource)
	 * @return a path (always a folder) to the current jar (or folder with class files)
	 */
	public static Path pathCurrentJAR( Class<?> c ) {
		URI pathURI;
		try {
			pathURI = c.getProtectionDomain().getCodeSource().getLocation().toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		Path path = Paths.get(pathURI);
		
		if (Files.isDirectory(path)) {
			// If it's a folder this is good enough, and we return it
			return path;
		} else {
			// If it's a file, then we assume this is path to the jar, and return its parent folder
			return path.getParent();
		}
	}
}
