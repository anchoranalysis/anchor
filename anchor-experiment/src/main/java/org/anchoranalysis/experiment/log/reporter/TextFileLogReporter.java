package org.anchoranalysis.experiment.log.reporter;

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


import java.io.IOException;
import java.io.PrintWriter;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.file.FileOutput;

public class TextFileLogReporter implements StatefulLogReporter {

	private BoundOutputManager bom;
	private ErrorReporter errorReporter;
	
	private FileOutput fileOutput;
	private PrintWriter printWriter;
	
	public TextFileLogReporter(BoundOutputManager bom,
			ErrorReporter errorReporter) {
		super();
		this.bom = bom;
		this.errorReporter = errorReporter;
	}

	@Override
	public void logFormatted(String formatString, Object... args) {
		log( String.format(formatString,args) );
	}
	
	@Override
	public void start() {
		
		fileOutput = TextFileLogHelper.createOutput(bom);
		
		if (fileOutput==null) {
			return;
		}
	
		try {
			fileOutput.start();
			printWriter = fileOutput.getWriter();
		} catch (IOException e) {
			errorReporter.recordError(LogReporter.class, e);
		}		
	}

	@Override
	public void log( String message ) {
		if (fileOutput==null) {
			return;
		}
		
		if (printWriter!=null) {
			printWriter.print(message);
			printWriter.println();
			printWriter.flush();
		}
	}
	
	@Override
	public void close(boolean successful) {
		
		if (fileOutput==null) {
			return;
		}
		
		fileOutput.end();
	}


}
