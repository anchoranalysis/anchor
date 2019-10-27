package org.anchoranalysis.io.manifest;

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


import java.io.Serializable;
import java.nio.file.Path;

import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.folder.RootFolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ManifestRecorder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7253272905284863941L;
	
	// TODO we change this to be a RootFolder, but for now just to be backwards compatible, we allow all FolderWire and optionreplace in the constructor
	private RootFolder rootFolder;	// Paths relative to this

	private static Log log = LogFactory.getLog(ManifestRecorder.class);
	
	public ManifestRecorder() {
		super();
		rootFolder = new RootFolder();
	}	

	public void init( Path rootFolderPath ) {
		
		log.debug( String.format("init %s", rootFolderPath) ); 

		rootFolder.initRootPath( rootFolderPath );
		
		
		/*if (rootFolder!=null && rootFolder instanceof FolderWritePhysical) {
		
			for (FileWrite f : rootFolder.fileList()) {
				folder.add(f);
			}
			
			for (FolderWrite f : rootFolder.getFolderList()) {
				folder.add(f);
			}
		}*/
		
		//this.rootFolder = folder;
	
	}

	public FolderWrite getRootFolder() {
		return rootFolder;
	}

}
