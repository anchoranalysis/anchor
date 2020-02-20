package org.anchoranalysis.experiment.log.reporter;

/*-
 * #%L
 * anchor-experiment
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

import java.io.IOException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.file.FileOutput;

/** Writes text to a file, but only if close is called with a successful=true
 * 
 *  The text cannot be written immediately, so is saved until close() is called.
 *  
 * @author feehano
 *
 */
public class FailureOnlyTextFileLogReporter implements StatefulLogReporter {

	private StringBuilder sb;
	
	private BoundOutputManager bom;
	private ErrorReporter errorReporter;
	
	public FailureOnlyTextFileLogReporter(BoundOutputManager bom, ErrorReporter errorReporter) {
		super();
		this.bom = bom;
		this.errorReporter = errorReporter;
	}	
	
	@Override
	public void log(String message) {
		sb.append(message);
		sb.append( System.lineSeparator() );
	}

	@Override
	public void logFormatted(String formatString, Object... args) {
		log( String.format(formatString,args) );
	}

	@Override
	public void start() {
		sb = new StringBuilder();
	}

	@Override
	public void close(boolean successful) {
		if (!successful) {
			writeStringToFile( sb.toString() );
		}
	}
	
	private void writeStringToFile( String message ) {
		
		try {
			FileOutput fileOutput = TextFileLogHelper.createOutput(bom);
			
			if (fileOutput==null) {
				return;
			}
			
			fileOutput.start();
			fileOutput.getWriter().append( message );
			fileOutput.end();
			
		} catch (IOException | OutputWriteFailedException e) {
			errorReporter.recordError(LogReporter.class, e);
		}
	}

}
