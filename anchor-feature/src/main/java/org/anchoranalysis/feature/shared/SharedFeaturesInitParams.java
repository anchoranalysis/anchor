package org.anchoranalysis.feature.shared;

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


import java.util.List;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.init.params.BeanInitParams;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.bean.store.BeanStoreAdder;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.list.FeatureListStoreUtilities;

public class SharedFeaturesInitParams extends BeanInitParams {

	// START: InitParams
	private KeyValueParamsInitParams soParams;
	// END: InitParams
	
	// START: Stores
	private NamedProviderStore<FeatureList> storeFeatureList;
	// END: Stores
	
	// START: Single Items
	private SharedFeatureSet sharedFeatureSet;
	// END: Single Items

	private SharedFeaturesInitParams(SharedObjects so) {
		super();
		this.soParams = KeyValueParamsInitParams.create(so);
		
		storeFeatureList = so.getOrCreate(FeatureList.class);
		
		// We populate our shared features from our storeFeatureList
		sharedFeatureSet = new SharedFeatureSet();
		FeatureListStoreUtilities.addFeatureListToStoreNoDuplicateDirectly(storeFeatureList, sharedFeatureSet);
	}
	
	public static SharedFeaturesInitParams create( SharedObjects so ) {
		return new SharedFeaturesInitParams(so);
	}
	
	
	/**
	 * Creates empty params
	 * @param logErrorReporter
	 * @return
	 */
	public static SharedFeaturesInitParams create( LogErrorReporter logErrorReporter ) {
		SharedObjects so = new SharedObjects(logErrorReporter);
		return create(so);
	}
	
	public NamedProviderStore<FeatureList> getFeatureListSet() {
		return storeFeatureList;
	}
	
	public void addAll( List<NamedBean<FeatureListProvider>> namedFeatureListCreator, LogErrorReporter logger ) throws OperationFailedException {
		
		assert( getFeatureListSet()!=null );
		BeanStoreAdder.addPreserveNameEmbedded(
			namedFeatureListCreator,
			getFeatureListSet(),
			new FeatureBridge(getSharedFeatureSet(), this, logger )
		);
	}

	public KeyValueParamsInitParams getParams() {
		return soParams;
	}
	
	public SharedFeatureSet getSharedFeatureSet() {
		return sharedFeatureSet;
	}
}
