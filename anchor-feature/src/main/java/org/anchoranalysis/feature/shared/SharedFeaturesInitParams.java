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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.name.store.SharedObjects;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.list.FeatureListStoreUtilities;

public class SharedFeaturesInitParams extends BeanInitParams {

	// START: InitParams
	private KeyValueParamsInitParams soParams;
	// END: InitParams
	
	// START: Stores
	private NamedProviderStore<FeatureList<FeatureInput>> storeFeatureList;
	// END: Stores
	
	// START: Single Items
	private SharedFeatureMulti sharedFeatureSet;
	// END: Single Items

	private SharedFeaturesInitParams(SharedObjects so) {
		super();
		this.soParams = KeyValueParamsInitParams.create(so);
		
		storeFeatureList = so.getOrCreate(FeatureList.class);
		
		// We populate our shared features from our storeFeatureList
		sharedFeatureSet = new SharedFeatureMulti();
		FeatureListStoreUtilities.addFeatureListToStoreNoDuplicateDirectly(
			storeFeatureList,
			sharedFeatureSet
		);
	}
	
	public static SharedFeaturesInitParams create( SharedObjects so ) {
		return new SharedFeaturesInitParams(so);
	}
	
	
	/**
	 * Creates empty params
	 * 
	 * @param logger
	 * @return
	 */
	public static SharedFeaturesInitParams create( Logger logger ) {
		SharedObjects so = new SharedObjects(logger);
		return create(so);
	}
	
	public NamedProviderStore<FeatureList<FeatureInput>> getFeatureListSet() {
		return storeFeatureList;
	}
	
	public void populate(
		List<NamedBean<FeatureListProvider<FeatureInput>>> namedFeatureListCreator,
		Logger logger
	) throws OperationFailedException {
		
		assert( getFeatureListSet()!=null );
		try {
			for (NamedBean<FeatureListProvider<FeatureInput>> namedBean : namedFeatureListCreator) {
				namedBean.getItem().initRecursive(this, logger);
				addFeatureList(namedBean, logger);
			}
		} catch (InitException e) {
			throw new OperationFailedException(e);
		}
	}
	
	private void addFeatureList( NamedBean<FeatureListProvider<FeatureInput>> nb, Logger logger ) throws OperationFailedException {
		
		try {
			FeatureList<FeatureInput> fl = nb.getItem().create();
			String name = nb.getName();

			// If there's only one item in the feature list, then we set it as the custom
			//  name of teh feature
			if (fl.size()==1) {
				fl.get(0).setCustomName(name);
			}
			
			storeFeatureList.add( name, ()->fl );
			sharedFeatureSet.addNoDuplicate(fl);
						
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
	

	public KeyValueParamsInitParams getParams() {
		return soParams;
	}
	
	public SharedFeatureMulti getSharedFeatureSet() {
		return sharedFeatureSet;
	}
}
