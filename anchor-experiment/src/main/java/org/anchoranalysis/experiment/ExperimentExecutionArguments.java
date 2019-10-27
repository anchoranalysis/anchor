package org.anchoranalysis.experiment;

import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.io.params.InputContextParams;

/*
 * #%L
 * anchor-experiment
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

public class ExperimentExecutionArguments {
	
	/**
	 * Is the GUI enabled
	 */
	private boolean guiEnabled = false;
	
	/**
	 * Is debug-mode enabled
	 */
	private boolean debugEnabled = false;
	
	/**
	 * A directory indicating where inputs can be located
	 */
	private Path inputDirectory;

	/**
	 * A directory indicating where inputs can be located
	 */
	private Path outputDirectory;


	/** Creates an input-context, reusing parameters from the experiment-execution 
	 * @throws IOException */
	public InputContextParams createInputContext() throws IOException {
		InputContextParams out = new InputContextParams();
		out.setDebugMode(debugEnabled);
		out.setInputDir(inputDirectory);
		out.setGuiMode(guiEnabled);
		return out;
	}
	
	public Path getInputDirectory() {
		return inputDirectory;
	}
	
	// The path will be converted to an absolute path, if it hasn't been already, based upon the current working directory
	public void setInputDirectory(Path inputDirectory) {
		
		if (inputDirectory==null) {
			this.inputDirectory = null;
			return;
		}
		
		if (!inputDirectory.isAbsolute()) {
			this.inputDirectory = inputDirectory.toAbsolutePath().normalize();
		} else {
			this.inputDirectory = inputDirectory.normalize();	
		}
	}
	
	public boolean hasInputDirectory() {
		return inputDirectory!=null;
	}

	public void setGUIEnabled( boolean value) {
		guiEnabled = value;
	}
	
	public void setDebugEnabled( boolean value ) {
		debugEnabled = value;
	}
	
	public boolean isGUIEnabled() {
		return guiEnabled;
	}
	
	public boolean isDebugEnabled() {
		return debugEnabled;
	}
		
	public Path getOutputDirectory() {
		return outputDirectory;
	}
	
	public boolean hasOutputDirectory() {
		return outputDirectory!=null;
	}

	public void setOutputDirectory(Path outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
}
