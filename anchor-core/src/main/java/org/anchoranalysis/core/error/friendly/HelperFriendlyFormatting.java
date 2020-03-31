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
import java.io.StringWriter;
import java.io.Writer;

/**
 * Utilities for describing an exception (with lots of nested causes) in a user-friendly way
 * 
 * @author Owen Feehan
 *
 */
class HelperFriendlyFormatting {

	/**
	 * Incrementally prepended to each exception when describing nested-exceptions 
	 */
	private static final String PREPEND_EXCEPTION_STRING = "   ";

	private HelperFriendlyFormatting() {
		// Only static access
	}	
	
	/**
	 * A friendly-message describing what went wrong, but WITHOUT describing any further causes (nested exceptions)
	 * 
	 * We search for the first non-empty error message
	 * 
	 * @param excDescribe the exception to describe
	 * @return a string with user-friendly error message
	 */
	public static String friendlyMessageNonHierarchical( Throwable excDescribe ) {
		
		Throwable e = excDescribe;
		while( e!=null ) {
			
			if (e.getMessage()!=null && !e.getMessage().isEmpty()) {
				return e.getMessage();
			}
			
			e = e.getCause();
		}
		throw new IllegalStateException("None of the nested exceptions have an associated message");
	}

	/**
	 * A friendly-message describing what went wrong, additionally any further causes (nested exceptions)
	 * 
	 * We display an increasingly-indented multiline message with the nested-causes (skipping any
	 *  exception with an empty message)
	 * 
	 * @param excDescribe the exception to describe
	 * @return a string with user-friendly error message
	 */	
	public static String friendlyMessageHierarchy( Throwable excDescribe ) {
		StringWriter writer = new StringWriter();
		try {
			friendlyMessageHierarchical(excDescribe, writer, -1, false);
		} catch (IOException e) {
			assert false;
			throw new AssertionError(
				"An IO exception occured generating a friendlyMessage with a StringWriter. This shouldn't happen!",
				e
			);
		}
		return writer.toString();
	}
	
	/**
	 * We search for the first non-empty error message
	 * @param exc the exception to describe
	 * @param writer where the friendly-messaged is outputted
	 * @param wordWrapLimit max-number of characters of a single-line of the screen (considering only prefix + line), if it's -1 it is disabled
	 * @param showExceptionType adds a rightmost column showing the exception class type
	 * 
	 * @throws IOException if an I/O error occurs with the writer
	 * 
	 */
	public static void friendlyMessageHierarchical( Throwable exc, Writer writer, int wordWrapLimit, boolean showExceptionType ) throws IOException {
		recursivelyDescribeExceptionStack(exc, writer, PREPEND_EXCEPTION_STRING, wordWrapLimit, showExceptionType);
	}
	

	/**
	 * Incrementally adds a line to a string builder with the message of each exception
	 * moving onto the next nested exception until there are no more
	 *
	 * @param excDescribe exception to describe
	 * @param writer where the friendly-messaged is outputted
	 * @param prefix incrementally prepended to each message (first message is skipped)
	 * @param wordWrapLimit max-number of characters of a single-line of the screen (considering only prefix + line), if it's -1 it is disabled
	 * @param showExceptionType adds a rightmost column showing the exception class type
	 * @throws IOException if an I/O error occurs with the writer
	 */
	private static void recursivelyDescribeExceptionStack( Throwable excDescribe, Writer writer, String prefix, int wordWrapLimit, boolean showExceptionType ) throws IOException {
		
		// Calculate the maximum length of a message
		int maxLength = calculateMaximumLengthMessage( excDescribe, prefix );
		
		// If it's greater than our word-wrap limit, then we reduce it to our word wrap limit
		if (wordWrapLimit!=-1 && maxLength > wordWrapLimit) {
			maxLength = wordWrapLimit;
		}
		
		String prefixCurrent = "";
		
		Throwable e = excDescribe;
		boolean firstLine = true;
		while( true ) {

			// Skip any exception with any empty message unless it's the last exception in the chain
			// (if it's the last exception in the chain then e.getCause()==e or .getCause()==null)
			/*if (hasEmptyMessage(e) && !isFinal(e)) {
				e = e.getCause();
				continue;
			}*/
			
			// Unless it's the very first line of the exception, we add a newline
			if( firstLine)	{ // Test for being the first line
				firstLine = false;
			} else {
				writer.append( System.lineSeparator() );
			}
			
			// Extract a message from the exception, and append it to the writer
			// with maybe prefix and suffix
			splitFromExc(e).appendNicelyWrappedLines(
				writer,
				prefixCurrent,
				suffixFor(showExceptionType, e),
				maxLength
			);
			
			// Move to next nested exception
			if (!isFinal(e)) {
				e = e.getCause();
				prefixCurrent = prefixCurrent + prefix;
			} else {
				break;
			}
		}
	}

	
	/**
	 * Calculates the maximum-length of the messages (including the prefixxed strings) of all nested-exceptions
	 * 
	 * Each message is split by its newline characters, so that each line is treated individually
	 *   
	 * @param excDescribe exception to describe
	 * @param prefix incrementally prepended to each message (first message is skipped) 
	 * @return the maximum-length of the message associated with an exception including its prepended string
	 */
	private static int calculateMaximumLengthMessage( Throwable excDescribe, String prefix  ) {
		assert(excDescribe!=null);
		
		int maxLength = Integer.MIN_VALUE;
		
		int prefixCurrentLength = 0;
		Throwable e = excDescribe;
		while( true ) {
			
			// Skip any exception with any empty message
			/*if (e.getMessage()==null || e.getMessage().isEmpty()) {
				e = e.getCause();
				continue;
			}*/
			
			SplitString splitMessage = splitFromExc(e);
			
			// Size of message we are considering
			int msgLength = splitMessage.maxLength() + prefixCurrentLength;
			
			if (msgLength>maxLength) {
				maxLength = msgLength;
			}
			
			// Current size of the prepended string
			if (!isFinal(e)) {
				e = e.getCause();
				prefixCurrentLength += prefix.length();
			} else {
				break;
			}
		}
		return maxLength;
	}

	/** we only have a suffix if we are showing the exception type */
	private static String suffixFor( boolean showExceptionType, Throwable e ) {
		if (showExceptionType) {
			return String.format("    (%s)",  e.getClass().getSimpleName() );
		} else {
			return "";
		}
	}
	
	/** Extracts a message, and splits it by newline */
	private static SplitString splitFromExc( Throwable exc ) {
		return new SplitString(
			messageFromExc(exc)
		);
	}
	
	/** If there's a message it's used, otherwise the SimpleName of the exception */
	private static String messageFromExc( Throwable exc ) {
		if (hasEmptyMessage(exc)) {
			return exc.getClass().getSimpleName();
		} else {
			return exc.getMessage();
		}
	}
	
	/** Does the exception lack a message? */
	private static boolean hasEmptyMessage( Throwable exc ) {
		return exc.getMessage()==null || exc.getMessage().isEmpty();
	}
	
	/** Is it the last exception in the chain? */
	private static boolean isFinal( Throwable exc ) {
		return exc.getCause()==null || exc.getCause()==exc;
	}
}
