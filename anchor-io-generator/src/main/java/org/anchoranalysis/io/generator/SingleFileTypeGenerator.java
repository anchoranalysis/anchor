package org.anchoranalysis.io.generator;

/*-
 * #%L
 * anchor-io-generator
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

import java.nio.file.Path;

import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.filepath.prefixer.FilePathCreator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.output.OutputWriteFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;

public abstract class SingleFileTypeGenerator extends Generator {

	// We delegate to a much simpler method, for single file generators
	@Override	
	public void write( OutputNameStyle outputNameStyle, FilePathCreator filePathCreator, IWriteOperationRecorder writeOperationRecorder, BoundOutputManager outputManager ) throws OutputWriteFailedException {
		
		assert( outputManager.getOutputWriteSettings()!=null );
		
		String filePhysicalName = outputNameStyle.getPhysicalName() + "." + getFileExtension(outputManager.getOutputWriteSettings());
		
		Path outFilePath = filePathCreator.outFilePath( filePhysicalName ) ;
		
		writeToFile(outputManager.getOutputWriteSettings(), outFilePath );	

		writeOperationRecorder.write(
			outputNameStyle.getOutputName(),
			createManifestDescription(),
			filePathCreator.relativePath(outFilePath),
			""
		);
	}
	
	
	// We delegate to a much simpler method, for single file generators
	@Override
	public int write( IndexableOutputNameStyle outputNameStyle, FilePathCreator filePathCreator, IWriteOperationRecorder writeOperationRecorder, String index, BoundOutputManager outputManager ) throws OutputWriteFailedException {
		
		String filePhysicalName;
		filePhysicalName = outputNameStyle.getPhysicalName(index) + "." + getFileExtension(outputManager.getOutputWriteSettings());
		
		Path outFilePath = filePathCreator.outFilePath( filePhysicalName ) ;
		
		writeToFile(outputManager.getOutputWriteSettings(), outFilePath );	

		writeOperationRecorder.write(
			outputNameStyle.getOutputName(),
			createManifestDescription(),
			filePathCreator.relativePath(outFilePath),
			index
		);
		
		return 1;
	}

	// We create a single file type
	@Override
	public FileType[] getFileTypes( OutputWriteSettings outputWriteSettings ) {
		return new FileType[] {
				new FileType( createManifestDescription(), getFileExtension(outputWriteSettings) )		
		};
	}
	
	public abstract void writeToFile( OutputWriteSettings outputWriteSettings, Path filePath ) throws OutputWriteFailedException;
	
	public abstract String getFileExtension( OutputWriteSettings outputWriteSettings );
	
	public abstract ManifestDescription createManifestDescription();
}
