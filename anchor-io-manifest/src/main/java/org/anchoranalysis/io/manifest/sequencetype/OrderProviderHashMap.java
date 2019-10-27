package org.anchoranalysis.io.manifest.sequencetype;

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
import java.util.Set;

import org.anchoranalysis.core.index.container.IOrderProvider;

public class OrderProviderHashMap implements IOrderProvider {
	
	// Maps each index to its bundle index
	private HashMap<String,Integer> indexHash;
	
	public OrderProviderHashMap() {
		indexHash = new HashMap<>();
		
		
	}
	
	public void addIntegerSet( Set<Integer> set ) {

		// We iterate over every element assigning them
		// the correct bundle index
		int counter = 0;
		for ( Integer index : set ) {
			indexHash.put( String.valueOf(index), counter++);
		}
	}
	
	public void addStringSet( Set<String> set ) {
		
		// We iterate over every element assigning them
		// the correct bundle index
		int counter = 0;
		for ( String index : set ) {
			indexHash.put( index, counter++);
		}
	}

	@Override
	public int order(String index) {
		Integer o = indexHash.get(index);
		if (o==null) {
			throw new IndexOutOfBoundsException();
		}
		return o;
	}

	
	
}