package org.anchoranalysis.core.error.friendly;

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


import java.io.IOException;
import java.io.Writer;

/**
 * Methods for extracting a user-friendly error description 
 * @author Owen Feehan
 *
 */
public interface IFriendlyException {

	/**
	 * A friendly message to describe to the user what went wrong. If no message has been
	 *   defined in this exception, we keep iterating the hierarchy until we find a message
	 * @return a string describing what went wrong
	 */
	String friendlyMessage();
	
	/**
	 * A friendly message to describe to the user what went wrong, which doesn't impose a fixed-size, or showExceptionNames
	 *   
	 * @return a string (often multi-lined) describing a hierarchy of errors that occurred
	 */
	String friendlyMessageHierarchy();
	
	/**
	 * A friendly message to describe to the user what went wrong, including all nested exceptions
	 *   that have a non-empty error message
	 * 
	 * It starts the least-most-nested exception. It ends with the further-most-nested exception (cause).
	 * 
	 * @param writer where the friendly-messaged is outputted
	 * @param wordWrapLimit a fixed-width size for the message and indentation (ignoring the exception name). -1 disables.
	 * @param showExceptionNames show exception-names after the error output  
	 * @throws IOException if an I/O error occurs with the writer 
	 */
	void friendlyMessageHierarchy( Writer writer, int wordWrapLimit, boolean showExceptionNames ) throws IOException;
}
