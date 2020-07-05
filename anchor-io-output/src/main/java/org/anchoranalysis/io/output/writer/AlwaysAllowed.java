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
import java.util.Optional;

import org.anchoranalysis.core.functional.Operation;
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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AlwaysAllowed implements Writer {

	// START REQUIRED ARGUMENTS
	/** Bound output manager */
	private final BoundOutputManager bom;
	
	/** Execute before every operation */
	private final WriterExecuteBeforeEveryOperation preop;
	// END REQUIRED ARGUMENTS
	
	@Override
	public Optional<BoundOutputManager> bindAsSubFolder(
		String outputName,
		ManifestFolderDescription manifestDescription,
		Optional<FolderWriteWithPath> folder
	) throws OutputWriteFailedException {
		
		preop.exec();
		
		// We construct a sub-folder for the desired outputName
		Path folderOut = bom.outFilePath(outputName);
		
		// We only change the writeOperationRecorder if we actually pass a folder
		IWriteOperationRecorder recorderNew = bom.writeFolderToOperationRecorder(
			folderOut,
			manifestDescription,
			folder
		);
		
		FilePathPrefix fpp = new FilePathPrefix( folderOut );
		return Optional.of(
			new BoundOutputManager(
				bom.getOutputManager(),
				fpp,
				bom.getOutputWriteSettings(),
				recorderNew,
				bom.getLazyDirectoryFactory(),
				Optional.of(preop)
			)
		);
	}
	
	
	@Override
	public void writeSubfolder( String outputName, Operation<? extends WritableItem,OutputWriteFailedException> collectionGenerator ) throws OutputWriteFailedException {
		
		preop.exec();

		collectionGenerator.doOperation().write(
			new IntegerSuffixOutputNameStyle(outputName,3),
			bom
		);
	}
	
	@Override
	public int write( IndexableOutputNameStyle outputNameStyle, Operation<? extends WritableItem,OutputWriteFailedException> generator, String index ) throws OutputWriteFailedException {
		
		preop.exec();
		return generator.doOperation().write( outputNameStyle, index, bom);
	}
	
	// Write a file without checking if the outputName is allowed
	@Override
	public void write( OutputNameStyle outputNameStyle, Operation<? extends WritableItem,OutputWriteFailedException> generator ) throws OutputWriteFailedException {
		
		preop.exec();
		generator.doOperation().write(outputNameStyle, bom);
	}
		
	
	// A non-generator way of creating outputs, that are still included in the manifest
	// Returns null if output is not allowed
	@Override
	public Optional<Path> writeGenerateFilename( String outputName, String extension, Optional<ManifestDescription> manifestDescription, String outputNamePrefix, String outputNameSuffix, String index ) {
		
		preop.exec();
		
		Path outPath = bom.outFilePath(
			outputNamePrefix + outputName + outputNameSuffix + "." + extension
		);
		
		manifestDescription.ifPresent( md->
			bom.writeFileToOperationRecorder(
				outputName,
				outPath,
				md,
				index
			)
		);
		return Optional.of(outPath);
	}
	
	@Override
	public OutputWriteSettings getOutputWriteSettings() {
		return bom.getOutputWriteSettings();
	}
	
}
