package org.anchoranalysis.io.manifest.finder;

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


import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.FileWriteManifestMatch;
import org.anchoranalysis.io.manifest.match.ManifestDescriptionFunctionMatch;

public class FinderKeyValueParams extends FinderSingleFile {
	
	private String manifestFunction;
	
	public FinderKeyValueParams(String manifestFunction, ErrorReporter errorReporter) {
		super(errorReporter);
		this.manifestFunction = manifestFunction;
	}
	
	public KeyValueParams get() throws GetOperationFailedException {
		assert( exists() );
	
		try {
			return KeyValueParams.readFromFile( getFoundFile().calcPath() );
		} catch (IOException e) {
			throw new GetOperationFailedException(e);
		}
	}


	@Override
	protected Optional<FileWrite> findFile(ManifestRecorder manifestRecorder)
			throws MultipleFilesException {
		List<FileWrite> files = FinderUtilities.findListFile( manifestRecorder,
			new FileWriteManifestMatch(
				new ManifestDescriptionFunctionMatch(manifestFunction)
			)
		);
		
		if (files.isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(
			files.get(0)
		);
	}
}