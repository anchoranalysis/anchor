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
import java.util.List;

import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.Match;

public class RootFolder extends FolderWrite implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -939826653076766321L;

	private FileList delegate;
	
	// We don't want to serialize this, as its temporary state (and an error will be thrown as WindowsPath is not serializable)
	private transient Path rootPath;
	
	public RootFolder() {
		
	}
	
	// This must be always called before it can used, as the state is kept transient
	public void initRootPath( Path rootFolderPath ) {
		if (delegate==null) {
			delegate = new FileList(this);
		}
		this.rootPath = rootFolderPath;
	}
	
	@Override
	public void write(String outputName,
			ManifestDescription manifestDescription, Path outFilePath,
			String index) {
		delegate.write(outputName, manifestDescription, outFilePath, index);
	}

	@Override
	public Path getRelativePath() {
		assert( rootPath!= null );
		return rootPath;
	}

	@Override
	public void findFile(List<FileWrite> foundList, Match<FileWrite> match,
			boolean recursive) {
		delegate.findFile(foundList, match, recursive);
	}

	@Override
	public List<FileWrite> fileList() {
		return delegate.getFileList();
	}
	
	public void add( FileWrite file ) {
		delegate.add( file );
	}
	
	public void add( FolderWrite folder ) {
		getFolderList().add(folder);
	}

}
