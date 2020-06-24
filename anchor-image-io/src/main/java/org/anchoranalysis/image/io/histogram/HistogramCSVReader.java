package org.anchoranalysis.image.io.histogram;

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


import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramArray;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine.ReadByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderException;

public class HistogramCSVReader {

	public static Histogram readHistogramFromFile( Path filePath ) throws CSVReaderException {
				
		Map<Integer,Integer> map = new HashMap<>();
		
		try( ReadByLine reader = CSVReaderByLine.open(filePath)) {
			reader.read(
				(line, firstLine) -> addLineToMap( map, line )
			);
		}
		
		return histogramFromMap(map);
	}
	
	private static void addLineToMap( Map<Integer,Integer> map, String[] line ) throws OperationFailedException {
		
		float binF = Float.valueOf(line[0]);
		int bin = (int) binF;
		
		if (binF!=bin) {
			throw new OperationFailedException( String.format("Bin-value of %f is not integer.",binF) );
		}
		
		float countF = Float.valueOf(line[1]);
		int count = (int) countF;
		
		if (countF!=count) {
			throw new OperationFailedException( String.format("Count-value of %f is not integer.",countF) );
		}
		
		if (map.containsKey(bin)) {
			throw new OperationFailedException( String.format("There are multiple bins of value %d",bin) );
		}
		
		map.put(bin, count);
	}
	
	// Maximum-value
	private static int maxVal( Set<Integer> set ) {
		
		Integer max = null;
		for( Integer i : set ) {
			if (max==null || i>max ) {
				max = i;
			}
		}
		return max;
	}
	
	
	private static int guessMaxHistVal( int maxBinVal ) {
		if (maxBinVal<=255) {
			return 255;
		} else {
			return 65535;
		}
	}
	
	private static Histogram histogramFromMap( Map<Integer,Integer> map ) {

		// We get the highest-intensity value from the map
		int maxCSVVal = maxVal(map.keySet());
		
		// We guess the upper limit of the histogram to match an unsigned 8-bit or 16-bit image
		int maxHistVal = guessMaxHistVal(maxCSVVal);
		
		Histogram hist = new HistogramArray(maxHistVal);
		
		for( Integer bin : map.keySet() ) {
			Integer count = map.get(bin);
			hist.incrValBy(bin, count);
		}
		return hist;
	}
}
