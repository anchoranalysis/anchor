package org.anchoranalysis.io.manifest.finder;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.Match;

public class FinderUtilities {

	private FinderUtilities() {}
	
	public static List<FileWrite> findListFile( ManifestRecorder manifestRecorder, Match<FileWrite> match ) {
		
		ArrayList<FileWrite> foundList = new ArrayList<>();
		manifestRecorder.getRootFolder().findFile(foundList, match, false);
		return foundList;
	}
	
	public static List<FolderWrite> findListFolder( ManifestRecorder manifestRecorder, Match<FolderWrite> match ) {
		
		ArrayList<FolderWrite> foundList = new ArrayList<>();
		manifestRecorder.getRootFolder().findFolder(foundList, match);
		return foundList;
	}
	
	
	public static FileWrite findSingleItem( ManifestRecorder manifestRecorder, Match<FileWrite> match ) throws MultipleFilesException {
		
		List<FileWrite> files = findListFile( manifestRecorder, match );
		if (files.size()==0) {
			return null;
		}
		if (files.size()>1) {
			throw new MultipleFilesException("More than one matching object was found in the manifest");
		}
		
		return files.get(0);
	}
}
