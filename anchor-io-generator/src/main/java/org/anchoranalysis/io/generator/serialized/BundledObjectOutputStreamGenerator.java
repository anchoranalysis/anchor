package org.anchoranalysis.io.generator.serialized;

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

import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceIncrementalWriter;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.deserializer.bundle.Bundle;
import org.anchoranalysis.io.manifest.deserializer.bundle.BundleParameters;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class BundledObjectOutputStreamGenerator<T extends Serializable> extends Generator implements IterableGenerator<T> {

	private T element;
	
	private Bundle<T> bundle;
	
	private BundleParameters bundleParameters;
	
	private ObjectOutputStreamGenerator<Bundle<T>> outputGenerator;
	
	private GeneratorSequenceIncrementalWriter<Bundle<T>> generatorSequence;
	
	public BundledObjectOutputStreamGenerator( BundleParameters bundleParameters, IndexableOutputNameStyle indexableOutputNameStyle, BoundOutputManager parentOutputManager, String manifestDescriptionFunction ) {
		this.bundleParameters = bundleParameters;
		
		ManifestDescription manifestDescription = new ManifestDescription("serializedBundle", manifestDescriptionFunction);
		
		outputGenerator = new ObjectOutputStreamGenerator<>( manifestDescriptionFunction );
		
		generatorSequence = new GeneratorSequenceIncrementalWriter<>(
				parentOutputManager,
				indexableOutputNameStyle.getOutputName(),
				indexableOutputNameStyle,
				outputGenerator,
				manifestDescription,
				0,
				true
		);
	}
	
	@Override
	public void start() throws OutputWriteFailedException {
		bundle = new Bundle<>();
		generatorSequence.start();
		
	}

	

	@Override
	public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager) throws OutputWriteFailedException {
		throw new OutputWriteFailedException("this generator does not support writes without indexes");
	}
	
	@Override
	public int write(IndexableOutputNameStyle outputNameStyle, String index, BoundOutputManager outputManager) throws OutputWriteFailedException {
		bundle.add(index, element);
		
		// If we have reached our full capacity, then we serialize the bundle, and clear it for the next set of items
		if (bundle.size()==bundleParameters.getBundleSize()) {
			generatorSequence.add(bundle);
			this.bundle = new Bundle<>();
		}
		return 1;
	}
	

	@Override
	public void end() throws OutputWriteFailedException {
		generatorSequence.add(bundle);
		
		if (generatorSequence.isOn()) {
		
			Generator bundleParametersGenerator = new ObjectOutputStreamGenerator<>( bundleParameters,  "bundleParameters" );
			
			BoundOutputManager subfolderOutputManager = generatorSequence.getSubFolderOutputManager().orElseThrow( ()->
				new OutputWriteFailedException("No subfolder output-manager exists")
			);
			
			subfolderOutputManager.getWriterAlwaysAllowed().write(
				"bundleParameters",
				() -> bundleParametersGenerator
			);
		}
		
		generatorSequence.end();
	}
	
	@Override
	public FileType[] getFileTypes( OutputWriteSettings outputWriteSettings ) {
		return new FileType[] {
			new FileType( outputGenerator.createManifestDescription(), outputGenerator.getFileExtension(outputWriteSettings) )
		};
	}
	
	@Override
	public T getIterableElement() {
		return element;
	}

	@Override
	public void setIterableElement(T element) {
		this.element = element;
	}

	@Override
	public Generator getGenerator() {
		return this;
	}

}
