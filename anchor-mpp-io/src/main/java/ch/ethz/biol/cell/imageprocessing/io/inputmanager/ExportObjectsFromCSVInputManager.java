package ch.ethz.biol.cell.imageprocessing.io.inputmanager;

/*
 * #%L
 * anchor-mpp-io
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.params.InputContextParams;
import org.anchoranalysis.mpp.io.bean.input.MultiInputManager;
import org.anchoranalysis.mpp.io.input.MultiInput;

import ch.ethz.biol.cell.imageprocessing.io.inputobject.namedchnlcollection.ExportObjectsFromCSVInputObject;

// An input stack
public class ExportObjectsFromCSVInputManager extends InputManager<ExportObjectsFromCSVInputObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private MultiInputManager input;
	
	@BeanField
	private FilePathGenerator appendCSV;
	// END BEAN PROPERTIES

	@Override
	public List<ExportObjectsFromCSVInputObject> inputObjects(
			InputContextParams inputContext, ProgressReporter progressReporter)
			throws IOException, DeserializationFailedException {
		
		Iterator<MultiInput> itr = input.inputObjects(inputContext, progressReporter).iterator();

		List<ExportObjectsFromCSVInputObject> out = new ArrayList<>();
		
		while( itr.hasNext() ) {
			MultiInput inputObj = itr.next();
			
			Path csvFilePathOut = appendCSV.outFilePath(inputObj.pathForBinding(), inputContext.isDebugMode() );
			out.add( new ExportObjectsFromCSVInputObject(inputObj, csvFilePathOut) );
		}
		
		return out;
	}

	public FilePathGenerator getAppendCSV() {
		return appendCSV;
	}

	public void setAppendCSV(FilePathGenerator appendCSV) {
		this.appendCSV = appendCSV;
	}


	public MultiInputManager getInput() {
		return input;
	}


	public void setInput(MultiInputManager input) {
		this.input = input;
	}
}
