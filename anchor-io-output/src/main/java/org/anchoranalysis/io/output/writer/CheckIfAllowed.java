package org.anchoranalysis.io.output.writer;

/*-
 * #%L
 * anchor-io-output
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

import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.output.OutputWriteFailedException;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;

public class CheckIfAllowed extends Writer {

	private BoundOutputManager bom;
	private Writer writer;

	public CheckIfAllowed(BoundOutputManager bom, Writer writer) {
		this.bom = bom;
		this.writer = writer;
	}

	@Override
	public BoundOutputManager bindAsSubFolder(String outputName,
			ManifestFolderDescription manifestDescription,
			FolderWriteWithPath folder) throws OutputWriteFailedException {

		if (!bom.isOutputAllowed(outputName)) return null;
		
		return writer.bindAsSubFolder(outputName, manifestDescription, folder);
	}

	@Override
	public void writeSubfolder(String outputName,
			Operation<? extends WritableItem> collectionGenerator)
			throws OutputWriteFailedException {
		
		if (!bom.isOutputAllowed(outputName)) return;
		
		writer.writeSubfolder(outputName, collectionGenerator);
	}

	@Override
	public int write(IndexableOutputNameStyle outputNameStyle,
			Operation<? extends WritableItem> generator, String index)
			throws OutputWriteFailedException {
		
		if ( !bom.isOutputAllowed(outputNameStyle.getOutputName())) return -1;
		
		return writer.write(outputNameStyle, generator, index);
	}

	@Override
	public void write(OutputNameStyle outputNameStyle, Operation<? extends WritableItem> generator)
			throws OutputWriteFailedException {

		if ( !bom.isOutputAllowed(outputNameStyle.getOutputName())) return;
		
		writer.write(outputNameStyle, generator);
	}

	@Override
	public Path writeGenerateFilename(String outputName, String extension,
			ManifestDescription manifestDescription, String outputNamePrefix,
			String outputNameSuffix, String index) {
		
		if (!bom.isOutputAllowed(outputName)) return null;
		
		return writer.writeGenerateFilename(outputName, extension, manifestDescription, outputNamePrefix, outputNameSuffix, index);
	}
	
	@Override
	public OutputWriteSettings getOutputWriteSettings() {
		return bom.getOutputWriteSettings();
	}
}
