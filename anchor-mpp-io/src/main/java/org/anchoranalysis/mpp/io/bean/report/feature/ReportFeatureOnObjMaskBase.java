package org.anchoranalysis.mpp.io.bean.report.feature;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;

/*-
 * #%L
 * anchor-mpp-io
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureInput;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.bean.provider.ObjMaskProvider;
import org.anchoranalysis.image.feature.bean.evaluator.FeatureEvaluator;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

public abstract class ReportFeatureOnObjMaskBase<T extends FeatureInput> extends ReportFeatureForSharedObjects {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private ObjMaskProvider objs;
	
	@BeanField
	private FeatureEvaluator<T> featureEvaluator;
	
	@BeanField
	private String title;
	// END BEAN PROPERTIES
	
	@Override
	public String genFeatureStrFor(MPPInitParams so, LogErrorReporter logger)
			throws OperationFailedException {

		try {
			objs.initRecursive( so.getImage(), logger );
			featureEvaluator.initRecursive( so.getFeature(), logger );
		} catch (InitException e) {
			throw new OperationFailedException(e);
		}
		
		try {
			ObjMaskCollection objsCollection = objs.create();
						
			FeatureCalculatorSingle<T> session = featureEvaluator.createAndStartSession();
			double val = calcFeatureOn( objsCollection, session );
			return Double.toString(val);
			
		} catch (FeatureCalcException | CreateException e) {
			throw new OperationFailedException(e);
		}
	}
	
	protected abstract double calcFeatureOn( ObjMaskCollection objs, FeatureCalculatorSingle<T> session ) throws FeatureCalcException;
	
	@Override
	public boolean isNumeric() {
		return true;
	}

	@Override
	public String genTitleStr() throws OperationFailedException {
		return title;
	}
	
	public ObjMaskProvider getObjs() {
		return objs;
	}

	public void setObjs(ObjMaskProvider objs) {
		this.objs = objs;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public FeatureEvaluator<T> getFeatureEvaluator() {
		return featureEvaluator;
	}


	public void setFeatureEvaluator(FeatureEvaluator<T> featureEvaluator) {
		this.featureEvaluator = featureEvaluator;
	}
}
