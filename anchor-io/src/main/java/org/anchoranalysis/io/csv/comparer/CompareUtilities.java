package org.anchoranalysis.io.csv.comparer;

/*
 * #%L
 * anchor-io
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


import java.io.PrintStream;

import org.anchoranalysis.io.csv.reader.CSVReaderException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

class CompareUtilities {


	public static void checkZeroRows( boolean rejectZeroRows, String[] lines1, String[] lines2 ) throws CSVReaderException {
		if (!rejectZeroRows) {
			return;
		}
		
		if (lines1==null || lines2==null) {
			throw new CSVReaderException("At least one input csv file has zero rows");
		}
	}
	
	
	/**
	 * Are arrays equals?
	 * 
	 * @param lines1
	 * @param lines2 
	 * @param ignoreFirstNumColumns
	 * @return
	 */
	public static boolean areArraysEqual( String[] lines1, String[] lines2, int ignoreFirstNumColumns ) {
		
		if (ignoreFirstNumColumns>0) {

			if (lines1==null) {
				return lines2==null;
			}
			if (lines2==null) {
				return lines1==null;
			}
			
			if (lines1.length!=lines2.length) {
				return false;
			}
			
			int maxInd = Math.max( lines1.length - ignoreFirstNumColumns, 0 );
			
			for( int i=ignoreFirstNumColumns; i<maxInd; i++ ) {
				if (!lines1[i].equals(lines2[i])) {
					return false;
				}
			}
			return true;
			
		} else {
			// The simple case where we don't ignore any columns
			return ArrayUtils.isEquals(lines1, lines2);
		}
	}


	
	/**
	 * Prints two lines (represented by string arrays) to the screen, ensuring that each array item is 
	 * presented in vertical columns
	 * 
	 * Default delimeter of "  "
	 */
	public static void printTwoLines( PrintStream messageStream, String[] line1, String line2[] ) {
		printTwoLines(messageStream,line1,line2,"  ");
	}
	

	/**
	 * Prints two lines (represented by string arrays) to the screen, ensuring that each array item is 
	 * presented in vertical columns
	 * 
	 * @param messageStream if non-equal, additional explanation messages are printed here
	 * @param line1 an array of strings for first line
	 * @param line2 an array of strings for second line
	 * @param delimter a string that separates each column
	 */
	public static void printTwoLines( PrintStream messageStream, String[] line1, String line2[], String delimeter ) {
		
		StringBuilder out1 = new StringBuilder();
		StringBuilder out2 = new StringBuilder();
		
		if( line1.length!=line2.length ) {
			throw new IllegalArgumentException("line1 and line2 have a different number of elements");
		}
		for( int i=0; i<line1.length; i++) {
			
			if (i!=0) {
				// If not the first item
				// Write out the delimter
				out1.append(delimeter);
				out2.append(delimeter);
			}
			
			String elem1 = line1[i];
			String elem2 = line2[i];

			// Write out both elements, padding whichever is shorter
			int maxLength = Integer.max( elem1.length(), elem2.length() );
			out1.append( StringUtils.rightPad(elem1, maxLength) );
			out2.append( StringUtils.rightPad(elem2, maxLength) );
		}
		
		messageStream.println( out1.toString() );
		messageStream.println( out2.toString() );
	}
	
}
