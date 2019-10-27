package org.anchoranalysis.image.histogram;

/*-
 * #%L
 * anchor-image
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

import org.anchoranalysis.core.random.RandomNumberGenerator;

public class HistogramSampler {
	
	private int arr[];
	private int sum;
	private int maxVal;
	
	public HistogramSampler( Histogram histogram ) {
		assert(histogram!=null);
		arr = new int[histogram.size()];
		
		sum = 0;
		maxVal = histogram.getMaxBin();
		
		for( int c=0; c<arr.length; c++ ) {
			arr[c] = sum;
			sum += histogram.getCount(c);
		}
	}
	
	public int sample( RandomNumberGenerator re ) {
		int index = (int) (re.nextDouble()*sum);
		
		// We count down until we find the first item less than the sum
		for( int c=(arr.length-1); c>=0; c--) {
			if (index>=arr[c]) {
				return c;
			}
		}
		
		assert false;
		return -1;
	}
	
	public Histogram sampleMany( RandomNumberGenerator re, int numSamples ) {
		
		Histogram h = new HistogramArray(maxVal);
		for( int i=0; i<numSamples; i++) {
			h.incrVal( sample(re) );
		}
		return h;
	}
}
