package org.anchoranalysis.image.feature.bean.evaluator;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.provider.FeatureProvider;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.feature.session.FeatureSessionCreateParamsSingle;

public class FeatureEvaluatorNrgStack extends FeatureEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private FeatureProvider featureProvider;
	
	@BeanField @Optional
	private StackProvider stackProviderNRG;
	
	@BeanField @Optional
	private KeyValueParamsProvider keyValueParamsProvider;
	// END BEAN PROPERTIES

	private Feature feature;
	private NRGStackWithParams nrgStack;
		
	public FeatureSessionCreateParamsSingle createAndStartSession() throws OperationFailedException {
		return createAndStartSession(false);
	}
	
	// Must always be executed first
	public FeatureSessionCreateParamsSingle createAndStartSession( boolean recordTimes ) throws OperationFailedException {
	
		try {
			feature = featureProvider.create();
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
		
		if (feature==null) {
			throw new OperationFailedException("FeatureProvider returns null. A feature is required.");
		}
		
//		// Init our shared features
//		for( String key : sharedFeatureList.keys() ) {
//			Feature f;
//			try {
//				f = sharedFeatureList.getNull(key);
//				assert(f!=null);
//				f.init(paramsInit);
//			} catch (GetOperationFailedException | InitException e) {
//				throw new OperationFailedException(e);
//			}
//		}

		KeyValueParams kpv;
		if (keyValueParamsProvider!=null) {
			try {
				kpv = keyValueParamsProvider.create();
			} catch (CreateException e) {
				throw new OperationFailedException(e);
			}
		} else {
			kpv = new KeyValueParams();
		}
		

		try {
			nrgStack = null;
			
			if (stackProviderNRG!=null) {
				
				nrgStack = new NRGStackWithParams( stackProviderNRG.create() );
				nrgStack.setParams(kpv);
			}
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}

		
		SharedFeatureSet sharedFeatures = getSharedObjects().getSharedFeatureSet();
		//SharedFeatureSet sharedFeatures = new SharedFeatureSet();
		
		FeatureSessionCreateParamsSingle session = new FeatureSessionCreateParamsSingle(
			feature,
			sharedFeatures,
			kpv
		);
		session.setNrgStack(nrgStack);
		try {
			session.start( getLogger() );
		} catch (InitException e) {
			throw new OperationFailedException(e);
		}
		
		
		return session;
	}
	
	public FeatureProvider getFeatureProvider() {
		return featureProvider;
	}

	public void setFeatureProvider(FeatureProvider featureProvider) {
		this.featureProvider = featureProvider;
	}



	public StackProvider getStackProviderNRG() {
		return stackProviderNRG;
	}



	public void setStackProviderNRG(StackProvider stackProviderNRG) {
		this.stackProviderNRG = stackProviderNRG;
	}


	public KeyValueParamsProvider getKeyValueParamsProvider() {
		return keyValueParamsProvider;
	}


	public void setKeyValueParamsProvider(
			KeyValueParamsProvider keyValueParamsProvider) {
		this.keyValueParamsProvider = keyValueParamsProvider;
	}
}
