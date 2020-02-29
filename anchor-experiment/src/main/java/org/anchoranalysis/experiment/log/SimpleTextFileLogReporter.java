package org.anchoranalysis.experiment.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.anchoranalysis.experiment.log.reporter.StatefulLogReporter;

/*
 * #%L
 * anchor-core
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



public class SimpleTextFileLogReporter implements StatefulLogReporter {

	private String filePath;
	
	private PrintWriter writer;
	
	private boolean first = false;

	public SimpleTextFileLogReporter(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public void start() {
		try {
			writer = new PrintWriter(filePath);
			first = true;
		} catch (FileNotFoundException e) {
			System.out.println(
				String.format("Cannot create file %s for log", filePath)
			);
		}
		// NOTHING TO INITIALIZE
	}

	@Override
	public void log(String message) {
				
		// To avoid printing a newline at the end of the last-message
		// This removes a needless newline on the console at the end of the application.
		if (writer!=null) {
			
			if (first) {
				first = false;
			} else {
				writer.println();
			}
			
			writer.print(message); // NOSONAR
		}
	}
	
	@Override
	public void logFormatted(String formatString, Object... args) {
		if (writer!=null) {
			log( String.format(formatString,args) );
		}
	}

	@Override
	public void close(boolean successful) {
		// NOTHING TO CLOSE
	}
	
	
}
