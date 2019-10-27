package org.anchoranalysis.io.output.file;

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

import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

public class FileOutputFromManager {
	
	private FileOutputFromManager() {
		
	}

	/**
	 * 
	 * Creates a FileOutput
	 * 
	 * @param extension file extension
	 * @param manifestDescription manifest description
	 * @param outputManager output-manager
	 * @param outputName output-name
	 * 
	 * @return the FileOutput or null if it the output is not allowed 
	 */
	public static FileOutput create(
		String extension,
		ManifestDescription manifestDescription,
		BoundOutputManager outputManager,
		String outputName
	) {
		
		
		
		Path fileOutputPath = outputManager.getWriterCheckIfAllowed().writeGenerateFilename(
			outputName,
			extension,
			manifestDescription,
			"",
			"",
			""
		);
		
		if (fileOutputPath==null) {
			return null;
		}
		
		return new FileOutput(fileOutputPath.toString(), extension, manifestDescription);
		
	}


	
}
