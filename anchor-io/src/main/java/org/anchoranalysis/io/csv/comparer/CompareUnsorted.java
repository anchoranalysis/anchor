package org.anchoranalysis.io.csv.comparer;

/*-
 * #%L
 * anchor-io
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

import static org.anchoranalysis.io.csv.comparer.CompareUtilities.*;

import java.io.IOException;

import org.anchoranalysis.io.csv.reader.CSVReader.OpenedCSVFile;

class CompareUnsorted {
	
	public boolean compareCsvFilesWithoutSorting( OpenedCSVFile file1, OpenedCSVFile file2, int ignoreFirstNumColumns, boolean rejectZeroRows ) throws IOException {
		
		boolean first = true;
		
		while( true ) {
			String[] lines1 = file1.readLine();
			String[] lines2 = file2.readLine();
			
			if (first) {
				checkZeroRows(rejectZeroRows, lines1,lines2);
				first = false;
			}
			
			if (!areArraysEqual(lines1,lines2,ignoreFirstNumColumns)) {
				return false;
			}
			
			if (lines1==null) {
				// lines2 must also be null by this point
				return true;
			}
		}		
	}
}
