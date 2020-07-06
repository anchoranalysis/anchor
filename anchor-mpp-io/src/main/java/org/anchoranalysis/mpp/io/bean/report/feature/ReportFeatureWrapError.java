package org.anchoranalysis.mpp.io.bean.report.feature;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.bean.report.feature.ReportFeature;

public class ReportFeatureWrapError extends ReportFeatureForSharedObjects {

	// START BEAN PROPERTIES
	@BeanField
	private ReportFeature<MPPInitParams> item;
	
	@BeanField
	private String message;
	// END BEAN PROPERTIES
		
	@Override
	public boolean isNumeric() {
		return item.isNumeric();
	}

	@Override
	public String genTitleStr() throws OperationFailedException {
		return item.genTitleStr();
	}

	@Override
	public String genFeatureStrFor(MPPInitParams obj, Logger logger)
			throws OperationFailedException {
		try {
			return item.genFeatureStrFor(obj, logger);
		} catch (OperationFailedException e) {
			return message;
		}
	}

	public ReportFeature<MPPInitParams> getItem() {
		return item;
	}

	public void setItem(ReportFeature<MPPInitParams> item) {
		this.item = item;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}



}
