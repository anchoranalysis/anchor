package org.anchoranalysis.mpp.io.bean.report.feature;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.provider.MarkProvider;
import org.anchoranalysis.anchor.mpp.feature.session.FeatureSessionCreateParamsMPPSingle;
import org.anchoranalysis.anchor.mpp.mark.Mark;

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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;

public class ReportFeatureOnMark extends ReportFeatureForMPP {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private MarkProvider markProvider;
	// END BEAN PROPERTIES

	@Override
	public boolean isNumeric() {
		return true;
	}
	
	@Override
	public String genFeatureStrFor(MPPInitParams so, LogErrorReporter logger)
			throws OperationFailedException {

		// Maybe we should duplicate the providers?
		try {
			init( so, logger );
			markProvider.initRecursive( so, logger );
		} catch (InitException e) {
			throw new OperationFailedException(e);
		}
		
		
		Mark mark;
		try {
			mark = markProvider.create();
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
		
					
		try {
			FeatureSessionCreateParamsMPPSingle session = createSession();
			
			double val = session.calc(mark);
			return Double.toString(val);
		} catch (FeatureCalcException | CreateException e) {
			throw new OperationFailedException(e);
		}
		
		
	}

	public MarkProvider getMarkProvider() {
		return markProvider;
	}

	public void setMarkProvider(MarkProvider markProvider) {
		this.markProvider = markProvider;
	}

}
