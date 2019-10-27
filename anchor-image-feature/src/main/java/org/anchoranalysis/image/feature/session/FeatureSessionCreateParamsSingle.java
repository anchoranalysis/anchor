package org.anchoranalysis.image.feature.session;

import org.anchoranalysis.core.error.InitException;

/*
 * #%L
 * anchor-image-feature
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


import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.feature.evaluator.EvaluateSingleObjMask;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

/**
 * Creates the parameters before passing them to a SequentialSession
 * 
 * This class is for when there is guaranteed to be only one Result to returned. So it returns them as double
 *   rather than as ResultsVector
 *   
 * It can be self-contained (initialised by a feature). Or it can be an index of a FeatureSessionCreateParams.
 * 
 * Note than when start() is called. All features are started() on the delegate. The subsetting only applies to calculations.
 * 
 * @author Owen Feehan
 *
 */
public class FeatureSessionCreateParamsSingle extends FeatureSession implements EvaluateSingleObjMask {

	/**
	 * From where the single feature is calculated
	 */
	private FeatureSessionCreateParams delegate;
	
	/**
	 * The index that is referred to in the delegate FeatureSessionCreateParams (0 if there's only one item)
	 */
	private int index;
	
	private FeatureInitParams paramsInit;
	
	/**
	 * Features shared in the session
	 */
	private SharedFeatureSet sharedFeatures;
	
	/**
	 * We set up a parent, that contains a single Feature. This is self-contained case.
	 * 
	 * @param feature
	 */
	public FeatureSessionCreateParamsSingle(
		Feature feature,
		SharedFeatureSet sharedFeatures
	) {
		this(feature,sharedFeatures,null);
	}
		
	/**
	 * We set up a parent, that contains a single Feature. This is self-contained case.
	 * 
	 * @param listKeyValueParams can be NULL
	 */
	public FeatureSessionCreateParamsSingle(
		Feature feature,
		SharedFeatureSet sharedFeatures,
		KeyValueParams keyValueParams
	) {
		this(feature, new FeatureInitParams(keyValueParams) );
		this.sharedFeatures = sharedFeatures;
	}
	
	
	public FeatureSessionCreateParamsSingle(
		Feature feature,
		FeatureInitParams paramsInit
	) {
		this.paramsInit = paramsInit;
		this.index = 0;
		delegate = new FeatureSessionCreateParams( new FeatureList(feature) );
	}
	
	/**
	 * We take an existing FeatureSessionCreateParams and refer to a single item within it
	 * 
	 * @param parent
	 * @param index
	 */
	public FeatureSessionCreateParamsSingle( FeatureSessionCreateParams parent, int index ) {
		this.index = index;
		this.delegate = parent;
	}
	
	/**
	 * Starts with the prior passed Init.
	 * 
	 * If there's a non-null nrgStack it is added
	 * 
	 * @throws InitException
	 */
	public void start( LogErrorReporter logger ) throws InitException {
		if (delegate.getNrgStack()!=null) {
			paramsInit.setNrgStack(delegate.getNrgStack());
		}
		delegate.start(paramsInit, sharedFeatures, logger );
	}

	private void checkSingleFeature() throws FeatureCalcException {
		if (delegate.numFeatures()!=1) {
			throw new FeatureCalcException("Number of features associated with session != 1");
		}
	}
	
	public double calc() throws FeatureCalcException {
		checkSingleFeature();
		return delegate.calc().get(index);
	}
	
	@Override
	public double calc( ObjMask om) throws FeatureCalcException {
		checkSingleFeature();
		return delegate.calc(om).get(index);
	}
	
	public double calc( ObjMask objMask1, ObjMask objMask2 ) throws FeatureCalcException {
		checkSingleFeature();
		return delegate.calc(objMask1,objMask2).get(index);
	}
	
	public double calc( ObjMaskCollection objs ) throws FeatureCalcException {
		checkSingleFeature();
		return delegate.calc(objs).get(index);
	}
	
	public double calc(ObjMask obj1, ObjMask obj2, ObjMask omMerged) throws FeatureCalcException {
		checkSingleFeature();
		return delegate.calc(obj1,obj2,omMerged).get(index);
	}

	public double calc(Histogram hist) throws FeatureCalcException {
		return delegate.calc(hist).get(index);
	}
	
	public double calc( NRGStack nrgStack ) throws FeatureCalcException {
		return delegate.calc(nrgStack).get(index);
	}

	public double calc(FeatureCalcParams params)
			throws FeatureCalcException {
		return delegate.calc(params).get(index);
	}
	
	public void setNrgStack(NRGStackWithParams nrgStack) {
		delegate.setNrgStack(nrgStack);
	}
	
	public String featureName() {
		return delegate.featureNameForIndex(index);
	}

	public void setRes(ImageRes res) {
		delegate.setRes(res);
	}

	public Feature getFeature() {
		return delegate.getFeatureList().get(index);
	}

	public FeatureInitParams getParamsInit() {
		return paramsInit;
	}

	public SharedFeatureSet getSharedFeatures() {
		return sharedFeatures;
	}

	public FeatureSessionCreateParams getDelegate() {
		return delegate;
	}


}
