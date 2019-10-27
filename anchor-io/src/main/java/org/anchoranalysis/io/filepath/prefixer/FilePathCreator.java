package org.anchoranalysis.io.filepath.prefixer;

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

/**
 * Generates file-paths
 * 
 * @author Owen Feehan
 *
 */
public interface FilePathCreator {
	
	/**
	 * Generates a full path, given the final part of a filePath
	 * 
	 * The prefix is added to final-part to generate a full path.
	 * 
	 * All sub-directories are created if needed to ensure it's possible to write to the fullPath.
	 * 
	 * @param filePathRelative the final part of the filePath
	 * @return a resolved path containing the folderPath of FilePathPrefix, the filenamePrefix of FilePathPrefix and filePathRelative
	 */
	Path outFilePath( String filePathRelative );	
	
	/**
	 * Extracts a relative-file path (to the folderPath of the FilePathPrefix) from an absolute path
	 * 
	 * This relative-path includes any filenamePrefix added by the FilePathPrefix
	 * 
	 * @param fullPath
	 * @return the relative-path 
	 */
	Path relativePath( Path fullPath );
	
}
