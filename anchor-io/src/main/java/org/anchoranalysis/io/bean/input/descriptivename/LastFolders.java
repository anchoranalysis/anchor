package org.anchoranalysis.io.bean.input.descriptivename;

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

import org.anchoranalysis.bean.annotation.BeanField;
import org.apache.commons.io.FilenameUtils;

public class LastFolders extends DescriptiveNameFromFile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private int numFoldersInDescription = 0;
	
	@BeanField
	private boolean removeExtensionInDescription = true;
	
	@BeanField
	private int skipFirstNumFolders = 0;
	
	@BeanField
	private boolean skipFileName = false;
	// END BEAN PROPERTIES
	
	public LastFolders() {
		
	}
	
	public LastFolders( int numFoldersInDescription ) {
		this.numFoldersInDescription = numFoldersInDescription;
	}
	
	@Override
	protected String createDescriptiveName( File file, int index ) {
		
		String nameOut = "";
		if (!skipFileName) {
		
			nameOut = new File( file.getPath() ).getName();
		
			if (removeExtensionInDescription) {
				nameOut = FilenameUtils.removeExtension( nameOut );
			}
		}
				
		File currentFile = file;
		for( int i=0; i<numFoldersInDescription; i++) {
			
			currentFile = currentFile.getParentFile();
			
			if (currentFile==null) {
				break;
			}
			
			if (i>=skipFirstNumFolders) {
				
				if (nameOut.isEmpty()) {
					nameOut = currentFile.getName();
				} else {
					nameOut = currentFile.getName() + "/" + nameOut;
				}
			}
		}
		return nameOut;		
	}

	public int getSkipFirstNumFolders() {
		return skipFirstNumFolders;
	}

	public void setSkipFirstNumFolders(int skipFirstNumFolders) {
		this.skipFirstNumFolders = skipFirstNumFolders;
	}

	public boolean isSkipFileName() {
		return skipFileName;
	}

	public void setSkipFileName(boolean skipFileName) {
		this.skipFileName = skipFileName;
	}
	
	public int getNumFoldersInDescription() {
		return numFoldersInDescription;
	}

	public void setNumFoldersInDescription(int numFoldersInDescription) {
		this.numFoldersInDescription = numFoldersInDescription;
	}

	public boolean isRemoveExtensionInDescription() {
		return removeExtensionInDescription;
	}

	public void setRemoveExtensionInDescription(boolean removeExtensionInDescription) {
		this.removeExtensionInDescription = removeExtensionInDescription;
	}
}
