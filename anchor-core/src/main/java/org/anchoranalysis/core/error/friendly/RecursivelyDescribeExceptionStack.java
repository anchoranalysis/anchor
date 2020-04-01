package org.anchoranalysis.core.error.friendly;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Generates a nice string representation of an Exception and its causes according to certain rules.
 * 
 * @author owen
 *
 */
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
		int maxLength = calculateMaximumLengthMessage( excDescribe, prefix );
		
		// If it's greater than our word-wrap limit, then we reduce it to our word wrap limit
		if (wordWrapLimit!=-1 && maxLength > wordWrapLimit) {
			maxLength = wordWrapLimit;
		}
		
		String prefixCurrent = "";
		
		Throwable e = excDescribe;
		boolean firstLine = true;
		
		Iterator<Throwable> itr = new ExceptionToPrintIterator(excDescribe);
		while( itr.hasNext() ) {

			e = itr.next();

			
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
		
		Iterator<Throwable> itr = new ExceptionToPrintIterator(excDescribe);
		while( itr.hasNext() ) {

			Throwable e = itr.next();
			
			SplitString splitMessage = splitFromExc(e);
			
			// Size of message we are considering
			int msgLength = splitMessage.maxLength() + prefixCurrentLength;
			
			// Remember max length so far
			if (msgLength>maxLength) {
				maxLength = msgLength;
			}
			
			// Update current size of the prepended string
			prefixCurrentLength += prefix.length();
		}
		return maxLength;
	}
	
	/** Extracts a message, and splits it by newline */
	private static SplitString splitFromExc( Throwable exc ) {
		return new SplitString(
			messageFromExc(exc)
		);
	}
	
	
	/** If there's a message it's used, otherwise the SimpleName of the exception */
	private static String messageFromExc( Throwable exc ) {
		if (ExceptionTypes.hasEmptyMessage(exc)) {
			return exc.getClass().getSimpleName();
		} else {
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
