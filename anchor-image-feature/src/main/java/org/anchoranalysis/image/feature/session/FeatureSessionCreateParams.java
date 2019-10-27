package org.anchoranalysis.image.feature.session;

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


import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.SequentialSession;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.feature.histogram.FeatureHistogramParams;
import org.anchoranalysis.image.feature.objmask.FeatureObjMaskParams;
import org.anchoranalysis.image.feature.objmask.collection.FeatureObjMaskCollectionParams;
import org.anchoranalysis.image.feature.objmask.pair.FeatureObjMaskPairParams;
import org.anchoranalysis.image.feature.objmask.pair.merged.FeatureObjMaskPairMergedParams;
import org.anchoranalysis.image.feature.stack.FeatureStackParams;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

public class FeatureSessionCreateParams extends FeatureSession {

	private SequentialSession session;
	private ImageRes res;
	private NRGStackWithParams nrgStack=null;
	private ParamsFactory factory = new ParamsFactory();
	
	public class ParamsFactory {
		
		public FeatureCalcParams createParams() {
			return null;
		}
		
		public FeatureCalcParams createParams( ObjMask om ) {
			FeatureObjMaskParams params = new FeatureObjMaskParams(om);
			params.setNrgStack(nrgStack);
			return params;
		}
			
		public FeatureCalcParams createParams( ObjMask objMask1, ObjMask objMask2 ) {
			FeatureObjMaskPairParams params = new FeatureObjMaskPairParams(objMask1, objMask2);
			params.setNrgStack(nrgStack);
			return params;
		}
		
		public FeatureCalcParams createParams( ObjMaskCollection omc ) {
			FeatureObjMaskCollectionParams params = new FeatureObjMaskCollectionParams(omc);
			params.setNrgStack(nrgStack);
			return params;
		}
		
		public FeatureCalcParams createParams( ObjMask obj1, ObjMask obj2, ObjMask omMerged ) {
			FeatureObjMaskPairMergedParams params = new FeatureObjMaskPairMergedParams(obj1, obj2,omMerged);
			params.setNrgStack(nrgStack);
			return params;
		}
		
		public FeatureCalcParams createParams( Histogram hist ) {
			return new FeatureHistogramParams(hist, determineRes() );
		}
		
		public FeatureCalcParams createParams( NRGStack nrgStack ) {
			return new FeatureStackParams(nrgStack);
		}
	}


	public FeatureSessionCreateParams(FeatureList listFeatures) {
		super();
		this.session = new SequentialSession(listFeatures);
	}
	
	public ParamsFactory getParamsFactory() {
		return factory;
	}
	
	public void start( LogErrorReporter logger ) throws InitException {
		session.start( new FeatureInitParams(), new SharedFeatureSet(), logger );
	}
	
	public void start(FeatureInitParams featureInitParams, SharedFeatureSet sharedFeatureList, LogErrorReporter logger ) throws InitException {
		session.start(featureInitParams, sharedFeatureList, logger);
	}

	//featureStoreForTask.init(soImage, soFeature, cache);
	
	public KeyValueParams determineNrgParams() {
		return nrgStack.getParams();
	}
	
	
	
	
	/**
	 * For Params that require a resolution, this can be specified, or guessed from an NRGStack
	 * 
	 * @return
	 */
	public ImageRes determineRes() {
		if (res!=null) {
			return res;
		} else if (nrgStack!=null && nrgStack.getNrgStack().getNumChnl()>0) {
			return nrgStack.getDimensions().getRes();
		} else {
			return null;
		}
	}

	public ResultsVector calc() throws FeatureCalcException {
		return session.calc( factory.createParams() );
	}
	
	public ResultsVector calc( ObjMask om ) throws FeatureCalcException {
		return session.calc( factory.createParams(om) );
	}
		

	
	public ResultsVector calc( ObjMask objMask1, ObjMask objMask2 ) throws FeatureCalcException {
		return session.calc( factory.createParams(objMask1,objMask2) );
	}

	public ResultsVector calc( ObjMaskCollection omc ) throws FeatureCalcException {
		return session.calc( factory.createParams(omc) );
	}
	
	public ResultsVector calc(ObjMask obj1, ObjMask obj2, ObjMask omMerged) throws FeatureCalcException {
		return session.calc( factory.createParams(obj1,obj2,omMerged) );
	}
		
	public ResultsVector calc( Histogram hist ) throws FeatureCalcException {
		return session.calc( factory.createParams(hist) );
	}
	
	public ResultsVector calc( NRGStack nrgStack ) throws FeatureCalcException {
		return session.calc( factory.createParams(nrgStack) );
	}
	
	public String featureNameForIndex( int index ) {
		return session.getFeatureList().get(index).getFriendlyName();
	}
	
	/*
	 * Creates a sub-session that refers to a single feature within the session
	 */
	public FeatureSessionCreateParamsSingle createSingle( int index ) {
		return new FeatureSessionCreateParamsSingle(this, index);
	}
	
	public KeyValueParams getKeyValueParams() {
		return nrgStack.getParams();
	}
		
	public NRGStack getNrgStack() {
		if (nrgStack==null) {
			return null;
		}
		return nrgStack.getNrgStack();
	}

	public void setNrgStack(NRGStackWithParams nrgStack) {
		this.nrgStack = nrgStack;
	}

	public List<Feature> getFeatureList() {
		return session.getFeatureList();
	}

	public ImageRes getRes() {
		return res;
	}

	public void setRes(ImageRes res) {
		this.res = res;
	}

	public int numFeatures() {
		return session.numFeatures();
	}

	public ResultsVector calcSuppressErrors(FeatureCalcParams params,
			ErrorReporter errorReporter) throws FeatureCalcException {
		return session.calcSuppressErrors(params, errorReporter);
	}

	public ResultsVector calc(FeatureCalcParams params)
			throws FeatureCalcException {
		return session.calc(params);
	}

	public FeatureSessionCreateParamsSubsession createSubsession( FeatureCalcParams params ) throws CreateException {
		return new FeatureSessionCreateParamsSubsession(
			session.createSubsession(),
			getParamsFactory(),
			params
		);
	}
}
