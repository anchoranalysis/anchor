package org.anchoranalysis.core.error.reporter;

import org.anchoranalysis.core.error.friendly.IFriendlyException;
import org.anchoranalysis.core.log.LogReporter;

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


import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Records errors, by writing them into a logReporter
 * 
 * Does some formatting (and sometimes adds a stacktrace) depending on context and exception-type
 * 
 * @author Owen Feehan
 *
 */
public class ErrorReporterIntoLog implements ErrorReporter {
	
	private LogReporter logReporter;

	public ErrorReporterIntoLog(LogReporter logReporter) {
		super();
		assert logReporter!=null;
		this.logReporter = logReporter;
	}

	@Override
	public void recordError( Class<?> classOriginating, Throwable exc ) {
		
		// Special behaviour if it's a friendly exception
		if (exc instanceof IFriendlyException) {
			startErrorBanner();
			IFriendlyException eCast = (IFriendlyException) exc;
			logReporter.log( eCast.friendlyMessageHierarchy() );
			endErrorBanner();
		} else {
			startErrorBanner();
			try {
				logReporter.log( exc.toString() );
				logReporter.log("");	// newline
				logReporter.log( ExceptionUtils.getFullStackTrace(exc) );
			} catch (Exception e) {
				logReporter.log("An error occurred while writing an error: " + e.toString());
			} finally {
				endErrorBanner(classOriginating);
			}
		}
	}

	@Override
	public void recordError(Class<?> classOriginating, String errorMsg) {
		startErrorBanner();
		try {
			logReporter.log( errorMsg );
		} catch (Exception e) {
			logReporter.log("An error occurred while writing an error: " + e.toString());
		} finally {
			endErrorBanner(classOriginating);
		}
	}
	
	private void startErrorBanner() {
		logReporter.log( "------------ BEGIN ERROR ------------" );
	}
	
	private void endErrorBanner() {
		logReporter.log("------------ END ERROR ------------");
	}
	
	private void endErrorBanner( Class<?> c ) {
		logReporter.logFormatted( "%nThe error occurred when executing a method in class %s%n------------ END ERROR ------------", c.getName() );
	}
}
