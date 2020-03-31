package org.anchoranalysis.feature.bean.list;

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
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.operator.Constant;

/**
 * Loads a set of KeyValueParams as features
 * 
 * @author Owen Feehan
 *
 */
public class FeatureListProviderKeyValueParams extends FeatureListProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField 
	private String collectionID = "";
	// END BEAN PROPERTIES

	@Override
	public FeatureList create() throws CreateException {

		FeatureList out = new FeatureList();
		
		KeyValueParamsInitParams soParams = getSharedObjects().getParams();
		
		try {
			KeyValueParams kpv = soParams.getNamedKeyValueParamsCollection().getException(collectionID);
			
			for( String key : kpv.keySet() ) {
				
				double val = kpv.getPropertyAsDouble(key);
				
				Constant featureNew = new Constant();
				featureNew.setCustomName( key );
				featureNew.setValue( val );
				
				out.add(featureNew);
			}
			
		} catch (NamedProviderGetException e) {
			throw new CreateException(e);
		}
		
		return out;
	}

	public String getCollectionID() {
		return collectionID;
	}

	public void setCollectionID(String collectionID) {
		this.collectionID = collectionID;
	}

}
