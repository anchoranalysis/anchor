package org.anchoranalysis.io.generator.sequence;

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


import java.util.HashMap;

// Remembers the last number used in a sequence, associated with a particular string
public class SequenceMemory {

	private HashMap<String,Integer> map = new HashMap<>();

	public int lastIndex( String key ) {
		
		Integer index = map.get(key);
		
		if (index==null) {
			index = Integer.valueOf(0);
			map.put(key, index );
		}
		
		return index;
	}
	
	// Increments the index and returns the new incremented index
	public int indexIncr( String key ) {
		int ind = lastIndex(key);
		ind++;
		updateIndex(key,ind);
		return ind;
	}
	
	public void updateIndex( String key, int indexNew ) {
		
		map.remove(key);
		Integer index = Integer.valueOf(indexNew);
		map.put(key, index);
	}
	
}
