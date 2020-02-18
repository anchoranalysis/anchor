package org.anchoranalysis.io.output.bean;

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
import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.io.bean.output.allowed.OutputAllowed;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

/*
 *  Responsible for making decisions on where output goes and what form it takes
 *  
 *  Is always associated with a particular infilePath through init()
 *   which may be changed as the program is executed.
 */
public abstract class OutputManager extends AnchorBean<OutputManager> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6960949966148383845L;
	
	public abstract boolean isOutputAllowed( String outputName );
	
	/** A second-level of OutputAllowed for a particular key, or NULL if none is defined for this key */ 
	public abstract OutputAllowed outputAllowedSecondLevel( String key );
	
	public abstract BoundOutputManager bindRootFolder( String expIdentifier, ManifestRecorder writeOperationRecorder, boolean debugMode ) throws IOException;
	
	public abstract FilePathPrefix prefixForFile( Path infilePath, String expIdentifier, ManifestRecorder manifestRecorder, ManifestRecorder experimentalManifestRecorder, boolean debugMode ) throws IOException;
}
