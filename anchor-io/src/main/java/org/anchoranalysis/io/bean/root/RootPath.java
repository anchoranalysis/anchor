package org.anchoranalysis.io.bean.root;

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
import java.nio.file.Paths;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.filepath.prefixer.FilePathDifferenceFromFolderPath;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Defines a *root path* i.e. a directory in which files are read/written during analysis
 * 
 * Analysis scripts may select different root-paths depending on how they are executed (during debugging, locally/server) 
 *
 * The name of a root must not be unique, but the combination of all fields should be unique i.e. several roots can
 *  have the same name, but should vary in their other settings
 * 
 * @author Owen Feehan
 *
 */
		
public class RootPath extends AnchorBean<RootPath> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START PROPERTIES
	
	@BeanField
	private String name;
	
	/*** A path on a filesystem to the directory, that defines the root */ 
	@BeanField
	private String path;
	
	/*** If TRUE this root is preferred, when executing a job in debugging mode */
	@BeanField
	private boolean debug = false;

	// END PROPERTIES
	
	/**
	 * It splits the *root* portion of the path from the remainder
	 * 
	 * @param path path to split
	 * @return the split-path
	 * @throws IOException if the path cannot be matched against the root
	 */
	public SplitPath split( Path path ) throws IOException {
		
		SplitPath out = new SplitPath();
		
		Path rootPath = asPath();
				
		// We get the difference of what is left, or else an exception is thrown if it cannot match
		FilePathDifferenceFromFolderPath diff = new FilePathDifferenceFromFolderPath();
		diff.init( rootPath, path);
		
		out.setRoot( rootPath );
		out.setPath( diff.getRemainderCombined() );
		return out;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Path asPath() {
		return Paths.get(path);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public boolean equals( Object obj ) {
		if (this == obj) {
			return true;
		}
	    if (!(obj instanceof RootPath)) {
	        return false;
	    }
	    RootPath objCast = (RootPath) obj;
	    
	    if (!name.equals(objCast.name)) {
	    	return false;
	    }
	    
	    if (!path.equals(objCast.path)) {
	    	return false;
	    }
	    
	    if (debug!=objCast.debug) {
	    	return false;
	    }

	    return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
	        .append(name)
	        .append(path)
	        .append(debug)
	        .toHashCode();
	}
	
	
}
