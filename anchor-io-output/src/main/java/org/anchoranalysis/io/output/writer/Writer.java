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
import org.anchoranalysis.io.output.namestyle.SimpleOutputNameStyle;

// 

/**
 * Allows users to write various things to the file system based upon
//  the properties of the current bound output manager
 * 
 * We use Operations so that the generator is only calculated, if the operation is actually written
 * 
 * @author Owen Feehan
 *
 */
public abstract class Writer {

	public abstract BoundOutputManager bindAsSubFolder( String outputName, ManifestFolderDescription manifestDescription, FolderWriteWithPath folder ) throws OutputWriteFailedException;
	
	public abstract void writeSubfolder( String outputName, Operation<? extends WritableItem> collectionGenerator ) throws OutputWriteFailedException;
	
	public abstract int write( IndexableOutputNameStyle outputNameStyle, Operation<? extends WritableItem> generator, String index ) throws OutputWriteFailedException;

	public abstract void write( OutputNameStyle outputNameStyle, Operation<? extends WritableItem> generator ) throws OutputWriteFailedException;
	
	public void write( String outputName, Operation<? extends WritableItem> generator ) throws OutputWriteFailedException {
		write( new SimpleOutputNameStyle(outputName), generator);
	}
	
	// Write a file with an index represented by an int, returns the number of files created
	public int write( IndexableOutputNameStyle outputNameStyle, Operation<? extends WritableItem> generator, int index ) throws OutputWriteFailedException {
		return write( outputNameStyle, generator, Integer.toString(index) );
	}

	/**
	 * The path to write a particular output to
	 * 
	 * @param outputName
	 * @param extension
	 * @param manifestDescription
	 * @param outputNamePrefix
	 * @param outputNameSuffix
	 * @param index
	 * @return the path to write to or null if the output is not allowed
	 */
	public abstract Path writeGenerateFilename( String outputName, String extension, ManifestDescription manifestDescription, String outputNamePrefix, String outputNameSuffix, String index );
	
	public abstract OutputWriteSettings getOutputWriteSettings();
}
