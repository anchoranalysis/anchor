package org.anchoranalysis.annotation.io.bean.input;

/*
 * #%L
 * anchor-annotation
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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.annotation.io.bean.strategy.AnnotatorStrategy;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.image.io.input.StackInputBase;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.params.InputContextParams;


public class AnnotationInputManager<T extends StackInputBase, S extends AnnotatorStrategy> extends InputManager<AnnotationWithStrategy<S>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private InputManager<T> input;

	
	@BeanField
	private S annotatorStrategy;
	// END BEAN PROPERTIES

	
	@Override
	public List<AnnotationWithStrategy<S>> inputObjects(InputContextParams inputContext, ProgressReporter progressReporter)
			throws FileNotFoundException, IOException,
			DeserializationFailedException {

		try( ProgressReporterMultiple prm = new ProgressReporterMultiple(progressReporter, 2)) {
			
			List<T> inputs = input.inputObjects(
				inputContext,
				new ProgressReporterOneOfMany(prm)
			);
		
			prm.incrWorker();
		
			List<AnnotationWithStrategy<S>> outList = createListInput(
				inputs,
				new ProgressReporterOneOfMany(prm)
			);
			prm.incrWorker();
			
			return outList;
		}
	}
	
	
	private List<AnnotationWithStrategy<S>> createListInput( List<T> listInputObjects, ProgressReporter progressReporter ) throws IOException {
		List<AnnotationWithStrategy<S>> outList = new ArrayList<>();

		progressReporter.setMin( 0 );
		progressReporter.setMax( listInputObjects.size() );
		progressReporter.open();
		
		try {
			for(int i=0; i<listInputObjects.size(); i++) {
				
				T item = listInputObjects.get(i);
				outList.add(
					createInput(item)
				);
				
				progressReporter.update(i);
			}
		} finally {
			progressReporter.close();
		}
		
		return outList;
	}
	
	public AnnotationWithStrategy<S> createInput( StackInputBase item ) throws IOException {
		return new AnnotationWithStrategy<S>(item, annotatorStrategy);
	}
	
	public InputManager<T> getInput() {
		return input;
	}

	public void setInput(InputManager<T> input) {
		this.input = input;
	}


	public S getAnnotatorStrategy() {
		return annotatorStrategy;
	}


	public void setAnnotatorStrategy(S annotatorStrategy) {
		this.annotatorStrategy = annotatorStrategy;
	}

}
