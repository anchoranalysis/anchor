package org.anchoranalysis.io.manifest.folder;

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
import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.FileWriteIndex;
import org.anchoranalysis.io.manifest.match.Match;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// A folder contains a list of subfolders, contained files varies by implementation
public abstract class FolderWrite extends SequencedFolder implements IWriteOperationRecorder, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1342671218988826781L;

	private FolderWrite parentFolder = null;
	
	private ArrayList<FolderWrite> folders = new ArrayList<>();

	private static Log log = LogFactory.getLog(FolderWrite.class);

	private ManifestFolderDescription manifestFolderDescription;
	
	public FolderWrite() {
		log.debug( "New Folder Write: empty" );
	}
	
	// Parent folder
	public FolderWrite(FolderWrite parentFolder) {
		super();
		log.debug( "New Folder Write: " + parentFolder.getRelativePath() );
		this.parentFolder = parentFolder;
	}
	
	public ManifestFolderDescription getManifestFolderDescription() {
		return manifestFolderDescription;
	}

	public void setManifestFolderDescription(ManifestFolderDescription manifestFolderDescription) {
		this.manifestFolderDescription = manifestFolderDescription;
	}
	
	public abstract Path getRelativePath();
	
	public Path calcPath() {
		if (parentFolder!=null) {
			return parentFolder.calcPath().resolve( getRelativePath() );
		} else {
			return getRelativePath();
		}
	}
	
	@Override
	public void findFileFromIndex(List<FileWrite> foundList, String index,
			boolean recursive) {
		FileWriteIndex match = new FileWriteIndex( index );
		findFile(foundList, match, true);
	}
	

	// Finds a folder a comparator matches
	public abstract void findFile( List<FileWrite> foundList, Match<FileWrite> match, boolean recursive );

	
	
	// Finds a folder a comparator matches
	public synchronized void findFolder( List<FolderWrite> foundList, Match<FolderWrite> match ) {
		
		for (FolderWrite folder : folders) {
			
			if (match.matches(folder)) {
				foundList.add(folder);
			}
			
			if (folder==null) {
				continue;
			}
			//assert(folder!=null);
			
			folder.findFolder(foundList, match);
		}
	}
	
	@Override
	public synchronized IWriteOperationRecorder writeFolder( Path relativeFolderPath, ManifestFolderDescription manifestFolderDescription, FolderWriteWithPath folder ) {
		
		assert(folder!=null);
		folder.setParentFolder(this);
		folder.setPath( relativeFolderPath );
		folder.setManifestFolderDescription(manifestFolderDescription);
		folders.add(folder);
		
		return folder;
	}


	protected FolderWrite getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(FolderWrite parentFolder) {
		this.parentFolder = parentFolder;
	}

	protected List<FolderWrite> getFolderList() {
		return folders;
	}
	
	public abstract List<FileWrite> fileList();

	@Override
	public SequenceType getAssociatedSequence() {
		return getManifestFolderDescription().getSequenceType();
	}
}
