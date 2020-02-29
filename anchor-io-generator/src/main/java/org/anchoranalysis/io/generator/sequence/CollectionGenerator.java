package org.anchoranalysis.io.generator.sequence;

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


import java.util.Collection;

import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.filepath.prefixer.FilePathCreator;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class CollectionGenerator<T> extends Generator implements IterableGenerator<Collection<T>> {

	private Collection<T> collection;
	private IterableGenerator<T> generator;
	private BoundOutputManager outputManager;
	private int numDigits;
	private boolean checkIfAllowed;
	private String subfolderName;
	
	public CollectionGenerator(String subfolderName,
			IterableGenerator<T> generator, BoundOutputManager outputManager, int numDigits, boolean checkIfAllowed ) {
		super();
		this.generator = generator;
		this.outputManager = outputManager;
		this.numDigits = numDigits;
		this.checkIfAllowed = checkIfAllowed;
		this.subfolderName = subfolderName;
	}
	
	public CollectionGenerator(Collection<T> collection, String subfolderName,
			IterableGenerator<T> generator, BoundOutputManager outputManager, int numDigits, boolean checkIfAllowed ) {
		super();
		this.collection = collection;
		this.generator = generator;
		this.outputManager = outputManager;
		this.numDigits = numDigits;
		this.checkIfAllowed = checkIfAllowed;
		this.subfolderName = subfolderName;
	}

	@Override
	public void write(OutputNameStyle outputNameStyle,
			FilePathCreator filePathGnrtr,
			IWriteOperationRecorder writeOperationRecorder,
			BoundOutputManager outputManager) throws OutputWriteFailedException {
		
		writeCollection( subfolderName, outputNameStyle.deriveIndexableStyle(numDigits), 0 );
	}

	@Override
	public int write(IndexableOutputNameStyle outputNameStyle,
			FilePathCreator filePathGnrtr,
			IWriteOperationRecorder writeOperationRecorder,
			String index, BoundOutputManager outputManager)
			throws OutputWriteFailedException {
		
		// In this context, we take the index as an indication of the first id to use - and assume the String index is a number
		int indexInt = Integer.parseInt(index);
		int numWritten = writeCollection( subfolderName, outputNameStyle, indexInt );
		return numWritten;
	}
	
	private int writeCollection( String subfolderName, IndexableOutputNameStyle outputNameStyle, int startIndex ) throws OutputWriteFailedException {
		
		assert( collection!= null );
		
		// We start with id with 0
		GeneratorSequenceIncrementalWriter<T> sequenceWriter = new GeneratorSequenceIncrementalWriter<>(
			outputManager,
			subfolderName,
			outputNameStyle,
			generator,
			startIndex,
			checkIfAllowed
		);
		
		int numWritten = 0;
		
		sequenceWriter.start();
		for (T element : collection) {
			sequenceWriter.add(element);
			numWritten++;
		}
		sequenceWriter.end();
	
		return numWritten;
	}

	@Override
	public FileType[] getFileTypes(OutputWriteSettings outputWriteSettings) {
		return generator.getGenerator().getFileTypes(outputWriteSettings);
	}

	@Override
	public Collection<T> getIterableElement() {
		return collection;
	}

	@Override
	public void setIterableElement(Collection<T> element)
			throws SetOperationFailedException {
		this.collection = element;
	}

	@Override
	public void start() throws OutputWriteFailedException {
		generator.start();
		
	}

	@Override
	public void end() throws OutputWriteFailedException {
		generator.end();
	}

	@Override
	public Generator getGenerator() {
		return this;
	}
}
