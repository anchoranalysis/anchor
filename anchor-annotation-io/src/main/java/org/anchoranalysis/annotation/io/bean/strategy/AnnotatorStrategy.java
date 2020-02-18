package org.anchoranalysis.annotation.io.bean.strategy;



import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.annotation.io.bean.background.AnnotationBackgroundDefinition;

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


import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.input.ProvidesStackInput;

public abstract class AnnotatorStrategy extends AnchorBean<AnnotatorStrategy> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private AnnotationBackgroundDefinition background;
	
	@BeanField @DefaultInstance
	private RasterReader rasterReader;
	// END BEAN PROPERTIES
	
	public abstract Path annotationPathFor( ProvidesStackInput item ) throws IOException;
	
	/** Returns a label describing the annotation, or NULL if this makes no sense */
	public abstract String annotationLabelFor( ProvidesStackInput item ) throws IOException;
	
	public abstract int weightWidthDescription();
	
	public RasterReader getRasterReader() {
		return rasterReader;
	}
	
	public void setRasterReader(RasterReader rasterReader) {
		this.rasterReader = rasterReader;
	}

	public AnnotationBackgroundDefinition getBackground() {
		return background;
	}

	public void setBackground(AnnotationBackgroundDefinition background) {
		this.background = background;
	}
}
