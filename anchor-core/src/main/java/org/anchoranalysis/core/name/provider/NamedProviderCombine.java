package org.anchoranalysis.core.name.provider;

/*
 * #%L
 * anchor-image
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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NamedProviderCombine<T> implements INamedProvider<T> {

	private List<INamedProvider<T>> list = new ArrayList<>();

	@Override
	public T getException(String key) throws NamedProviderGetException {
		T item = getNull(key);
		if (item==null) {
			throw NamedProviderGetException.nonExistingItem(key);
		}
		return item;
	}
	
	@Override
	public T getNull(String key) throws NamedProviderGetException {
		
		for (INamedProvider<T> item : list) {
			
			T ret = item.getNull(key);
			
			if (ret!=null) {
				return ret;
			}
		}
		return null;
	}

	@Override
	public Set<String> keys() {

		HashSet<String> combinedList = new HashSet<>();
		
		for (INamedProvider<T> item : list) {
			combinedList.addAll( item.keys() );
		}
		
		return combinedList;
	}
	
	public void add( INamedProvider<T> key ) {
		assert( key != null );
		list.add(key);
	}


	
	
}
