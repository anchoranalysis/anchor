package org.anchoranalysis.mpp.io.bean.input;

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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.StackInputBase;
import org.anchoranalysis.io.bean.filepath.generator.FilePathGenerator;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.params.InputContextParams;
import org.anchoranalysis.mpp.io.input.MultiInput;

import static org.anchoranalysis.mpp.io.bean.input.AppendHelper.*;

// An input stack
public class MultiInputManager extends MultiInputManagerBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private String inputName = "input_image";
	
	@BeanField
	private InputManager<? extends StackInputBase> input;
	
	@BeanField @DefaultInstance
	private RasterReader rasterReader;	// For reading appended files
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppendStack = new ArrayList<>();
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppendCfg = new ArrayList<>();
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotation = new ArrayList<>();		// Uses both accepted and rejected
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotationAcceptedOnly = new ArrayList<>();		// Uses both accepted only
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotationRejectedOnly = new ArrayList<>();		// Uses both accepted rejectedonly
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppendObjMaskCollection = new ArrayList<>();
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppendKeyValueParams = new ArrayList<>();
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppendHistogram = new ArrayList<>();
	
	@BeanField @Optional
	private List<NamedBean<FilePathGenerator>> listAppendFilePath = new ArrayList<>();
	// END BEAN PROPERTIES

	@Override
	public List<MultiInput> inputObjects(InputContextParams inputContext, ProgressReporter progressReporter)
			throws FileNotFoundException, IOException,
			DeserializationFailedException {
		
		List<MultiInput> outList = new ArrayList<>();
		
		Iterator<? extends StackInputBase> itr = input.inputObjects(inputContext, progressReporter).iterator();
		
		while (itr.hasNext()) {
			StackInputBase mainStack = itr.next();
			
			MultiInput inputObject = new MultiInput(inputName,mainStack);
			appendFromLists(inputObject, inputContext.isDebugMode());
			
			outList.add( inputObject );
		}
	
		return outList;
	}
	
	private void appendFromLists( MultiInput inputObject, boolean doDebug ) {
		
		appendStack( listAppendStack, inputObject, doDebug, rasterReader );
		
		appendFromCfgLists( inputObject, doDebug );
				
		appendObjMaskCollection(listAppendObjMaskCollection, inputObject, doDebug );
		appendKeyValueParams( listAppendKeyValueParams, inputObject, doDebug );
		appendHistogram( listAppendHistogram, inputObject, doDebug );
		appendFilePath( listAppendFilePath, inputObject, doDebug );
	}
	
	private void appendFromCfgLists( MultiInput inputObject, boolean doDebug ) {
		appendCfg(
			listAppendCfg,
			inputObject,
			doDebug
		);
		appendCfgFromAnnotation( listAppendCfgFromAnnotation, inputObject, true, true, doDebug );
		appendCfgFromAnnotation( listAppendCfgFromAnnotationAcceptedOnly, inputObject, true, false, doDebug );
		appendCfgFromAnnotation( listAppendCfgFromAnnotationRejectedOnly, inputObject, false, true, doDebug );
	}
			
	public InputManager<? extends StackInputBase> getInput() {
		return input;
	}

	public void setInput(InputManager<? extends StackInputBase> input) {
		this.input = input;
	}

	public List<NamedBean<FilePathGenerator>> getListAppendStack() {
		return listAppendStack;
	}

	public void setListAppendStack(
			List<NamedBean<FilePathGenerator>> listAppendStack) {
		this.listAppendStack = listAppendStack;
	}

	public List<NamedBean<FilePathGenerator>> getListAppendCfg() {
		return listAppendCfg;
	}

	public void setListAppendCfg(List<NamedBean<FilePathGenerator>> listAppendCfg) {
		this.listAppendCfg = listAppendCfg;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public List<NamedBean<FilePathGenerator>> getListAppendObjMaskCollection() {
		return listAppendObjMaskCollection;
	}

	public void setListAppendObjMaskCollection(
			List<NamedBean<FilePathGenerator>> listAppendObjMaskCollection) {
		this.listAppendObjMaskCollection = listAppendObjMaskCollection;
	}

	public List<NamedBean<FilePathGenerator>> getListAppendCfgFromAnnotation() {
		return listAppendCfgFromAnnotation;
	}

	public void setListAppendCfgFromAnnotation(
			List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotation) {
		this.listAppendCfgFromAnnotation = listAppendCfgFromAnnotation;
	}

	public List<NamedBean<FilePathGenerator>> getListAppendKeyValueParams() {
		return listAppendKeyValueParams;
	}

	public void setListAppendKeyValueParams(
			List<NamedBean<FilePathGenerator>> listAppendKeyValueParams) {
		this.listAppendKeyValueParams = listAppendKeyValueParams;
	}

	public List<NamedBean<FilePathGenerator>> getListAppendCfgFromAnnotationAcceptedOnly() {
		return listAppendCfgFromAnnotationAcceptedOnly;
	}

	public void setListAppendCfgFromAnnotationAcceptedOnly(
			List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotationAcceptedOnly) {
		this.listAppendCfgFromAnnotationAcceptedOnly = listAppendCfgFromAnnotationAcceptedOnly;
	}

	public List<NamedBean<FilePathGenerator>> getListAppendCfgFromAnnotationRejectedOnly() {
		return listAppendCfgFromAnnotationRejectedOnly;
	}

	public void setListAppendCfgFromAnnotationRejectedOnly(
			List<NamedBean<FilePathGenerator>> listAppendCfgFromAnnotationRejectedOnly) {
		this.listAppendCfgFromAnnotationRejectedOnly = listAppendCfgFromAnnotationRejectedOnly;
	}

	public List<NamedBean<FilePathGenerator>> getListAppendHistogram() {
		return listAppendHistogram;
	}

	public void setListAppendHistogram(
			List<NamedBean<FilePathGenerator>> listAppendHistogram) {
		this.listAppendHistogram = listAppendHistogram;
	}

	public RasterReader getRasterReader() {
		return rasterReader;
	}

	public void setRasterReader(RasterReader rasterReader) {
		this.rasterReader = rasterReader;
	}

	public List<NamedBean<FilePathGenerator>> getListAppendFilePath() {
		return listAppendFilePath;
	}

	public void setListAppendFilePath(
			List<NamedBean<FilePathGenerator>> listAppendFilePath) {
		this.listAppendFilePath = listAppendFilePath;
	}
}
