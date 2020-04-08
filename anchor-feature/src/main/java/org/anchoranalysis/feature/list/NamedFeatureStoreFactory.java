package org.anchoranalysis.feature.list;

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
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

public class NamedFeatureStoreFactory {

	public static <T extends FeatureCalcParams> NamedFeatureStore<T> createNamedFeatureList( List<NamedBean<FeatureListProvider<T>>> listFeatureListProvider ) throws CreateException {
		
		try {
			NamedFeatureStore<T> out = new NamedFeatureStore<>();
			for( NamedBean<FeatureListProvider<T>> ni : listFeatureListProvider ) {
								
				// NOTE: Naming convention
				//  When a featureList contains a single item, we use the name of the featureList, rather than the feature 
				FeatureList<T> featureList = ni.getValue().create();
				
				if (featureList.size()==0) {
					continue;
				}
				
				FeatureListStoreUtilities.addFeatureListToStore( featureList, ni.getName(), out );
			}
			return out;
			
		} catch (BeanDuplicateException e) {
			throw new CreateException(e);
		}		
	}
}
