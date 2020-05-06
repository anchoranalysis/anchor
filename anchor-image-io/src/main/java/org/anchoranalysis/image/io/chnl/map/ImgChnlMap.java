package org.anchoranalysis.image.io.chnl.map;

import java.util.Collection;

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


import java.util.LinkedHashMap;
import java.util.Set;

import org.anchoranalysis.image.io.bean.chnl.map.ImgChnlMapEntry;

/**
 * A map of image channels to indices. 
 * 
 * <p>It is vital that the insertion order is preserved, so a LinkedHashMap or similar should be used</p>
 * 
 * This bean has a custom-factory
 * @author owen
 *
 */
public class ImgChnlMap {
	
	private LinkedHashMap<String,ImgChnlMapEntry> map = new LinkedHashMap<>();

	public void add( ImgChnlMapEntry entry ) {
		map.put( entry.getName(), entry );
	}
	
	public int get( String name ) {
		ImgChnlMapEntry entry = map.get(name);
		if (entry!=null) {
			return entry.getIndex();
		} else {
			return -1;
		}
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}
	
	public Collection<ImgChnlMapEntry> entryCollection() {
		return map.values();
	}
	
	public int getException( String name ) {
		int ind = get(name); 
		if (ind!=-1) {
			return ind;
		} else {
			throw new IndexOutOfBoundsException( String.format("No channel index for '%s' in imgChnlMap", name) );
		} 
	}
}
