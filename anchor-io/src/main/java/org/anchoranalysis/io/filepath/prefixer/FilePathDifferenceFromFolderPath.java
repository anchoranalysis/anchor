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


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.FilePathToUnixStyleConverter;
import org.apache.commons.io.FilenameUtils;

/**
 * Calculates the difference between a path and a base
 * 
 * i.e. if   a base is c:\root\somePrefix_
 *       and a file is c:\root\somePrefix_someFile.xml
 *       
 *     then the difference is "_someFile.xml"
 *     
 *  The different is recorded seperately as folder and filename components
 * 
 * @author Owen Feehan
 *
 */
public class FilePathDifferenceFromFolderPath {
	
	private String filename;
	private Path folder;
	
	// TODO change from the two init methods to two factory constructors
	public FilePathDifferenceFromFolderPath() {
		// Nothing to do
	}

	/**
	 * Converts both paths to absolute paths and URIs and considers the difference
	 * 
	 * @param baseFolderPath path to a base folder
	 * @param filePath the path to resolve
	 * @throws IOException if the canonical file cannot be found
	 */
	public void init( Path baseFolderPath, Path filePath ) throws AnchorIOException {
		
		try {
			String base = baseFolderPath.toFile().getCanonicalFile().toURI().getPath();
		    String all = filePath.toFile().getCanonicalFile().toURI().getPath();
		    
		    // As we've converted to URIs the seperator is always a forward slash
		    calcDiff(base, all);
		} catch (IOException e) {
			throw new AnchorIOException("Cannot fully resolve paths");
		}
	}
	
	/**
	 * Doesn't do any conversion of paths, and considers the difference
	 * 
	 * @param baseFolderPath path to a base folder
	 * @param filePath the path to resolve
	 */
	public void initDirect( Path baseFolderPath, Path filePath ) {
		calcDiff(baseFolderPath.toString(), filePath.toString() );		
	}
	
	/**
	 * Performs the difference
	 * 
	 * Assumes base is a folder. Relies on this.
	 * 
	 * @param base the base-folder as a string
	 * @param all the entire path as a string
	 */
	private void calcDiff( String baseFolderPath, String entirePath ) {
		
		// Convert the base, and all to forward slashes only
		String base = FilePathToUnixStyleConverter.toStringUnixStyle(baseFolderPath);
		String all = FilePathToUnixStyleConverter.toStringUnixStyle(entirePath);
		
		// if base is non-empty, but doesn't end in a directory seperator we add one
		//  (we use a forward slash due to the previous step converting it into a URL style)
		if (!base.isEmpty() && !base.endsWith("/") ) {
			base = base.concat("/");
		}
		
		 // We cannot match the base against the entire string
	    if (!all.startsWith(base)) {
	    	throw new IllegalArgumentException( String.format("Cannot match base '%s' against '%s'", base, all) ); 
	    }
	    
	    // Remainder path
	    String remainder = all.substring( base.length() );
	    File remainderFile = new File(remainder);
	    
	    File parentFile = remainderFile.getParentFile();
	    if (parentFile!=null) {
	    	this.folder = remainderFile.getParentFile().toPath();
	    } else {
	    	this.folder = null;
	    }
	    this.filename = remainderFile.getName();
	}
	
	public Path getRemainderCombined() {
		if (folder!=null) {
			return getFolder().resolve( getFilename() );
		} else {
			return Paths.get( getFilename() );
		}
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public String getFilenameWithoutExtension() {
		return FilenameUtils.removeExtension( this.filename );
	}

	public Path getFolder() {
		return folder;
	}
}