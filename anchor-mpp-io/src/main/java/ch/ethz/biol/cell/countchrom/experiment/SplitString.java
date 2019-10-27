package ch.ethz.biol.cell.countchrom.experiment;

/*
 * #%L
 * anchor-mpp-io
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


public class SplitString {

	private String[] splitParts;
	
	public SplitString( String input, String regexSplit ) {
		splitParts = input.split(regexSplit);
	}
	
	public int getNumParts() {
		return splitParts.length;
	}
	
	public String getSplitPartOrEmptyString( int index ) {
		if (index<splitParts.length) {
			return splitParts[index];
		} else {
			return "";
		}
	}
	
	public String combineFirstElements( int maxNumElements, String combineSeperator ) {
		
		StringBuilder sb = new StringBuilder();
		for( int i=0; i<maxNumElements; i++) {
			
			// In case we don't have enough elements
			if (i>=splitParts.length) {
				break;
			}
			
			if (i!=0) {
				sb.append(combineSeperator);
			}
			
			sb.append( splitParts[i] );
			
		}
		//System.out.println("a " + sb.toString() + " a" );
		
		return sb.toString();
	}
}
