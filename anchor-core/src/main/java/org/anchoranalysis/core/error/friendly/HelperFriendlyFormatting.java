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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

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
		while( e!=null ) {

			// Skip any exception with any empty message
			if (e.getMessage()==null || e.getMessage().isEmpty()) {
				e = e.getCause();
				continue;
			}
			
			// Unless it's the very first line of the exception, we add a newline
			if( e!=excDescribe)	{ // Test for being the first line
				writer.append( System.lineSeparator() );
			}
			
			SplitString split = new SplitString(e.getMessage());
			
			// we only have a suffix if we are showing the exception type
			String suffix = showExceptionType ? String.format("    (%s)",  e.getClass().getSimpleName() ) : "";
			
			split.appendNicelyWrappedLines(writer, prefixCurrent, suffix, maxLength);
			
			// Move to next nested exception
			e = e.getCause();
			prefixCurrent = prefixCurrent + prefix;
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
		while( e!=null ) {
			
			// Skip any exception with any empty message
			if (e.getMessage()==null || e.getMessage().isEmpty()) {
				e = e.getCause();
				continue;
			}
			
			SplitString splitMessage = new SplitString(e.getMessage());
			
			// Size of message we are considering
			int msgLength = splitMessage.maxLength() + prefixCurrentLength;
			
			if (msgLength>maxLength) {
				maxLength = msgLength;
			}
			
			// Move to next nested exception
			e = e.getCause();
			
			// Current size of the prepended string
			prefixCurrentLength += prefix.length();
		}
		return maxLength;
	}
	
	
	/**
	 * A string split by newlines
	 * 
	 * @author Owen Feehan
	 *
	 */
	private static class SplitString {
		
		private String[] lines;
		
		/**
		 * Constructor: create from an unsplit string
		 * 
		 * @param unsplitStr a string with 0 or more lines
		 */
		public SplitString( String unsplitStr ) {
			lines = splitString(unsplitStr);
		}
		
		/**
		 * Splits by newline
		 * 
		 * @param unsplitStr string with 0 or more newline characters
		 * @return an array with lines of the string (minus newlines)
		 */
		private static String[] splitString( String unsplitStr ) {
			return unsplitStr.split("\\r?\\n");
		}
		
		/**
		 * Find the maximum-length of any single line
		 * @return the maximum length of any single line
		 */
		public int maxLength() {
			
			assert( lines.length>0 );
			
			int max = Integer.MIN_VALUE;
			
			for( int i=0; i<lines.length; i++) {
				int curLen = lines[i].length();
				
				if (curLen>max) {
					max=curLen;
				}
			}
			
			return max;
		}
		
		/**
		 * Constructs a nicely formatted version of each line for outputting of the format
		 * 
		 * prefix+line[index]+suffix
		 * 
		 * for each index in the array. prefix+line[index] are forced to be of constant size by appending whitespace.
		 *
		 * @param writer where the lines are written to
		 * @param prefix a prefix placed before every line
		 * @param suffix a suffix placed at the end of only the first line
		 * @param wrapLengthPrefixPlusLine If length of prefix+line is < wrapLengthPrefixPlusLine, white space is appended.  If length is greater, word-wrapping occurs. -1 disables
		 * @throws IOException if an I/O error occurs
		 */
		public void appendNicelyWrappedLines( Writer writer, String prefix, String suffix, int wrapLengthPrefixPlusLine ) throws IOException {
			
			boolean doWrapping = wrapLengthPrefixPlusLine!=-1;
			
			for( int i=0; i<lines.length; i++) {
				
				String line = lines[i];
				if (i==0) {
					// We have a suffix on the first line
					// We only need the wrapping if we the suffix
					appendNicelyWrappedLine(writer, line, prefix, suffix, doWrapping, doWrapping, wrapLengthPrefixPlusLine);
				} else {
					// new line
					writer.append( System.lineSeparator() );
					
					// But no suffix on any other line
					appendNicelyWrappedLine(writer, line, prefix, "", doWrapping, false, wrapLengthPrefixPlusLine);
				}
			}
		}
		
		

		
		private void appendNicelyWrappedLine( Writer writer, String line, String prefix, String suffix, boolean wrapTooLongLines, boolean padTooShortFirstLine, int fixedSizeForWrappingPadding ) throws IOException {

			// The message combined with its prepend
			String combinedMessage = prefix + line;
		
			// If our line than our wrapLength, we need to split up into smaller individual chunks
			if (wrapTooLongLines && combinedMessage.length() > fixedSizeForWrappingPadding) {
				
				appendTooLargeLineBySplitting( writer, line, prefix, suffix, fixedSizeForWrappingPadding, padTooShortFirstLine );
				return;
				
			}
			
			writer.write(combinedMessage);
			
			if (padTooShortFirstLine && combinedMessage.length() < fixedSizeForWrappingPadding) {
				// Add whitespace to give table-effect
				int whiteSpaceLength = fixedSizeForWrappingPadding-combinedMessage.length();
				writer.append( StringUtils.repeat(" ", whiteSpaceLength) );
			}

			writer.append(suffix);
		}
		
		
		private void appendTooLargeLineBySplitting( Writer writer, String line, String prefix, String suffix, int fixedSizeForWrappingPadding, boolean padTooShortFirstLine ) throws IOException {
			// What's the max size of our line alone
			int wrapLengthLineAlone = fixedSizeForWrappingPadding - prefix.length();
			String maybeNewlined = WordUtils.wrap(line, wrapLengthLineAlone, System.lineSeparator(), true);
			
			String[] linesSplit = splitString(maybeNewlined);
			
			for( int i=0; i<linesSplit.length; i++) {
				String s = linesSplit[i];
				
				// Only allow the real suffix to be written out for the first-line, and that's the only time we need to pad
				if (i==0) {
					appendNicelyWrappedLine( writer, s, prefix, suffix, false, padTooShortFirstLine, fixedSizeForWrappingPadding );
				} else {
					// add a new line
					writer.append( System.lineSeparator() );
					appendNicelyWrappedLine( writer, s, prefix, "", false, false, fixedSizeForWrappingPadding );
				}
			}
		}
		
	}
}
