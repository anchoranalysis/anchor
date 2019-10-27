package org.anchoranalysis.io.bean.color.generator;

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


import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.RGBColorBean;

// If the list is too small for size, then we extend with the final item
public class ColorListGenerator extends ColorSetGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private List<RGBColorBean> list;
	// END BEAN PROPERTIES
	
	@Override
	public ColorList genColors(int num_colors) throws OperationFailedException {
		
		try {
			ColorList out = new ColorList();
			for( RGBColorBean item : list ) {
				out.add( item.duplicateBean().rgbColor() );
			}
			
			while ( out.size()<num_colors ) {
				out.add( list.get( list.size()-1 ).duplicateBean().rgbColor() );
			}
			
			return out;
			
		} catch (BeanDuplicateException e) {
			throw new OperationFailedException(e);
		}

	}

	public List<RGBColorBean> getList() {
		return list;
	}

	public void setList(List<RGBColorBean> list) {
		this.list = list;
	}

}
