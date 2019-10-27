package org.anchoranalysis.image.io.bean.chnl.map;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.io.bean.input.ImgChnlMapEntry;
import org.anchoranalysis.bean.error.BeanDuplicateException;

// THIS PROBABLY CAN'T BE DUPLICATED.... TODO let's get rid of it
//
// This is customly-defined, as it uses map-stuff
public class ImgChnlMap extends AnchorBean<ImgChnlMap> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1213727015932422224L;
	
	// START BEAN PROPERTIES
	private Map<String,ImgChnlMapEntry> map = new HashMap<>();
	// END BEAN PROPERTIES

	@Override
	public void checkMisconfigured( BeanInstanceMap defaultInstances ) throws BeanMisconfiguredException {
		super.checkMisconfigured( defaultInstances );
		for( ImgChnlMapEntry entry : map.values() ) {
			if (entry!=null) {
				entry.checkMisconfigured( defaultInstances );
			}
		}
	}
	
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
	
	public int getException( String name ) {
		int ind = get(name); 
		if (ind!=-1) {
			return ind;
		} else {
			throw new IndexOutOfBoundsException( String.format("No channel index for '%s' in imgChnlMap", name) );
		} 
	}

	
	public static class CreateImgChnlMapFromEntries implements IObjectBridge<List<ImgChnlMapEntry>,ImgChnlMap> {

		@Override
		public ImgChnlMap bridgeElement(List<ImgChnlMapEntry> list)
				throws GetOperationFailedException {
			
			ImgChnlMap beanOut = new ImgChnlMap();
	    	
	    	for( ImgChnlMapEntry entry : list ) {
	    		beanOut.add( entry );
	    	}
	    	
    	    return beanOut;		
		}
	}

	// We replace the default behaviour
	@Override
	public ImgChnlMap duplicateBean() throws BeanDuplicateException {
		ImgChnlMap out = new ImgChnlMap();
		for( ImgChnlMapEntry entry : map.values() ) {
			out.add(entry.duplicateBean());
		}
		return out;
	}

}
