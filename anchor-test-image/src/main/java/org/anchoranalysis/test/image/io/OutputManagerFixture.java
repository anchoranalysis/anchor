package org.anchoranalysis.test.image.io;

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


import java.nio.file.Path;

import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.bean.filepath.prefixer.FilePathPrefixer;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.OutputManagerPermissive;
import org.anchoranalysis.io.output.bean.OutputManagerWithPrefixer;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.test.LoggingFixture;

public class OutputManagerFixture {

	
	// These operations must occur before creating TempBoundOutputManager
	private static void globalSetup() {
		TestReaderWriterUtilities.ensureRasterWriter();		
	}
	
	public static BoundOutputManagerRouteErrors outputManagerForRouterErrors( Path pathTempFolder ) throws AnchorIOException {
		
		ErrorReporter errorReporter = LoggingFixture.simpleLogErrorReporter().getErrorReporter();

		return new BoundOutputManagerRouteErrors(
			createBoundOutputManagerFor(pathTempFolder, errorReporter),
			errorReporter
		);
	}
	
	public static BoundOutputManager outputManagerFor( Path pathTempFolder ) throws AnchorIOException {
		return createBoundOutputManagerFor(
			pathTempFolder,
			LoggingFixture.simpleLogErrorReporter().getErrorReporter()
		);
	}
	
	private static BoundOutputManager createBoundOutputManagerFor( Path pathTempFolder, ErrorReporter errorReporter ) throws AnchorIOException {
		
		globalSetup();
				
		OutputWriteSettings ows = new OutputWriteSettings();
		
		// We populate any defaults in OutputWriteSettings from our default bean factory
		try {
			ows.checkMisconfigured( RegisterBeanFactories.getDefaultInstances() );
		} catch (BeanMisconfiguredException e1) {
			errorReporter.recordError(OutputManagerFixture.class, e1);
		}
		
		OutputManagerWithPrefixer outputManager = new OutputManagerPermissive();
		outputManager.setDelExistingFolder( true );
		outputManager.setOutputWriteSettings( ows );
		outputManager.setFilePathPrefixer( 
			new FilePathPrefixerConstantPath(pathTempFolder)
		);
		
		return outputManager.bindRootFolder( "debug", new ManifestRecorder(), new FilePathPrefixerParams(false, null) );
	}

	private static class FilePathPrefixerConstantPath extends FilePathPrefixer {
		
		private FilePathPrefix prefix;
		
		public FilePathPrefixerConstantPath(Path path) {
			prefix = new FilePathPrefix(path);
		}

		@Override
		public FilePathPrefix outFilePrefix(InputFromManager input, String experimentIdentifier,
				FilePathPrefixerParams context) throws AnchorIOException {
			return prefix;
		}

		@Override
		public FilePathPrefix rootFolderPrefix(String experimentIdentifier, FilePathPrefixerParams context)
				throws AnchorIOException {
			return prefix;
		}
	}
	
}
