package org.anchoranalysis.io.bean.input;

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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine.ReadByLine;

class CsvMatcher {

	public static Set<String> rowsFromCsvThatMatch( Path path, String match, int numRowsExpected ) throws IOException {
				
		Set<String> set = new TreeSet<>();
		
		try( ReadByLine csvFile = CSVReaderByLine.open(path, ",", true, true) ) {
		
			int cnt = processLines( csvFile, set, match );
			
			if (cnt!=numRowsExpected) {
				throw new IOException(
					String.format("Csv file must have exactly %d rows. It has %d.", numRowsExpected, cnt)
				);
			}
		}
		
		return set;
	}
	
	// Returns the number of lines processed
	private static int processLines( ReadByLine csvFile, Set<String> set, String match ) throws IOException {
		return csvFile.read(
			(line, firstLine) -> maybeAddLineToSet( set, line, match )
		);
	}
	
	private static void maybeAddLineToSet( Set<String> set, String[] line, String match ) throws OperationFailedException {
		// First column is the name
		// Second column is the value to match against
		if (line.length!=2) {
			throw new OperationFailedException(
				String.format("Row must have exactly 2 values. It has %d.", line.length)
			);
		}
		
		if (line[1].equals(match)) {
			addWithDuplicationCheck(set, line[0]);
		}
	}
	
	private static void addWithDuplicationCheck( Set<String> set, String item ) throws OperationFailedException {
		if (!set.add(item)) {
			throw new OperationFailedException(
				String.format("CSV file contains duplicated values for: %s", item)
			);
		}
	}
}
