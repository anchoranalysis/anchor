package org.anchoranalysis.io.generator.collection;

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
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.operationrecorder.IWriteOperationRecorder;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class SubfolderGenerator<T,CollectionType extends Collection<T>> extends Generator implements IterableGenerator<CollectionType> {

	private CollectionType element;
	
	private IterableGenerator<T> generator;
	private String collectionOutputName;
	
	public SubfolderGenerator(IterableGenerator<T> generator, String collectionOutputName) {
		super();
		this.generator = generator;
		this.collectionOutputName = collectionOutputName;
	}

	@Override
	public void write(OutputNameStyle outputNameStyle,
			FilePathCreator filePathGnrtr,
			IWriteOperationRecorder writeOperationRecorder,
			BoundOutputManager outputManager)
			throws OutputWriteFailedException {
		
		String filePhysicalName = outputNameStyle.getPhysicalName();
		IterableGeneratorWriter.writeSubfolder(
			outputManager,
			filePhysicalName,
			collectionOutputName,
			generator,
			element,
			false
		);
	}

	@Override
	public int write(IndexableOutputNameStyle outputNameStyle,
			FilePathCreator filePathGnrtr,
			IWriteOperationRecorder writeOperationRecorder,
			String index, BoundOutputManager outputManager)
			throws OutputWriteFailedException {

		String filePhysicalName = outputNameStyle.getPhysicalName(index);
	
		IterableGeneratorWriter.writeSubfolder(outputManager, filePhysicalName, collectionOutputName, generator, element, false);
		return 1;
	}

	@Override
	public FileType[] getFileTypes(OutputWriteSettings outputWriteSettings) {
		return generator.getGenerator().getFileTypes(outputWriteSettings);
	}

	@Override
	public CollectionType getIterableElement() {
		return element;
	}

	@Override
	public void setIterableElement(CollectionType element)
			throws SetOperationFailedException {
		this.element = element;
	}

	@Override
	public void start() throws OutputWriteFailedException {

	}

	@Override
	public void end() throws OutputWriteFailedException {

	}

	@Override
	public Generator getGenerator() {
		return this;
	}
	
	public static ManifestDescription createManifestDescription(String type) {
		return new ManifestDescription("subfolder", type);
	}
}
