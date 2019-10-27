package org.anchoranalysis.io.bean.filepath.prefixer;

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


import java.io.IOException;
import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.error.BeanStrangeException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;

public abstract class FilePathPrefixer extends AnchorBean<FilePathPrefixer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7572853192837916054L;
	
	/**
	 * Provides a prefix which can be prepended to all output files. The prefix should be an absolute path.
	 * 
	 * @param pathIn an input-path to match against
	 * @param experimentIdentifier an identifier for the experiment
	 * @param debugMode TODO
	 * @return a prefixer
	 * @throws IOException if something goes wrong
	 */
	public abstract FilePathPrefix outFilePrefix( Path pathIn, String experimentIdentifier, boolean debugMode ) throws IOException;
	
	/**
	 * Provides a prefix that becomes the root-folder. The prefix should be an absolute path.
	 * 
	 * @param experimentIdentifier an identifier for the experiment
	 * @param debugMode TODO
	 * @return a prefixer
	 * @throws IOException
	 */
	public abstract FilePathPrefix rootFolderPrefix( String experimentIdentifier, boolean debugMode ) throws IOException;
	
	/**
	 * Converts a relative-path to an absolute-path (relative to the file-path associated with this current bean)
	 * 
	 * If there is no file-path associated with the current bean, then we throw an error if it is a relative path,
	 *  or otherwise it remains unchanged
	 *  
	 * If the pathToResolve is already absolute, then we return it as-is
	 * 
	 * @param pathToResolve input-path that is relative
	 * @return the converted path (relative to the localizedPath of the current file)
	 * @throws IOException if it cannot be converted to a real-path
	 */
	protected Path resolvePath( Path pathToResolve ) {
		
		if (pathToResolve.isAbsolute()) {
			// It's okay we it's absolute path
			return pathToResolve;
		}
		
		// We have a relative path
		if (getLocalPath()==null) {
			throw new BeanStrangeException(
				String.format("Cannot resolve relative-path: %s as there is no localPath for this bean", pathToResolve )
			);
		}
		
		assert !pathToResolve.isAbsolute();
		assert getLocalPath().isAbsolute();
		Path parent = getLocalPath().getParent();
		
		return parent.resolve(pathToResolve).normalize();
	}
}
