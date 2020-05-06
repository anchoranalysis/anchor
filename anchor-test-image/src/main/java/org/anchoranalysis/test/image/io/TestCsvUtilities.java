package org.anchoranalysis.test.image.io;

/*-
 * #%L
 * anchor-test-image
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.io.csv.reader.CSVReader.OpenedCSVFile;

public class TestCsvUtilities {

	/**
	 * Checks if a particular string can be found in any cell of a CSV file
	 * 
	 * @param file a csv-file to search through its cells
	 * @param str string to search for in any of the cells in the CsvFile (it may also be a substring of the cell)
	 * @return TRUE if at least one instance of str is found, FALSE otherwise
	 * @throws IOException if something goes wrong reading the csv-file
	 */
	public static boolean doesCsvFileContainString( OpenedCSVFile file, String str ) throws IOException {
		
		String[] line;
		while ( (line = file.readLine())!=null ) {
			
			for( int i=0; i<line.length; i++) {
				
				if( line[i].contains(str)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
