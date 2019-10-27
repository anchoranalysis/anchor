package ch.ethz.biol.cell.mpp.mark.contour.permutations;

/*
 * #%L
 * anchor-mpp
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


public class Histogram2D {

	private int maxSize;
	private int[][] arr;
	
	public Histogram2D( int maxSize ) {
		this.maxSize = maxSize;
		
		arr = new int[maxSize][maxSize];
		// Should be initialised to 0
	}
	
	public void add( int val1, int val2 ) {
		arr[val1][val2]++;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i<maxSize; i++) {
			for (int j=0; j<maxSize; j++) {
				
				int cnt = arr[i][j];
				
				if (cnt>0) {
					sb.append( String.format("%4d,%4d = \t%d\n",i,j,cnt) );
				}
			}
		}
		
		return sb.toString();
	}
}
