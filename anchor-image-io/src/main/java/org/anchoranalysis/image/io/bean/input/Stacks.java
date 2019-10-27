package org.anchoranalysis.image.io.bean.input;

/*
 * #%L
 * anchor-image-io
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
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.StackSequenceInput;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.input.FileInput;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Each file gives either:
 *   * a single stack
 *   * a time-series of stacks
 *   
 * @author Owen Feehan
 *
 */
public class Stacks extends InputManager<StackSequenceInput> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4614409393859815802L;
	
	// START BEANS
	@BeanField
	private InputManager<FileInput> fileInput;
	
	@BeanField @DefaultInstance
	private RasterReader rasterReader;
	
	@BeanField
	private boolean useLastSeriesIndexOnly;
	// END BEANS

	public Stacks() {
		
	}
	
	public Stacks( InputManager<FileInput> fileInput ) {
		this.fileInput = fileInput;
	}

	@Override
	public List<StackSequenceInput> inputObjects(InputContextParams inputContext, ProgressReporter progressReporter)
			throws FileNotFoundException, IOException, DeserializationFailedException {
		
		ArrayList<StackSequenceInput> listOut = new ArrayList<>(); 
		
		Iterator<FileInput> itrFiles = fileInput.inputObjects(inputContext, progressReporter).iterator();
		while( itrFiles.hasNext() ) {
			listOut.add( new StackCollectionFromFilesInputObject(itrFiles.next(), getRasterReader(), useLastSeriesIndexOnly ));
		}

		return listOut;
	}

	public RasterReader getRasterReader() {
		return rasterReader;
	}

	public void setRasterReader(RasterReader rasterReader) {
		this.rasterReader = rasterReader;
	}

	public boolean isUseLastSeriesIndexOnly() {
		return useLastSeriesIndexOnly;
	}

	public void setUseLastSeriesIndexOnly(boolean useLastSeriesIndexOnly) {
		this.useLastSeriesIndexOnly = useLastSeriesIndexOnly;
	}

	public InputManager<FileInput> getFileInput() {
		return fileInput;
	}

	public void setFileInput(InputManager<FileInput> fileInput) {
		this.fileInput = fileInput;
	}
}
