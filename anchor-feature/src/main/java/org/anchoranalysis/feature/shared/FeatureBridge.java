package org.anchoranalysis.feature.shared;

/*-
 * #%L
 * anchor-feature
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

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.bridge.BridgeElementException;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;

/**
 * Creates a FeatureList from a FeatureListProvider
 * 
 * @author Owen
 *
 */
class FeatureBridge implements IObjectBridge<NamedBean<FeatureListProvider>, FeatureList> {

	private SharedFeatureSet sharedFeatureSet;
	private SharedFeaturesInitParams so;
	private LogErrorReporter logger;
	
	public FeatureBridge(SharedFeatureSet sharedFeatureSet, SharedFeaturesInitParams so, LogErrorReporter logger ) {
		super();
		this.sharedFeatureSet = sharedFeatureSet;
		this.so = so;
		this.logger = logger;
	}

	@Override
	public FeatureList bridgeElement(NamedBean<FeatureListProvider> sourceObject)
			throws BridgeElementException {

		try {
			sourceObject.getValue().initRecursive(so,logger);
		} catch (InitException e1) {
			throw new BridgeElementException(e1);
		}
		
		try {
			FeatureList fl = sourceObject.getValue().create();
			
			sharedFeatureSet.addNoDuplicate(fl);
				
			return fl;
		} catch (CreateException e) {
			throw new BridgeElementException(e);
		}
	}

}
