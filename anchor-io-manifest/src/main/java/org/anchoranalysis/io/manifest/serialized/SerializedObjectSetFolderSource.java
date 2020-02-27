package org.anchoranalysis.io.manifest.serialized;

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
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.io.bean.file.matcher.MatchGlob;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.bean.provider.file.SearchDirectory;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.folder.FolderWritePhysical;
import org.anchoranalysis.io.manifest.folder.SequencedFolder;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;
import org.anchoranalysis.io.params.InputContextParams;

public class SerializedObjectSetFolderSource extends SequencedFolder {

	private HashMap<String,FileWrite> mapFileWrite = new HashMap<>();
	private IncrementalSequenceType incrSequenceType;
	
	// Constructor	
	public SerializedObjectSetFolderSource( Path folderPath ) throws SequenceTypeException {
		this(folderPath, null);
	}
	
	// Constructor	
	public SerializedObjectSetFolderSource( Path folderPath, String acceptFilter ) throws SequenceTypeException {
		super();

		SearchDirectory fileSet = createFileSet(folderPath, acceptFilter);
		
		incrSequenceType = new IncrementalSequenceType();
		int i = 0;
		
		
		FolderWritePhysical fwp = new FolderWritePhysical();
		fwp.setPath( folderPath );
		fwp.setParentFolder(null);
		
		try {
			Collection<File> files = fileSet.matchingFiles(
				new InputManagerParams(
					new InputContextParams(),
					ProgressReporterNull.get(),
					null		// HACK: Can be safely set to null as fileSet.setIgnoreHidden(false);						
				)
			);
			
			for ( File file : files ) {
				
				String iStr = Integer.toString(i);
				
				FileWrite fw = new FileWrite(fwp);
				fw.setFileName( file.getName() );
				fw.setIndex( iStr );
				fw.setOutputName("serializedObjectSetFolder");
				fw.setManifestDescription(null);
	
				mapFileWrite.put( iStr,  fw );
				
				incrSequenceType.update( String.valueOf(i) );
				
				i++;
			}
		} catch (AnchorIOException e) {
			throw new SequenceTypeException(e);
		}
	}

	@Override
	public SequenceType getAssociatedSequence() {
		return incrSequenceType;
	}

	@Override
	public void findFileFromIndex(List<FileWrite> foundList, String index,
			boolean recursive) {
		
		FileWrite fw = mapFileWrite.get(index);
		
		if (fw!=null) {
			foundList.add( fw );
		}
	}
	
	// AcceptFilter can be null in which case, it is ignored
	private SearchDirectory createFileSet( Path folderPath, String acceptFilter ) {
		
		// We use fileSets so as to be expansible with the future
		SearchDirectory fileSet = new SearchDirectory();
		fileSet.setDirectory( folderPath );
		fileSet.setIgnoreHidden(false);
		
		if (acceptFilter!=null) {
			fileSet.setMatcher( new MatchGlob(acceptFilter) );
		}
		
		return fileSet;
	}
}
