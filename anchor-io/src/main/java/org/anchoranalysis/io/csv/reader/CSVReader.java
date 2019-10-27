package org.anchoranalysis.io.csv.reader;

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


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;


// Reads a CSV File
public class CSVReader {

	private FileReader fileReader;
	private BufferedReader bufferedReader;

	private String[] headers;
	
	private String regExSeperator;
	
	private boolean firstLineHeaders;
	
	private boolean quotedStrings;
	
	public class OpenedCSVFile implements AutoCloseable {

		private int numCols = -1;

		public boolean hasHeaders() {
			return firstLineHeaders;
		}
		
		public String[] getHeaders() {
			return headers;
		}
		
		// Returns null when finished
		public String[] readLine() throws IOException {
			
			String line = bufferedReader.readLine();
			
			if (line==null) {
				return null;
			}
			
			String[] tokenized = line.split(regExSeperator);
			
			
			if (numCols==-1) {
				numCols = tokenized.length;
			}
			
			if (tokenized.length!=numCols) {
				throw new IOException("Incorrect number of columns for line");
			}
			
			maybeRemoveQuotes(tokenized);
			
			return tokenized;
		}
		
		public void setNumCols(int numCols) {
			this.numCols = numCols;
		}

		@Override
		public void close() throws IOException {
			if (fileReader!=null) {
				fileReader.close();
				fileReader = null;
			}
			
		}

	}
	
	public CSVReader( String regExSeperator, boolean firstLineHeaders ) {
		this(regExSeperator, firstLineHeaders, false);
	}
	
	
	public CSVReader( String regExSeperator, boolean firstLineHeaders, boolean quotedStrings ) {
		this.firstLineHeaders = firstLineHeaders;
		this.regExSeperator = regExSeperator;
		this.quotedStrings = quotedStrings;
	}
	
	/**
	 * Opens a CSV for reading.
	 * 
	 * @param filePath path to file
	 * @return the opened-file (that must eventually be closed)
	 * @throws IOException
	 */
	public OpenedCSVFile read( Path filePath ) throws IOException {
		fileReader = new FileReader(filePath.toFile());
		
		this.bufferedReader = new BufferedReader( fileReader );

		OpenedCSVFile fileOut = new OpenedCSVFile();
		
		if (firstLineHeaders) {
			String line = bufferedReader.readLine();
			
			if (line==null) {
				throw new IOException("No header line found");
			}
			
			headers = line.split(regExSeperator);
			fileOut.setNumCols( headers.length );
		}
		
		return fileOut;
	}
	
	private void maybeRemoveQuotes( String[] arr ) {
		
		if (!quotedStrings) {
			// Exit early if we don't supported quoted strings
			return;
		}
		
		for( int i=0; i<arr.length; i++) {
			arr[i] = maybeRemoveQuotes( arr[i] );
		}
	}
	
	private static String maybeRemoveQuotes( String s ) {
		if (s.length()<=2) {
			return s;
		}
		
		if (s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length()-1 );
		} else {
			return s;
		}
	}	
}
