package org.anchoranalysis.io.generator.sequence;

import java.util.Optional;

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

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


public class GeneratorSequenceNonIncrementalWriter<T> implements GeneratorSequenceNonIncremental<T> {

	private BoundOutputManager parentOutputManager = null;
	
	private IterableGenerator<T> iterableGenerator;
	
	private SequenceType sequenceType;
	
	private SequenceWriter sequenceWriter;
	
	private boolean firstAdd = true;
	
	private boolean suppressSubfolder;

	// Automatically create a ManifestDescription for the folder from the Generator
	public GeneratorSequenceNonIncrementalWriter( BoundOutputManager outputManager, String subfolderName, IndexableOutputNameStyle outputNameStyle, IterableGenerator<T> iterableGenerator, boolean checkIfAllowed ) {
		this( outputManager, subfolderName, outputNameStyle, iterableGenerator, checkIfAllowed, null);
	}
	
	// User-specified ManifestDescription for the folder
	public GeneratorSequenceNonIncrementalWriter( BoundOutputManager outputManager, String subfolderName, IndexableOutputNameStyle outputNameStyle, IterableGenerator<T> iterableGenerator, boolean checkIfAllowed, ManifestDescription folderManifestDescription ) {
		super();

		this.sequenceWriter = new SubfolderWriter( outputManager, subfolderName, outputNameStyle, folderManifestDescription, checkIfAllowed );
		assert(outputManager.getOutputWriteSettings().hasBeenInit());
		this.parentOutputManager = outputManager;
		this.iterableGenerator = iterableGenerator;
	}


	public boolean isOn() {
		return sequenceWriter.isOn();
	}
	
	private void initOnFirstAdd() throws InitException {
		
		// For now we only take the first FileType from the generator, we will have to modify this in future
		FileType[] fileTypes = iterableGenerator
				.getGenerator()
				.getFileTypes( this.parentOutputManager.getOutputWriteSettings() )
				.orElseThrow( ()->
					new InitException("This operation requires file-types to be defined by the generator")
				);
				
		this.sequenceWriter.init(fileTypes, this.sequenceType, this.suppressSubfolder );
	}
	
	@Override
	public void add( T element, String index ) throws OutputWriteFailedException {

		try {
			iterableGenerator.setIterableElement( element );
			
			// We delay the initialisation of subFolder until the first iteration and we have a valid generator
			if (firstAdd==true) {
				
				initOnFirstAdd();
				firstAdd = false;
			}
			
			// Then output isn't allowed and we should just exit
			if (!sequenceWriter.isOn()) {
				return;
			}
			
			sequenceType.update( index );
			this.sequenceWriter.write(
				() -> iterableGenerator.getGenerator(),
				String.valueOf(index)
			);
		} catch (InitException e) {
			throw new OutputWriteFailedException(e);
		} catch (SequenceTypeException e) {
			throw new OutputWriteFailedException(e);
		} catch (SetOperationFailedException e) {
			throw new OutputWriteFailedException(e);
		}
	}

	@Override
	public void start( SequenceType sequenceType, int totalNumAdd ) throws OutputWriteFailedException {
		iterableGenerator.start();
		this.sequenceType = sequenceType;
	}

	@Override
	public void end() throws OutputWriteFailedException {
		iterableGenerator.end();
	}
	
	public Optional<BoundOutputManager> getSubFolderOutputManager() {
		return sequenceWriter.getOutputManagerForFiles();
	}
		
	public boolean isSuppressSubfolder() {
		return suppressSubfolder;
	}

	public void setSuppressSubfolder(boolean suppressSubfolder) {
		this.suppressSubfolder = suppressSubfolder;
	}	
}
