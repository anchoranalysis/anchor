package org.anchoranalysis.io.bean.input;

/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Filters all the input objects so that only those with descriptive-names containing a particular
 * string are accepted.
 * 
 * If *contains* is empty, all objects match
 * 
 * @author FEEHANO
 *
 * @param <T>
 */
public class FilterDescriptiveName<T extends InputFromManager> extends InputManager<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private InputManager<T> input;
	
	@BeanField @AllowEmpty
	private String equals = "";
	// END BEAN PROPERTIES
	
	@Override
	public List<T> inputObjects(InputContextParams inputContext, ProgressReporter progressReporter)
			throws IOException, DeserializationFailedException {

		// Existing collection 
		List<T> in = input.inputObjects(inputContext, progressReporter);
		
		// If no string is specified, just pass pack the entire iterator
		if (equals.isEmpty()) {
			return in;
		}
		
		applyFilter(in);
		
		return in;
	}
	
	private void applyFilter( List<T> in ) {
		
		Iterator<T> itr = in.listIterator();
		while(itr.hasNext()) {
			T item = itr.next();
			
			if (!item.descriptiveName().equals(equals)) {
				itr.remove();
			}
		}
	}

	public InputManager<T> getInput() {
		return input;
	}

	public void setInput(InputManager<T> input) {
		this.input = input;
	}

	public String getEquals() {
		return equals;
	}

	public void setEquals(String equals) {
		this.equals = equals;
	}

}
