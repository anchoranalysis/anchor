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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.params.FeatureInput;

/**
 * Prepends a string to each feature in the list
 * 
 * @author Owen Feehan
 *
 */
public class FeatureListProviderPrependName extends FeatureListProvider<FeatureInput> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private FeatureListProvider<FeatureInput> item;
	
	@BeanField
	private String prependString;
	// END BEAN PROPERTIES

	public static void setNewNameOnFeature( Feature<? extends FeatureInput> f, String existingName, String prependString ) {
		String nameNew = String.format("%s%s", prependString, existingName);
		f.setCustomName(nameNew);
	}
	
	@Override
	public FeatureList<FeatureInput> create() throws CreateException {

		FeatureList<FeatureInput> features = item.create();
		
		for( Feature<FeatureInput> f : features ) {
			String existingName = f.getFriendlyName();
			setNewNameOnFeature( f, existingName, prependString );
			
		}
		
		return features;
	}

	public String getPrependString() {
		return prependString;
	}

	public void setPrependString(String prependString) {
		this.prependString = prependString;
	}

	public FeatureListProvider<FeatureInput> getItem() {
		return item;
	}

	public void setItem(FeatureListProvider<FeatureInput> item) {
		this.item = item;
	}
}
