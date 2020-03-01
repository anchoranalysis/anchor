package org.anchoranalysis.io.output.writer;

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


import java.nio.file.Path;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class AlwaysAllowed extends Writer {

	private BoundOutputManager bom;
	
	// Execute before every operation
	private WriterExecuteBeforeEveryOperation preop;

	public AlwaysAllowed(BoundOutputManager bom, WriterExecuteBeforeEveryOperation preop ) {
		this.bom = bom;
		this.preop = preop;
	}
	
	@Override
	public BoundOutputManager bindAsSubFolder( String outputName, ManifestFolderDescription manifestDescription, FolderWriteWithPath folder ) throws OutputWriteFailedException {
		
		preop.exec();
		
		assert bom.getWriteOperationRecorder()!=null;
		
		// We construct a sub-folder for the desired outputName
		Path folderOut = bom.getBoundFilePathPrefix().outFilePath(outputName);
		
		// We only change the writeOperationRecorder if we actually pass a folder
		IWriteOperationRecorder recorderNew;
		if (folder!=null) {
			// Assume the folder are writing to has no path
			assert(folder.calcPath()==null);
			
			Path relativePath = bom.getBoundFilePathPrefix().relativePath(folderOut);
			
			recorderNew = bom.getWriteOperationRecorder().writeFolder( relativePath, manifestDescription, folder);
		} else {
			recorderNew = bom.getWriteOperationRecorder();
		}
		
		FilePathPrefix fpp = new FilePathPrefix( folderOut );
		try {
			return new BoundOutputManager(
				bom.getOutputManager(),
				fpp,
				bom.getOutputWriteSettings(),
				recorderNew,
				bom.getLazyDirectoryFactory(),
				preop
			);
		} catch (AnchorIOException e) {
			throw new OutputWriteFailedException("Failed to create output-manager", e);
		}
	}
	
	
	@Override
	public void writeSubfolder( String outputName, Operation<? extends WritableItem> collectionGenerator ) throws OutputWriteFailedException {
		
		preop.exec();
		
		try {
			IndexableOutputNameStyle outputNameStyle = new IntegerSuffixOutputNameStyle(outputName, "_%03d");
			collectionGenerator.doOperation().write(outputNameStyle, bom.getBoundFilePathPrefix(), bom.getWriteOperationRecorder(), bom );
		} catch (ExecuteException e) {
			throw new OutputWriteFailedException("Cannot create generator", e.getCause());
		}	
	}
	
	@Override
	public int write( IndexableOutputNameStyle outputNameStyle, Operation<? extends WritableItem> generator, String index ) throws OutputWriteFailedException {
		
		preop.exec();
		
		try {
			return generator.doOperation().write( outputNameStyle, bom.getBoundFilePathPrefix(), bom.getWriteOperationRecorder(), index, bom);
		} catch (ExecuteException e) {
			throw new OutputWriteFailedException("Cannot create generator", e.getCause());
		}			
	}
	
	// Write a file without checking if the outputName is allowed
	@Override
	public void write( OutputNameStyle outputNameStyle, Operation<? extends WritableItem> generator ) throws OutputWriteFailedException {
		
		preop.exec();
		
		try {
			generator.doOperation().write( outputNameStyle, bom.getBoundFilePathPrefix(), bom.getWriteOperationRecorder(), bom);
		} catch (ExecuteException e) {
			throw new OutputWriteFailedException("Cannot create generator", e.getCause());
		}
	}
		
	
	// A non-generator way of creating outputs, that are still included in the manifest
	// Returns null if output is not allowed
	@Override
	public Path writeGenerateFilename( String outputName, String extension, ManifestDescription manifestDescription, String outputNamePrefix, String outputNameSuffix, String index ) {
		
		preop.exec();
		
		String fileMain = outputNamePrefix + outputName + outputNameSuffix + "." + extension;
		
		Path outfile_path = bom.getBoundFilePathPrefix().outFilePath( fileMain );
		bom.getWriteOperationRecorder().write(
			outputName,
			manifestDescription,
			bom.getBoundFilePathPrefix().relativePath(outfile_path),
			index
		);
		
		return outfile_path;
	}
	
	@Override
	public OutputWriteSettings getOutputWriteSettings() {
		return bom.getOutputWriteSettings();
	}
	
}
