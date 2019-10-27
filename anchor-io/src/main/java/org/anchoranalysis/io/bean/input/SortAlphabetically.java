package org.anchoranalysis.io.bean.input;

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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Sorts the input-items in alphabetical order of their descriptiveName()
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 */
public class SortAlphabetically<T extends InputFromManager> extends InputManager<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private InputManager<T> input;
	// END BEAN PROPERITES	
	
	@Override
	public List<T> inputObjects(InputContextParams inputContext,
			ProgressReporter progressReporter) throws IOException,
			DeserializationFailedException {
		
		List<T> list = new ArrayList<>();
		
		Iterator<T> itr = input.inputObjects(inputContext, progressReporter).iterator();
		while( itr.hasNext() ) {
			list.add( itr.next() );
		}
		
		Comparator<T> comparator = new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return o1.descriptiveName().compareTo(o2.descriptiveName());
			}
		};
		Collections.sort(list, comparator);
		
		return list;
	}

	public InputManager<T> getInput() {
		return input;
	}

	public void setInput(InputManager<T> input) {
		this.input = input;
	}
}
