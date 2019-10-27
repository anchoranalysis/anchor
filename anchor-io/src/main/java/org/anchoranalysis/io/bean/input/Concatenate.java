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
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Concatenates several input-managers
 * 
 * @author Owen Feehan
 *
 * @param <T> input-object type
 */
public class Concatenate<T extends InputFromManager> extends InputManager<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8305744272662659794L;

	// START BEAN PROPERTIES
	@BeanField
	private List<InputManager<T>> list = new ArrayList<>();
	// END BEAN PROPERTIES
	
	@Override
	public List<T> inputObjects(InputContextParams inputContext,
			ProgressReporter progressReporter) throws IOException,
			DeserializationFailedException {

		try( ProgressReporterMultiple prm = new ProgressReporterMultiple(progressReporter, list.size())) {
		
			ArrayList<T> listOut = new ArrayList<>();
			
			for( InputManager<T> inputManager : list ) {
				listOut.addAll(
					inputManager.inputObjects(inputContext, new ProgressReporterOneOfMany(prm))
				);
								
				prm.incrWorker();
			}
			return listOut;
		}
	}

	public List<InputManager<T>> getList() {
		return list;
	}

	public void setList(List<InputManager<T>> list) {
		this.list = list;
	}


}
