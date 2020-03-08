package org.anchoranalysis.annotation.io.bean.background;

/*-
 * #%L
 * anchor-annotation-io
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

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.bean.shared.StringMap;

public class AnnotationBackgroundDefinition extends AnchorBean<AnnotationBackgroundDefinition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private String stackNameVisualOriginal;
	
	/** If non-null, maps underlying stack-name to a background */
	@BeanField @Optional
	private StringMap backgroundStackMap;
	
	/** If non-empty any stackNames (after map) containing a certain string will be ignored */
	@BeanField @AllowEmpty
	private String ignoreContains = "";
	// END BEAN PROPERTIES

	public StringMap getBackgroundStackMap() {
		return backgroundStackMap;
	}
	
	public void setBackgroundStackMap(StringMap backgroundStackMap) {
		this.backgroundStackMap = backgroundStackMap;
	}
	
	public String getStackNameVisualOriginal() {
		return stackNameVisualOriginal;
	}
	
	public void setStackNameVisualOriginal(String stackNameVisualOriginal) {
		this.stackNameVisualOriginal = stackNameVisualOriginal;
	}

	public String getIgnoreContains() {
		return ignoreContains;
	}

	public void setIgnoreContains(String ignoreContains) {
		this.ignoreContains = ignoreContains;
	}
}
