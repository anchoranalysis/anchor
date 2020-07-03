package org.anchoranalysis.core.error.friendly;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.core.error.combinable.AnchorCombinableException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Generates a nice string representation of an Exception and its causes according to certain rules.
 * 
 * @author owen
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class RecursivelyDescribeExceptionStack {
	
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
	public static void apply( Throwable excDescribe, Writer writer, String prefix, int wordWrapLimit, boolean showExceptionType ) throws IOException {
		
		// Calculate the maximum length of a message
		int maxLength = calculateMaximumLengthMessage( excDescribe, prefix, !showExceptionType );
		
		// If it's greater than our word-wrap limit, then we reduce it to our word wrap limit
		if (wordWrapLimit!=-1 && maxLength > wordWrapLimit) {
			maxLength = wordWrapLimit;
		}
		
		StringBuilder prefixBuilder = new StringBuilder();
		
		boolean firstLine = true;
		
		ExceptionToPrintIterator itr = new ExceptionToPrintIterator(excDescribe);
		Throwable e = itr.getCurrent();
		do {
			// Unless it's the very first line of the exception, we add a newline
			if( firstLine)	{ // Test for being the first line
				firstLine = false;
			} else {
				writer.append( System.lineSeparator() );
			}
			
			// Extract a message from the exception, and append it to the writer
			// with maybe prefix and suffix
			splitFromExc(e,!showExceptionType).appendNicelyWrappedLines(
				writer,
				prefixBuilder.toString(),
				suffixFor(showExceptionType, e),
				maxLength
			);
			
			prefixBuilder.append(prefix);
			
			if (itr.hasNext()) {
				e = itr.next();
			} else {
				break;
			}
			
		} while( true );
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
	private static int calculateMaximumLengthMessage( Throwable excDescribe, String prefix, boolean showExceptionTypeIfUnfriendly  ) {
		assert(excDescribe!=null);
		
		int maxLength = Integer.MIN_VALUE;
		
		int prefixCurrentLength = 0;
		
		ExceptionToPrintIterator itr = new ExceptionToPrintIterator(excDescribe);
		Throwable e = itr.getCurrent();
		do {
			SplitString splitMessage = splitFromExc(e, showExceptionTypeIfUnfriendly);
			
			// Size of message we are considering
			int msgLength = splitMessage.maxLength() + prefixCurrentLength;
			
			// Remember max length so far
			if (msgLength>maxLength) {
				maxLength = msgLength;
			}
			
			// Update current size of the prepended string
			prefixCurrentLength += prefix.length();
			
			if (itr.hasNext()) {
				e = itr.next();
			} else {
				break;
			}
		} while( true );
		return maxLength;
	}
	
	/** Extracts a message, and splits it by newline */
	private static SplitString splitFromExc( Throwable exc, boolean showExceptionTypeIfUnfriendly ) {
		return new SplitString(
			messageFromExc(exc, showExceptionTypeIfUnfriendly)
		);
	}
	
	
	/** 
	 * If there's a message it's used, otherwise the SimpleName of the exception
	 * 
	 * @param exc exception to extract a descripive "message" from
	 * @param showExceptionTypeIfUnfriendly iff TRUE the exception-class is included in the message for "unfriendly" exceptions
	 *  
	 */
	private static String messageFromExc( Throwable exc, boolean showExceptionTypeIfUnfriendly ) {
		
		// If there's no message, use the class
		if (ExceptionTypes.hasEmptyMessage(exc)) {
			return exc.getClass().getSimpleName();
		}

		// If it's a "friendly' or 'combinable' exception rely on the message to be meaningful without the exception type
		if (exc instanceof AnchorFriendlyCheckedException || exc instanceof AnchorFriendlyRuntimeException || exc instanceof AnchorCombinableException) {
			return exc.getMessage();
		}
		
		// Otherwise include both the exception type and the message, if its allowed
		if (showExceptionTypeIfUnfriendly) {
			return String.format(
				"%s: %s",
				exc.getClass().getSimpleName(),
				exc.getMessage()
			);
		} else {
			// There's no poitn showing the class-type as it will be done anyway in the suffix
			return exc.getMessage();
		}
	}
	
	/** we only have a suffix if we are showing the exception type */
	private static String suffixFor( boolean showExceptionType, Throwable e ) {
		if (showExceptionType) {
			return String.format("    (%s)",  e.getClass().getSimpleName() );
		} else {
			return "";
		}
	}
}
