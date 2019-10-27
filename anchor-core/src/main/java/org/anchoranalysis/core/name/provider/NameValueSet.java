package org.anchoranalysis.core.name.provider;

/*
 * #%L
 * anchor-core
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.value.INameValue;
import org.anchoranalysis.core.name.value.NameValue;

public class NameValueSet<T> implements Iterable<INameValue<T>>, INamedProvider<T> {

	private HashMap<String,INameValue<T>> map = new HashMap<>();

	public NameValueSet() {
		super();
	}
	
	public NameValueSet(List<? extends INameValue<T>> list) {
		super();
		
		for (INameValue<T> nmp : list) {
			map.put( nmp.getName(), nmp );
		}
	}
	
	@Override
	public Set<String> keys() {
		return map.keySet();
	}
	
	@Override
	public T getException( String name ) throws GetOperationFailedException {
		INameValue<T> item= map.get(name);
		if (item==null) {
			throw new GetOperationFailedException( String.format("Item '%s' does not exist", name));
		}
		return item.getValue();
	}


	@Override
	public T getNull(String key) {
		INameValue<T> item = map.get(key); 
		return item!=null ? item.getValue() : null;
	}
	
	@Override
	public Iterator<INameValue<T>> iterator() {
		return map.values().iterator();
	}
	
	public void add( String name, T value ) {
		INameValue<T> item = new NameValue<>(name,value);
		map.put( name, item );
	}
	
	public void add( INameValue<T> ni ) {
		map.put( ni.getName(), ni);
	}
	
	public void add( NameValueSet<T> set ) {
		for ( String key : set.keys() ) {
			try {
				add( key, set.getException(key) );
			} catch (GetOperationFailedException e) {
				// This should never occur as we always use known key-values
				assert false;
			}
		}
	}
	
	public void removeIfExists( T item ) {
		map.remove( item );
	}
		
	public int size() {
		return map.size();
	}
	
	// Maybe this doesn't work too well
	public T getArbitrary() {
		return map.get( map.keySet().iterator().next() ).getValue();
	}
}
