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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Uses one input-manager normally, but a different one if in debug mode
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 */
public class BranchIfDebug<T extends InputFromManager> extends InputManager<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private InputManager<T> input;
	
	// If set to null and we are in debug mode, we take the first item from the normal input
	@BeanField @Optional
	private InputManager<T> inputDebug;
	// END BEAN PROPERTIES
	
	@Override
	public List<T> inputObjects(InputContextParams inputContext,
			ProgressReporter progressReporter)
			throws IOException, DeserializationFailedException {

		if (inputContext.isDebugMode()) {
			if (inputDebug==null) {
				// We pick the first
				Iterator<T> all = input.inputObjects(inputContext, progressReporter).iterator();
				T firstItem = all.next();
				return Collections.singletonList(firstItem);
			}
			
			return inputDebug.inputObjects(inputContext, progressReporter);
		}
		return input.inputObjects(inputContext, progressReporter);
	}

	public InputManager<T> getInput() {
		return input;
	}


	public void setInput(InputManager<T> input) {
		this.input = input;
	}


	public InputManager<T> getInputDebug() {
		return inputDebug;
	}


	public void setInputDebug(InputManager<T> inputDebug) {
		this.inputDebug = inputDebug;
	}



}
