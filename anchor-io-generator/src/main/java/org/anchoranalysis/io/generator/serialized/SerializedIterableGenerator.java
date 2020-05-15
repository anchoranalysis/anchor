package org.anchoranalysis.io.generator.serialized;

/*-
 * #%L
 * anchor-io-generator
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public abstract class SerializedIterableGenerator<T> extends SerializedGenerator implements IterableGenerator<T> {

	private T element = null;
	private String manifestFunction;
	
	public SerializedIterableGenerator( String manifestFunction ) {
		super();
		this.manifestFunction = manifestFunction;
	}
	
	public SerializedIterableGenerator(T element, String manifestFunction) {
		super();
		this.element = element;
		this.manifestFunction = manifestFunction;
	}
	
	@Override
	public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath) throws OutputWriteFailedException {
		
		if (getIterableElement()==null) {
			throw new OutputWriteFailedException("no mutable element set");
		}
		
		writeToFile(
			outputWriteSettings,
			filePath,
			getIterableElement()
		);
	}
	
	@Override
	public String getFileExtension(OutputWriteSettings outputWriteSettings) {
		return outputWriteSettings.getExtensionSerialized() + extensionSuffix(outputWriteSettings);
	}
	
	@Override
	public ManifestDescription createManifestDescription() {
		return new ManifestDescription("serialized", manifestFunction);
	}
	
	/** Writes a particular element to a file */
	protected abstract void writeToFile(
		OutputWriteSettings outputWriteSettings,
		Path filePath,
		T element
	) throws OutputWriteFailedException;
	
	/** Appended to the standard "serialized" extension, to form the complete extension */
	protected abstract String extensionSuffix( OutputWriteSettings outputWriteSettings );
	
	@Override
	public T getIterableElement() {
		return this.element;
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
