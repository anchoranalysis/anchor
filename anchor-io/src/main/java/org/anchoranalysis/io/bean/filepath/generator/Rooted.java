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
import java.util.logging.Logger;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.factory.BeanPathUtilities;
import org.anchoranalysis.io.bean.root.RootPathMap;
import org.anchoranalysis.io.bean.root.SplitPath;
import org.anchoranalysis.io.error.AnchorIOException;

public class Rooted extends FilePathGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private FilePathGenerator item;
	
	// The name of the RootPath to associate with this fileset
	@BeanField
	private String rootName;
	
	/**
	 * if TRUE, the root is not added to the outFilePath, and the path is instead localized against the location of the BeanXML. if FALSE, nothing is changed
	 */
	@BeanField
	private boolean suppressRootOut = false;
	
	/**
	 * if TRUE, the pathIn and pathOut are logged. Useful for debugging
	 */
	@BeanField
	private boolean logPath = false;
	// END BEAN PROPERTIES

	private Logger logger = Logger.getLogger(Rooted.class.getName());
	
	public void setSuppressRootOut(boolean suppressRootOut) {
		this.suppressRootOut = suppressRootOut;
	}

	@Override
	public Path outFilePath(Path pathIn, boolean debugMode) throws AnchorIOException {
		
		SplitPath pathInWithoutRoot = RootPathMap.instance().split(pathIn, rootName, debugMode);		
		
		Path pathOut = item.outFilePath(pathInWithoutRoot.getPath(), debugMode);
		
		if (suppressRootOut) {
			pathOut = BeanPathUtilities.combine( getLocalPath(), pathOut );
		} else {
			pathOut = pathInWithoutRoot.getRoot().resolve(pathOut);
		}
		
		if (logPath) {
			logger.info( String.format("pathIn=%s", pathIn) );
			logger.info( String.format("pathOut=%s", pathOut) );
		}
		
		return pathOut;
	}
	
	public FilePathGenerator getItem() {
		return item;
	}

	public void setItem(FilePathGenerator item) {
		this.item = item;
	}

	public boolean isLogPath() {
		return logPath;
	}

	public void setLogPath(boolean logPath) {
		this.logPath = logPath;
	}

	public boolean isSuppressRootOut() {
		return suppressRootOut;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

}
