package org.anchoranalysis.feature.bean.list;

import org.anchoranalysis.bean.StringSet;

/*
 * #%L
 * anchor-feature
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
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;

public abstract class FeatureListProviderReferencedFeatures extends FeatureListProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField @Optional
	private StringSet referencesFeatureListCreator;	// Makes sure a particular feature list creator is evaluated
	// END BEAN PROPERITES
	
	private static void ensureReferencedFeaturesCalled( StringSet referencesFeatureListCreator, SharedFeaturesInitParams so ) throws InitException {
		if (referencesFeatureListCreator!=null && so!=null) {
			for( String s : referencesFeatureListCreator.set() ) {
				
				//System.out.printf("Fetching dependent referencesFeatureList '%s'", s);
				try {
					so.getFeatureListSet().getException(s);
					
					// TODO cached calculation list?
//					FeatureInitParams featureInitParams = new FeatureInitParams(pso.getSharedFeatureSet(), new CachedCalculationList());
//					if (keyValueParamsProvider!=null) {
//						featureInitParams.getListKeyValueParams().add( keyValueParamsProvider.create() );
//					}
//					
//					for( Feature f : item ) {
//						f.init(featureInitParams);
//					}
				} catch (GetOperationFailedException e) {
					throw new InitException(e);
				}
			}
		}	
	}
	
	@Override
	public void onInit(SharedFeaturesInitParams so)
			throws InitException {
		super.onInit(so);
		ensureReferencedFeaturesCalled( referencesFeatureListCreator, so );
	}
	
	public StringSet getReferencesFeatureListCreator() {
		return referencesFeatureListCreator;
	}

	public void setReferencesFeatureListCreator(
			StringSet referencesFeatureListCreator) {
		this.referencesFeatureListCreator = referencesFeatureListCreator;
	}
}
